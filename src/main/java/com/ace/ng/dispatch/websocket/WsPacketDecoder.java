package com.ace.ng.dispatch.websocket;

import com.ace.ng.codec.binary.BinaryPacket;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.util.List;

/**
 * Created by ChenLong on 2015/4/14.
 */
public class WsPacketDecoder extends MessageToMessageDecoder<WebSocketFrame>{
    @Override
    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if(frame instanceof BinaryWebSocketFrame){
            ByteBuf in=frame.content();
            BinaryPacket packet=new BinaryPacket(in);
            in.retain();
            out.add(packet);
        }if(frame instanceof TextWebSocketFrame){
            throw new UnsupportedOperationException("Only support BinaryWebSocketFrame");
        }

    }

}
