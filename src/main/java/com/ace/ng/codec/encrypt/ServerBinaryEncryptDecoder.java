package com.ace.ng.codec.encrypt;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
 public class ServerBinaryEncryptDecoder extends MessageToMessageDecoder<BinaryPacket> {
	private static final Logger log =LoggerFactory.getLogger(ServerBinaryEncryptDecoder.class);
	private boolean incred=false;//是否已自增
    private CmdFactoryCenter cmdFactoryCenter;
    public ServerBinaryEncryptDecoder(CmdFactoryCenter cmdFactoryCenter){
        this.cmdFactoryCenter=cmdFactoryCenter;
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
        ByteBuf bufForDecode=BinaryCodecApi.decodeContent(packet, ctx);
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        if(checkIncr(bufForDecode, session)){
            CmdHandler cmdHandler=BinaryCodecApi.decodeCmdHandler(cmdFactoryCenter,bufForDecode);
            if(cmdHandler!=null){
                out.add(cmdHandler);
            }
        }else{
            bufForDecode.skipBytes(bufForDecode.readableBytes());
        }
        bufForDecode.release();
        incred=false;

    }
    private boolean checkIncr(ByteBuf bufForDecode, ISession session){
        int ci=bufForDecode.readInt();//获取消息中的自增ID
        if(session.containsAttribute(Session.INCREMENT)){//如果已存在自增ID
            int si=session.getAttribute(Session.INCREMENT).intValue();
            if(ci==si){//判断客户端传送自增ID是否与服务器相等
                if(!incred){
                    si=ci+1;//自增
                    session.setAttribute(Session.INCREMENT, si);
                    incred=true;
                }
            }else{
                log.error("自增ID不合法:ci ={},si={}", ci, si);
                return false;
            }
        }else{
            session.setAttribute(Session.INCREMENT, ci + 1);
            incred=true;
        }
        return true;
    }
}
