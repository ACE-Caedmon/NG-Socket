package com.ace.ng.dispatch.websocket;

import com.ace.ng.codec.binary.BinaryPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * Created by Administrator on 2015/4/14.
 */
public class WebSocketPacketEncoder extends MessageToMessageEncoder<BinaryPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, BinaryPacket packet, List<Object> out) throws Exception {
        BinaryWebSocketFrame frame=new BinaryWebSocketFrame(packet.getContent());
        out.add(frame);
    }
}
