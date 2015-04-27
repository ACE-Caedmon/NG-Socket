package com.ace.ng.codec;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.codec.binary.BinaryEncryptUtil;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.javassit.HandlerPropertySetter;
import com.ace.ng.dispatch.javassit.NoOpHandlerPropertySetter;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.utils.CommonUtils;
import com.google.protobuf.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2015/4/25.
 */
public class BinaryCodecApi {
    private static Logger log= LoggerFactory.getLogger(BinaryCodecApi.class);
    private static Map<Short,HandlerPropertySetter> handlerPropertySetterMap=new HashMap<Short,HandlerPropertySetter>(100);
    private static ClassPool classPool=ClassPool.getDefault();
    public static class EncryptBinaryPacket {
        public byte[] content;
        public int passwordIndex;
        public boolean isEncrypt;
    }
    public static EncryptBinaryPacket encodeContent(OutputPacket output,Channel channel){
        boolean isEncrypt=false;
        ISession session=channel.attr(Session.SESSION_KEY).get();
        if(session.containsAttribute(Session.NEED_ENCRYPT)){
            isEncrypt=session.getAttribute(Session.NEED_ENCRYPT);
        }
        ByteBuf buf=PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeShort(output.getCmd());
        CustomBuf content=new ByteCustomBuf(buf);
        output.getOutput().encode(content);
        byte[] dst=new byte[buf.readableBytes()];
        buf.readBytes(dst);
        int passwordIndex=new Random().nextInt(256);
        if(isEncrypt){
            List<Short> passports=session.getAttribute(Session.PASSPORT);
            short passport=passports.get(passwordIndex);//根据索引获取密码
            String secretKey=channel.attr(Session.SECRRET_KEY).get();
            BinaryEncryptUtil.encode(dst, dst.length, secretKey, passport);//加密
        }
        EncryptBinaryPacket packet=new EncryptBinaryPacket();
        packet.content=dst;
        packet.passwordIndex=passwordIndex;
        packet.isEncrypt=isEncrypt;
        return packet;
    }
    public static ByteBuf decodeContent(BinaryPacket packet,ChannelHandlerContext ctx){
        ByteBuf content=packet.getContent();
        short length=(short)content.readableBytes();
        int hasReadLength=0;
        boolean isEncrypt=content.readBoolean();
        hasReadLength+=1;
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        byte entryptOffset=content.readByte();
        hasReadLength+=1;
        ByteBuf bufForDecode= PooledByteBufAllocator.DEFAULT.buffer();//用来缓存一条报文的ByteBuf
        if(isEncrypt){
            byte[] dst=new byte[length-hasReadLength];//存储包体
            content.readBytes(dst);//读取包体内容
            short index = (short) (entryptOffset < 0 ? (256 + entryptOffset): entryptOffset);//获取密码表索引
            List<Short> passportList=session.getAttribute(Session.PASSPORT);//得到密码表集合
            short passport=passportList.get(index);//得到密码
            String secretKey=ctx.channel().attr(Session.SECRRET_KEY).get();
            BinaryEncryptUtil.decode(dst, dst.length, secretKey, passport);//解密
            bufForDecode.writeBytes(dst);
        }else {
            bufForDecode.writeBytes(content, length - hasReadLength);
        }
        return bufForDecode;
    }
    public static CmdHandler decodeCmdHandler(CmdFactoryCenter cmdFactoryCenter,ByteBuf bufForDecode){
        short cmd=bufForDecode.readShort();
        CmdHandler<?> handler=cmdFactoryCenter.getCmdHandler(cmd);
        if(handler!=null){
            try {
                CustomBuf contentBuf=new ByteCustomBuf(bufForDecode);//将ByteBuf作为构造参数传入自定义的装饰器
                HandlerPropertySetter propertySetter=getHandlerPropertySetter(cmd, handler);
                propertySetter.setHandlerProperties(contentBuf,handler);
                if(bufForDecode.isReadable()){
                    log.warn("数据包内容未读完:cmd={},remain={}", cmd, bufForDecode.readableBytes());
                }
            } catch (Exception e) {
                bufForDecode.skipBytes(bufForDecode.readableBytes());
                throw new RuntimeException("解码异常(cmd = "+cmd+")",e);

            }
        }else{
            bufForDecode.skipBytes(bufForDecode.readableBytes());
            throw new NullPointerException("未知指令(cmd = "+cmd+")");
        }
        return handler;
    }
    private static HandlerPropertySetter getHandlerPropertySetter(Short cmd,CmdHandler<?> handler) throws  Exception{
        //构造HandlerPropertySetter
        HandlerPropertySetter handlerPropertySetter=handlerPropertySetterMap.get(cmd);
        if(handlerPropertySetter==null){
            String handlerClassName=handler.getClass().getName();
            CtClass tempHandler=classPool.get(handlerClassName);
            CtClass handlerPropertySetterClass=classPool.getAndRename(NoOpHandlerPropertySetter.class.getName(),
                    NoOpHandlerPropertySetter.class.getName()+"Proxy$"+cmd );
            synchronized (handlerPropertySetterClass){
                //双重检查
                if((handlerPropertySetter=handlerPropertySetterMap.get(cmd))==null){
                    CtMethod setPropertiesMethod=handlerPropertySetterClass.
                            getDeclaredMethod("setHandlerProperties");
                    if(CommonUtils.hasDeclaredMethod(handler.getClass(), "decode", CustomBuf.class)){//如果自己实现了decode方法,则不采用自动set方式
                        setPropertiesMethod.insertAfter("$2.decode($1);");
                    }else{//自动调用set方法解码
                        addAutoDecodeSrc(setPropertiesMethod,tempHandler,handlerClassName);
                    }
                    handlerPropertySetter=(HandlerPropertySetter)
                            handlerPropertySetterClass.toClass().newInstance();
                    handlerPropertySetterMap.put(cmd,handlerPropertySetter);
                }
            }
        }
        return handlerPropertySetter;
    }
    private static void addAutoDecodeSrc(CtMethod setPropertiesMethod,CtClass tempHandler,String handlerClassName) throws Exception{
        CtField[] protocolFields=tempHandler.getDeclaredFields();
        setPropertiesMethod.addLocalVariable("h",tempHandler);
        setPropertiesMethod.insertAfter(" h=((" + handlerClassName + ")$2);");
        String setHead="h.set";
        for(CtField f:protocolFields){
            int modifier=f.getModifiers();
            if (!Modifier.isStatic(modifier)&&!Modifier.isFinal(modifier)){
                NotDecode notDecode=(NotDecode)f.getAnnotation(NotDecode.class);
                if(notDecode==null||!notDecode.value()){//需要解码的字段
                    CtClass typeClass=f.getType();
                    String typeSimpleName=typeClass.getSimpleName();
                    String typeAllName=typeClass.getName();
                    String setMethodString=setHead+CommonUtils.firstToUpperCase(f.getName());
                    switch (typeSimpleName){
                        case "long":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readLong());");
                            break;
                        case "int":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readInt());");
                            break;
                        case "short":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readShort());");
                            break;
                        case "byte":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readByte());");
                            break;
                        case "boolean":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readBoolean();");
                            break;
                        case "float":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readFloat());");
                            break;
                        case "double":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readDouble());");
                            break;
                        case "String":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readString());");
                            break;
                        default:
                            String builderClassFullName=typeAllName.replaceAll("\\$",".");
                            if(AbstractMessage.Builder.class.isAssignableFrom(Class.forName(typeAllName))){
                                int lastIndex=builderClassFullName.lastIndexOf("Builder");
                                String protoClassName=builderClassFullName.substring(0, lastIndex);
                                protoClassName=protoClassName.replaceAll("\\$",".");

                                setPropertiesMethod.insertAfter(setMethodString+"(("+builderClassFullName+")$1.readProtoBuf("+protoClassName+"newBuilder()));");
                            }else{
                                throw new UnsupportedOperationException("不支持的自动解码字段类型:" + typeSimpleName);
                            }

                    }

                }
            }

        }
    }
}
