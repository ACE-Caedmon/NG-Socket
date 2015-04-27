package com.ace.ng.dispatch.tcp;

import com.ace.ng.codec.binary.BinaryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Created by ChenLong on 2015/4/14.
 * 将BinaryPacket 转化为ByteBuf
 */
public class TCPBinaryEncoder extends MessageToByteEncoder<BinaryPacket> {
    @Override
    protected void encode(ChannelHandlerContext ctx, BinaryPacket packet, ByteBuf out) throws Exception {
        ByteBuf buf=Unpooled.buffer();
        buf.writeShort(packet.getContent().readableBytes());
        buf.writeBytes(packet.getContent());
        out.writeBytes(buf);
    }
}
