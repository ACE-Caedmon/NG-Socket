package com.ace.ng.client;

import com.ace.ng.codec.binary.BinaryDecoder;
import com.ace.ng.codec.binary.BinaryEncoder;
import com.ace.ng.codec.encrypt.BinaryEncryptDecoder;
import com.ace.ng.codec.encrypt.BinaryEncryptEncoder;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.dispatch.message.HandlerFactory;
import com.ace.ng.dispatch.tcp.TCPCmdDispatcher;
import com.ace.ng.session.Session;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Administrator on 2015/4/25.
 */
public class TCPClientInitializer extends ChannelInitializer<SocketChannel>{
    private CmdTaskFactory cmdTaskFactory;
    private HandlerFactory handlerFactory;
    private String secretKey;
    public TCPClientInitializer(HandlerFactory handlerFactory,CmdTaskFactory cmdTaskFactory,String secretKey){
        this.handlerFactory=handlerFactory;
        this.cmdTaskFactory = cmdTaskFactory;
        this.secretKey=secretKey;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        ch.pipeline().addLast(new BinaryDecoder());
        ch.pipeline().addLast(new BinaryEncryptDecoder(handlerFactory));
        ch.pipeline().addLast(new BinaryEncryptEncoder());
        ch.pipeline().addLast(new BinaryEncoder());
        ch.pipeline().addLast(new TCPCmdDispatcher(cmdTaskFactory));
        ch.attr(Session.SECRRET_KEY).set(secretKey);


    }
}
