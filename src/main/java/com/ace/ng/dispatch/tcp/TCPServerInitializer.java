/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.ace.ng.dispatch.tcp;


import com.ace.ng.codec.encrypt.ServerBodyEncoder;
import com.ace.ng.codec.encrypt.ServerBodyDecoder;
import com.ace.ng.proxy.ControlProxyFactory;
import com.ace.ng.session.Session;
import com.ace.ng.utils.NGSocketParams;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class TCPServerInitializer extends ChannelInitializer<SocketChannel>{
    private ControlProxyFactory controlProxyFactory;
	public TCPServerInitializer(ControlProxyFactory controlProxyFactory){
		this.controlProxyFactory=controlProxyFactory;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		if(NGSocketParams.NETTY_LOGGING){
			ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		}

		ch.pipeline().addLast(new TCPBinaryDecoder());
		ch.pipeline().addLast(new ServerBodyDecoder(controlProxyFactory));
		ch.pipeline().addLast(new ServerBodyEncoder());
		ch.pipeline().addLast(new TCPBinaryEncoder());
		ch.pipeline().addLast(new TCPServerInboundHandler(controlProxyFactory));
		ch.attr(Session.SECRRET_KEY).set(NGSocketParams.SOCKET_SECRET_KEY);


	}

}
