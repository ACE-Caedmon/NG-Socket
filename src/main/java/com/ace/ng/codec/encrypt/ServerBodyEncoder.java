package com.ace.ng.codec.encrypt;


import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.OutputPacket;
import com.ace.ng.codec.binary.BinaryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

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
 *             <th style="text-align:center">消息体(OutMessage中自定义内容)</th>
 *         </tr>
 *         <tr>
 *             <td>数据类型</td>
 *             <td style="text-align:center">short</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">byte</td>
 *             <td style="text-align:center">short</td>
 *             <td style="text-align:center">byte[]</td>
 *         </tr>
 *         <tr>
 *             <td>每部分字节数</td>
 *             <td style="text-align:center">2</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">1</td>
 *             <td style="text-align:center">2</td>
 *             <td style="text-align:center">根据消息体内容计算</td>
 *         </tr>
 *         <tr>
 *             <td style="text-align:center">是否加密</td>
 *             <td colspan="3" style="text-align:center">未加密部分</td>
 *             <td colspan="2" style="text-align:center">加密部分</td>
 *         </tr>
 *     </table>
 *
 * */
@Sharable
public class ServerBodyEncoder extends MessageToMessageEncoder<OutputPacket> {
    private static Logger log=LoggerFactory.getLogger(ServerBodyEncoder.class);
    @Override
	protected void encode(ChannelHandlerContext ctx, OutputPacket output, List<Object> out)
			throws Exception {
        BinaryCodecApi.EncryptBinaryPacket encryptBinaryPacket= BinaryCodecApi.encodeContent(output,ctx.channel());
        ByteBuf buf= PooledByteBufAllocator.DEFAULT.buffer(encryptBinaryPacket.content.length + 4);//开辟新Buff
        buf.writeBoolean(encryptBinaryPacket.isEncrypt);//写入加密标识
        buf.writeByte(encryptBinaryPacket.passwordIndex);//写入密码索引
        buf.writeBytes(encryptBinaryPacket.content);//写入dst
        BinaryPacket packet=new BinaryPacket(buf);
        out.add(packet);
	}
}
