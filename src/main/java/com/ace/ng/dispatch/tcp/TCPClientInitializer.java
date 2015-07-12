package com.ace.ng.dispatch.tcp;

import com.ace.ng.boot.TCPClientSettings;
import com.ace.ng.codec.encrypt.ClientBodyDecoder;
import com.ace.ng.codec.encrypt.ClientBodyEncoder;
import com.ace.ng.codec.encrypt.ServerBodyEncoder;
import com.ace.ng.codec.encrypt.ServerBodyDecoder;
import com.ace.ng.proxy.ControlProxyFactory;
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
    private ControlProxyFactory controlProxyFactory;
    private TCPClientSettings settings;
    public TCPClientInitializer(ControlProxyFactory controlProxyFactory,TCPClientSettings settings){
        this.controlProxyFactory=controlProxyFactory;
        this.settings=settings;
    }
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        if(NGSocketParams.NETTY_LOGGING){
            ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
        }
        ch.pipeline().addLast(new TCPBinaryDecoder());
        ch.pipeline().addLast(new ClientBodyDecoder(controlProxyFactory));
        ch.pipeline().addLast(new TCPBinaryEncoder());
        ch.pipeline().addLast(new ClientBodyEncoder());
        ch.pipeline().addLast(new TCPServerInboundHandler(controlProxyFactory));
        ch.attr(Session.SECRRET_KEY).set(settings.secretKey);


    }
}
