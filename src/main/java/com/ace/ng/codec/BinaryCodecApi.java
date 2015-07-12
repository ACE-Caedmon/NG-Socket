package com.ace.ng.codec;
import com.ace.ng.codec.binary.BinaryEncryptUtil;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.utils.CommonUtils;
import com.google.protobuf.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by Administrator on 2015/4/25.
 */
public class BinaryCodecApi {
    private static Logger log= LoggerFactory.getLogger(BinaryCodecApi.class);
    private static Map<String,ProtoCacheElement> protoElementCache =new HashMap<>();
    public static class EncryptBinaryPacket {
        public byte[] content;
        public int passwordIndex;
        public boolean isEncrypt;
    }
    public static class ProtoCacheElement{
        public String builderClassName;
        public String protoClassName;
    }
    public static EncryptBinaryPacket encodeContent(OutputPacket output,Channel channel){
        boolean isEncrypt=false;
        ISession session=channel.attr(Session.SESSION_KEY).get();
        if(session.containsAttribute(Session.NEED_ENCRYPT)){
            isEncrypt=session.getAttribute(Session.NEED_ENCRYPT);
        }
        ByteBuf buf=PooledByteBufAllocator.DEFAULT.buffer();
        buf.writeInt(output.getCmd());
        DataBuffer content=new ByteDataBuffer(buf);
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
        int length=content.readableBytes();
        int hasReadLength=0;
        //是否加密
        boolean isEncrypt=content.readBoolean();
        hasReadLength+=1;
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        byte entryptOffset=content.readByte();
        hasReadLength+=1;
        ByteBuf result= PooledByteBufAllocator.DEFAULT.buffer();//.DEFAULT.buffer();//用来缓存一条报文的ByteBuf
        if(isEncrypt){
            byte[] dst=new byte[length-hasReadLength];//存储包体
            content.readBytes(dst);//读取包体内容
            short index = (short) (entryptOffset < 0 ? (256 + entryptOffset): entryptOffset);//获取密码表索引
            List<Short> passportList=session.getAttribute(Session.PASSPORT);//得到密码表集合
            short passport=passportList.get(index);//得到密码
            String secretKey=ctx.channel().attr(Session.SECRRET_KEY).get();
            BinaryEncryptUtil.decode(dst, dst.length, secretKey, passport);//解密
            result.writeBytes(dst);
        }else {
            result.writeBytes(content, length - hasReadLength);
        }
        return result;
    }
//    private static void addAutoDecodeSrc(CtMethod setPropertiesMethod,CtClass tempHandler,String handlerClassName) throws Exception{
//        CtField[] protocolFields=tempHandler.getDeclaredFields();
//        setPropertiesMethod.addLocalVariable("h",tempHandler);
//        setPropertiesMethod.insertAfter(" h=((" + handlerClassName + ")$2);");
//        String setHead="h.set";
//        for(CtField f:protocolFields){
//            int modifier=f.getModifiers();
//            if (!Modifier.isStatic(modifier)&&!Modifier.isFinal(modifier)){
//                NotDecode notDecode=(NotDecode)f.getAnnotation(NotDecode.class);
//                if(notDecode==null||!notDecode.value()){//需要解码的字段
//                    CtClass typeClass=f.getType();
//                    String typeSimpleName=typeClass.getSimpleName();
//                    String typeAllName=typeClass.getName();
//                    String setMethodString=setHead+CommonUtils.firstToUpperCase(f.getName());
//                    switch (typeSimpleName){
//                        case "long":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readLong());");
//                            break;
//                        case "int":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readInt());");
//                            break;
//                        case "short":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readShort());");
//                            break;
//                        case "byte":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readByte());");
//                            break;
//                        case "boolean":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readBoolean());");
//                            break;
//                        case "float":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readFloat());");
//                            break;
//                        case "double":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readDouble());");
//                            break;
//                        case "String":
//                            setPropertiesMethod.insertAfter(setMethodString+"($1.readString());");
//                            break;
//                        default:
//                            // builderClassName Builder的ClassName
//                            //protoClassName Proto的ClassName
//                            String builderClassName=null;
//                            boolean firstProto=true;
//                            String protoClassName=null;
//                            if(protoElementCache.containsKey(typeAllName)){
//                                builderClassName= protoElementCache.get(typeAllName).builderClassName;
//                                protoClassName=protoElementCache.get(typeAllName).protoClassName;
//                                firstProto=false;
//                            }else{
//                                builderClassName=typeAllName.replaceAll("\\$",".");
//                                if(AbstractMessage.Builder.class.isAssignableFrom(Class.forName(typeAllName))) {
//                                    int lastIndex = builderClassName.lastIndexOf("Builder");
//                                    protoClassName = builderClassName.substring(0, lastIndex);
//                                    protoClassName = protoClassName.replaceAll("\\$", ".");
//                                    firstProto=true;
//                                }else{
//                                    throw new UnsupportedOperationException("不支持的自动解码字段类型:" + typeSimpleName);
//                                }
//                            }
//                            setPropertiesMethod.insertAfter(setMethodString+"(("+builderClassName+")$1.readProtoBuf("+protoClassName+"newBuilder()));");
//                            if(firstProto){
//                                ProtoCacheElement element=new ProtoCacheElement();
//                                element.builderClassName=builderClassName;
//                                element.protoClassName=protoClassName;
//                                protoElementCache.put(typeAllName, element);
//                            }
//                    }
//
//                }
//            }
//
//        }
//    }
}
