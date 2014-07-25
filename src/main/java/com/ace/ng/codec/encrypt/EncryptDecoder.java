package com.ace.ng.codec.encrypt;

import com.ace.ng.codec.ByteCustomBuf;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.NotDecode;
import com.ace.ng.constant.VarConst;
import com.ace.ng.dispatch.HandlerPropertySetter;
import com.ace.ng.dispatch.MessageHandlerCreator;
import com.ace.ng.dispatch.MessageHandlerFactory;
import com.ace.ng.dispatch.NoOpHandlerPropertySetter;
import com.ace.ng.dispatch.message.MessageHandler;
import com.ace.ng.dispatch.message.TCPHandlerFactory;
import com.ace.ng.session.ISession;
import com.ace.ng.utils.ClassUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

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
public class EncryptDecoder extends ReplayingDecoder<Void>{
	private Logger logger=LoggerFactory.getLogger(EncryptDecoder.class);
	private boolean incred=false;//是否已自增
    private TCPHandlerFactory handlerFactory;
    private static Map<Short,HandlerPropertySetter> handlerPropertySetterMap=new HashMap<Short,HandlerPropertySetter>(100);
    private static ClassPool classPool=ClassPool.getDefault();
    public EncryptDecoder(TCPHandlerFactory handlerFactory){
        this.handlerFactory=handlerFactory;
    }
	/**
     * 负责对数据包进行解码
	 * @param ctx 对应Channel的上下文
	 * @param in 输入流
	 * @param out 输出事件对象
	 * */
	@SuppressWarnings("unchecked")
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in,
			List<Object> out) throws Exception {
		short length=in.readShort();
			int hasReadLength=0;
			boolean isEncrypt=in.readBoolean();
			hasReadLength+=1;
			ISession session=ctx.channel().attr(VarConst.SESSION_KEY).get();
			byte entryptOffset=in.readByte();
			hasReadLength+=1;
			ByteBuf bufForDecode=Unpooled.buffer(length-hasReadLength);//用来缓存一条报文的ByteBuf
			if(isEncrypt){
				byte[] dst=new byte[length-hasReadLength];//存储包体
				in.readBytes(dst);//读取包体内容
				short index = (short) (entryptOffset < 0 ? (256 + entryptOffset): entryptOffset);//获取密码表索引
				List<Short> passportList=(List<Short>)session.getVar(VarConst.PASSPORT);//得到密码表集合
				short passport=passportList.get(index);//得到密码
				EncryptUtil.decode(dst, dst.length, EncryptUtil.KEY, passport);//解密
				bufForDecode.writeBytes(dst);
			}else{
				bufForDecode.writeBytes(in,length-hasReadLength);
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
					logger.error("自增ID不合法( ci = "+ci+",si = "+si+")");
					bufForDecode.skipBytes(bufForDecode.readableBytes());
				}
			}else{
				session.setVar(VarConst.INCREMENT, ci + 1);
				incred=true;
			}
			short cmd=bufForDecode.readShort();
            MessageHandler<?> handler=handlerFactory.getHandler(cmd);
            if(handler!=null){
                try {
                    CustomBuf contentBuf=new ByteCustomBuf(bufForDecode);//将ByteBuf作为构造参数传入自定义的装饰器
                    HandlerPropertySetter propertySetter=getHandlerPropertySetter(cmd, handler);
                    propertySetter.setHandlerProperties(contentBuf,handler);
                    //handler.decode(contentBuf);
                    if(bufForDecode.isReadable()){
                        logger.warn("报文还有内容没有读取(cmd ="+cmd+",remainLength = "+bufForDecode.readableBytes()+" )");
                    }
                } catch (Exception e) {
                    logger.error("解码异常(cmd = "+cmd+")",e);
                    bufForDecode.skipBytes(bufForDecode.readableBytes());
                }finally{
                    incred=false;//一条消息处理完就把自增标识重置一次
                    out.add(handler);
                }
            }else{
                logger.error("未知指令( cmd = "+cmd+",length = "+bufForDecode.readableBytes()+")");
                bufForDecode.skipBytes(bufForDecode.readableBytes());
            }


	}
    private static String firstToUpperCase(String s){
        return s.replaceFirst( s.substring(0, 1), s.substring(0, 1).toUpperCase());
    }

    private HandlerPropertySetter getHandlerPropertySetter(Short cmd,MessageHandler handler) throws  Exception{
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
                    if(ClassUtils.hasDeclaredMethod(handler.getClass(),"decode",CustomBuf.class)){//如果自己实现了decode方法,则不采用自动set方式
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
                    switch (typeSimpleName){
                        case "long":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readLong());");
                            break;
                        case "int":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readInt());");
                            break;
                        case "short":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readShort());");
                            break;
                        case "byte":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readByte());");
                            break;
                        case "boolean":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readBoolean();");
                            break;
                        case "float":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readFloat());");
                            break;
                        case "double":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readDouble());");
                            break;
                        case "String":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readString());");
                            break;
                        case "Builder":
                            setPropertiesMethod.insertAfter(setHead+firstToUpperCase(f.getName())+"($1.readToProtoBuf());");
                            break;
                        default:
                            logger.error("不支持的解码字段类型 "+typeSimpleName );
                            break;
                    }
                }
            }

        }
    }
}
