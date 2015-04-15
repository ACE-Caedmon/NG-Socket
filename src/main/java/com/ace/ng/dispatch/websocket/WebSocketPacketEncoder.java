package com.ace.ng.dispatch.websocket;

import com.ace.ng.codec.SocketPacket;
import com.ace.ng.codec.binary.BinaryPacket;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * Created by Administrator on 2015/4/14.
 */
public class WebSocketPacketEncoder extends MessageToMessageEncoder<SocketPacket> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SocketPacket packet, List<Object> out) throws Exception {
        if(packet instanceof BinaryPacket){
            BinaryWebSocketFrame frame=new BinaryWebSocketFrame(((BinaryPacket)packet).getContent());
            out.add(frame);
        }else{
            throw new UnsupportedOperationException("Only support BinaryWebSocketFrame");
        }

    }
}
