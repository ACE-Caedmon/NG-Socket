package com.ace.ng.dispatch.tcp;

import com.ace.ng.codec.binary.BinaryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;

import java.util.List;

/**
 * Created by ChenLong on 2015/4/14.
 * 将ByteBuf封装为BinaryPacket
 */
public class TCPBinaryDecoder extends ReplayingDecoder<Void> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        short length=in.readShort();
        BinaryPacket packet=new BinaryPacket(in.readBytes(length));
        out.add(packet);
    }
}
