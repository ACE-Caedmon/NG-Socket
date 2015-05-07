package com.ace.ng.dispatch.websocket;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.boot.WsClientSettings;
import com.ace.ng.session.Session;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.URI;

/**
 * Created by Administrator on 2015/4/25.
 */
public class WsClientInitalizer extends ChannelInitializer<SocketChannel>{
    private CmdFactoryCenter cmdFactoryCenter;
    private WsClientSettings settings;
    public WsClientInitalizer(WsClientSettings settings, CmdFactoryCenter cmdFactoryCenter){
        this.cmdFactoryCenter=cmdFactoryCenter;
        this.settings=settings;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        if(settings.logging){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new HttpClientCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(65535));
        URI uri=URI.create(settings.url);
        WebSocketClientHandshaker handshaker= WebSocketClientHandshakerFactory.newHandshaker(uri, settings.webSocketVersion, null, settings.allowExtensions, settings.httpHeaders);
        ch.pipeline().addLast(new WsClientInboundHandler(handshaker,cmdFactoryCenter));
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);

    }
}
