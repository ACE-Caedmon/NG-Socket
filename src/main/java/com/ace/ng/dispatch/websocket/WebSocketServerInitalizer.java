package com.ace.ng.dispatch.websocket;

import com.ace.ng.constant.VarConst;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.dispatch.message.HandlerFactory;
import io.netty.buffer.PooledByteBufAllocator;
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
public class WebSocketServerInitalizer extends ChannelInitializer<SocketChannel> {
    private HandlerFactory handlerFactory;
    private CmdTaskFactory<?> cmdTaskFactory;
    private String secretKey;
    public WebSocketServerInitalizer( HandlerFactory handlerFactory,CmdTaskFactory<?> cmdTaskFactory,String secretKey){
        this.handlerFactory=handlerFactory;
        this.cmdTaskFactory=cmdTaskFactory;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.config().setAllocator(PooledByteBufAllocator.DEFAULT);
        //IdleStateHandler idleStateHandler=new IdleStateHandler(60, 30, 0);
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        //ch.pipeline().addLast(idleStateHandler);
        ch.pipeline().addLast(new HttpServerCodec());
        ch.pipeline().addLast(new WebSocketCmdDispatcher(handlerFactory, cmdTaskFactory));
        ch.attr(VarConst.SECRRET_KEY).set(secretKey);

    }
}
