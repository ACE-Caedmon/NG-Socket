package com.ace.ng.codec.encrypt;


import com.ace.ng.codec.ByteCustomBuf;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.OutMessage;
import com.ace.ng.constant.VarConst;
import com.ace.ng.session.ISession;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.List;
import java.util.Random;
/**
 * 网络数据报文编码器
 * @author Chenlong
 * 协议格式<br>
 *     <table  border frame="box">
 *         <tr>
 *             <th style="text-align:center"></th>
 *             <th style="text-align:center">包长</th>
 *             <th style="text-align:center">是否加密</th>
 *             <th style="text-align:center">密码表索引</th>
 *             <th style="text-align:center">指令ID(cmd)</th>
 *             <th style="text-align:center">消息错误码(code)</th>
 *             <th style="text-align:center">消息体(OutMessage中自定义内容)</th>
 *         </tr>
 *         <tr>
 *             <td>数据类型</td>
 *             <td style="text-align:center">short</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">short</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">byte[]</td>
 *         </tr>
 *         <tr>
 *             <td>每部分字节数</td>
 *             <td style="text-align:center">2</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">2</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">根据消息体内容计算</td>
 *         </tr>
 *         <tr>
 *             <td style="text-align:center">是否加密</td>
 *             <td colspan="3" style="text-align:center">未加密部分</td>
 *             <td colspan="3" style="text-align:center">加密部分</td>
 *         </tr>
 *     </table>
 * */
@Sharable
public class EncryptEncoder extends MessageToByteEncoder<OutMessage>{
    @Override
	protected void encode(ChannelHandlerContext ctx, OutMessage msg, ByteBuf out)
			throws Exception {
		ByteBuf buf=Unpooled.buffer();
		buf.writeShort(msg.getCmd());
		buf.writeByte(msg.getCode());
		CustomBuf content=new ByteCustomBuf(buf);
		msg.encode(content);
		byte[] dst=new byte[buf.readableBytes()];
		ISession session=(ISession)ctx.channel().attr(VarConst.SESSION_KEY).get();
		Object isEncryptObject=session.getVar(VarConst.NEED_ENCRYPT);//是否需要加密
		boolean isEncrypt=false;
		if(isEncryptObject!=null){
			isEncrypt=(Boolean)isEncryptObject;
		}
		buf.readBytes(dst);
		int index=new Random().nextInt(256);//随机获取密码索引
		if(isEncrypt){
			List<Short> passports=(List<Short>)session.getVar(VarConst.PASSPORT);
			short passport=passports.get(index);//根据索引获取密码
			EncryptUtil.encode(dst, dst.length, EncryptUtil.KEY, passport);//加密
		}
		buf=Unpooled.buffer(dst.length+4);//开辟新Buff
		buf.writeShort(dst.length+2);//写入包长度
		buf.writeBoolean(isEncrypt);//写入加密标识
		buf.writeByte(index);//写入密码索引
		buf.writeBytes(dst);//写入dst
		//logger.debug("Server Send :"+JSON.toJSONString(msg));
		out.writeBytes(buf);
	}

}
