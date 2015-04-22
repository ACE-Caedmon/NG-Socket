package com.ace.ng.codec.encrypt;

import com.ace.ng.codec.ByteCustomBuf;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.NotDecode;
import com.ace.ng.codec.binary.BinaryEncryptUtil;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.constant.VarConst;
import com.ace.ng.dispatch.javassit.HandlerPropertySetter;
import com.ace.ng.dispatch.javassit.NoOpHandlerPropertySetter;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.HandlerFactory;
import com.ace.ng.session.ISession;
import com.ace.ng.utils.CommonUtils;
import com.google.protobuf.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOption;
import io.netty.handler.codec.MessageToMessageDecoder;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网络数据报文解码处理器
 * @author Chenlong
 * 协议格式<br>
 *     <table  border frame="box">
 *         <tr>
 *             <th style="text-align:center"></th>
 *             <th style="text-align:center">包长</th>
 *             <th style="text-align:center">是否加密</th>
 *             <th style="text-align:center">密码表索引</th>
 *             <th style="text-align:center">自增ID</th>
 *             <th style="text-align:center">指令ID(cmd)</th>
 *             <th style="text-align:center">消息体(MessageHandler中自定义内容)</th>
 *         </tr>
 *         <tr>
 *             <td>数据类型</td>
 *             <td style="text-align:center">short</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">int</td>
 *             <td style="text-align:center">short</td>
 *             <td style="text-align:center">byte[]</td>
 *         </tr>
 *             <td>每部分字节数</td>
 *             <td style="text-align:center">2</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">4</td>
 *             <td style="text-align:center">2</td>
 *             <td style="text-align:center">根据消息体内容计算</td>
 *         </tr>
 *         <tr>
 *             <td style="text-align:center">是否加密</td>
 *             <td colspan="3" style="text-align:center">未加密部分</td>
 *             <td colspan="3" style="text-align:center">加密部分</td>
 *         </tr>
 *     </table>
 * */
public class BinaryEncryptDecoder extends MessageToMessageDecoder<BinaryPacket> {
	private static final Logger log =LoggerFactory.getLogger(BinaryEncryptDecoder.class);
	private boolean incred=false;//是否已自增
    private HandlerFactory handlerFactory;
    private static Map<Short,HandlerPropertySetter> handlerPropertySetterMap=new HashMap<Short,HandlerPropertySetter>(100);
    private static ClassPool classPool=ClassPool.getDefault();
    public BinaryEncryptDecoder(HandlerFactory handlerFactory){
        this.handlerFactory=handlerFactory;
    }
	/**
     * 负责对数据包进行解码
	 * @param ctx 对应Channel的上下文
	 * @param packet 数据包
	 * @param out 输出事件对象
	 * */
	@Override
	protected void decode(ChannelHandlerContext ctx, BinaryPacket packet,
			List<Object> out) throws Exception {
        ByteBuf content=packet.getContent();
		short length=(short)content.readableBytes();
        int hasReadLength=0;
        boolean isEncrypt=content.readBoolean();
        hasReadLength+=1;
        ISession session=ctx.channel().attr(VarConst.SESSION_KEY).get();
        byte entryptOffset=content.readByte();
        hasReadLength+=1;
        ByteBuf bufForDecode= PooledByteBufAllocator.DEFAULT.buffer();//用来缓存一条报文的ByteBuf
        if(isEncrypt){
            byte[] dst=new byte[length-hasReadLength];//存储包体
            content.readBytes(dst);//读取包体内容
            short index = (short) (entryptOffset < 0 ? (256 + entryptOffset): entryptOffset);//获取密码表索引
            List<Short> passportList=(List<Short>)session.getVar(VarConst.PASSPORT);//得到密码表集合
            short passport=passportList.get(index);//得到密码
            String secretKey=ctx.attr(VarConst.SECRRET_KEY).get();
            BinaryEncryptUtil.decode(dst, dst.length, secretKey, passport);//解密
            bufForDecode.writeBytes(dst);
        }else{
            bufForDecode.writeBytes(content,length-hasReadLength);
        }
        int ci=bufForDecode.readInt();//获取消息中的自增ID
        if(session.containsVar(VarConst.INCREMENT)){//如果已存在自增ID
            int si=(Integer)session.getVar(VarConst.INCREMENT);
            if(ci==si){//判断客户端传送自增ID是否与服务器相等
                if(!incred){
                    si=ci+1;//自增
                    session.setVar(VarConst.INCREMENT, si);
                    incred=true;
                }
            }else{
                log.error("自增ID不合法:ci ={},si={}", ci, si);
                bufForDecode.skipBytes(bufForDecode.readableBytes());
                return;
            }
        }else{
            session.setVar(VarConst.INCREMENT, ci + 1);
            incred=true;
        }
        short cmd=bufForDecode.readShort();
        CmdHandler<?> handler=handlerFactory.getHandler(cmd);
        if(handler!=null){
            try {
                CustomBuf contentBuf=new ByteCustomBuf(bufForDecode);//将ByteBuf作为构造参数传入自定义的装饰器
                HandlerPropertySetter propertySetter=getHandlerPropertySetter(cmd, handler);
                propertySetter.setHandlerProperties(contentBuf,handler);
                //handler.decode(contentBuf);
                if(bufForDecode.isReadable()){
                    log.warn("数据包内容未读完:cmd={},remain={}", cmd, bufForDecode.readableBytes());
                }
            } catch (Exception e) {
                log.error("解码异常:cmd={}", cmd, e);
                bufForDecode.skipBytes(bufForDecode.readableBytes());
            }finally{
                incred=false;//一条消息处理完就把自增标识重置一次
                out.add(handler);
            }
        }else{
            log.error("未知指令:cmd ={},length={}", cmd, bufForDecode.readableBytes());
            bufForDecode.skipBytes(bufForDecode.readableBytes());
        }
        bufForDecode.release();

	}

    private HandlerPropertySetter getHandlerPropertySetter(Short cmd,CmdHandler<?> handler) throws  Exception{
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
    private void addAutoDecodeSrc(CtMethod setPropertiesMethod,CtClass tempHandler,String handlerClassName) throws Exception{
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
                        case "Builder":
                            setPropertiesMethod.insertAfter(setMethodString+"($1.readProtoBuf());");
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
