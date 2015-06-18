package com.ace.ng.dispatch.tcp;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.boot.TCPClientSettings;
import com.ace.ng.codec.encrypt.ServerBinaryEncryptDecoder;
import com.ace.ng.codec.encrypt.ServerBinaryEncryptEncoder;
import com.ace.ng.session.Session;
import com.ace.ng.utils.NGSocketParams;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

/**
 * Created by Administrator on 2015/4/25.
 */
public class TCPClientInitializer extends ChannelInitializer<SocketChannel>{
    private CmdFactoryCenter cmdFactoryCenter;
    private TCPClientSettings settings;
    public TCPClientInitializer(CmdFactoryCenter cmdFactoryCenter,TCPClientSettings settings){
        this.cmdFactoryCenter=cmdFactoryCenter;
        this.settings=settings;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if(NGSocketParams.NETTY_LOGGING){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new TCPBinaryDecoder());
        ch.pipeline().addLast(new ServerBinaryEncryptDecoder(cmdFactoryCenter));
        ch.pipeline().addLast(new ServerBinaryEncryptEncoder());
        ch.pipeline().addLast(new TCPBinaryEncoder());
        ch.pipeline().addLast(new TCPServerInboundHandler(cmdFactoryCenter));
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);


    }
}
