package com.ace.ng.dispatch.websocket;

import com.ace.ng.proxy.ControlProxyFactory;
import com.ace.ng.session.Session;
import com.ace.ng.utils.NGSocketParams;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Administrator on 2015/4/13.
 */
public class WsServerInitalizer extends ChannelInitializer<SocketChannel> {
    private ControlProxyFactory cmdFactoryCenter;
    public WsServerInitalizer(ControlProxyFactory cmdFactoryCenter){
        this.cmdFactoryCenter=cmdFactoryCenter;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
        if(NGSocketParams.NETTY_LOGGING){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(65535));
        ch.pipeline().addLast(new WsServerInboundHandler(cmdFactoryCenter));
        ch.attr(Session.SECRRET_KEY).set(NGSocketParams.SOCKET_SECRET_KEY);

    }
}
