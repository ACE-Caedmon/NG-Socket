package com.ace.ng.dispatch.websocket;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.boot.ServerSettings;
import com.ace.ng.session.Session;
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
    private CmdFactoryCenter cmdFactoryCenter;
    private ServerSettings settings;
    public WsServerInitalizer(CmdFactoryCenter cmdFactoryCenter, ServerSettings settings){
        this.cmdFactoryCenter=cmdFactoryCenter;
        this.settings=settings;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
        //IdleStateHandler idleStateHandler=new IdleStateHandler(60, 30, 0);
        if(settings.logging){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new HttpObjectAggregator(65535));
        ch.pipeline().addLast(new WsServerInboundHandler(cmdFactoryCenter));
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);

    }
}
