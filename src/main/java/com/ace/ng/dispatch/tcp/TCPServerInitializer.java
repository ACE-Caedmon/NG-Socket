/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.ace.ng.dispatch.tcp;


import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.codec.encrypt.ServerBinaryEncryptDecoder;
import com.ace.ng.codec.encrypt.ServerBinaryEncryptEncoder;
import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.session.Session;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class TCPServerInitializer extends ChannelInitializer<SocketChannel>{
	private CmdTaskFactory cmdTaskFactory;
    private CmdFactoryCenter cmdFactoryCenter;
	private String secretKey;
	public TCPServerInitializer(CmdFactoryCenter cmdFactoryCenter,String secretKey){
		this.cmdFactoryCenter=cmdFactoryCenter;
		this.secretKey=secretKey;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		ch.pipeline().addLast(new TCPBinaryDecoder());
		ch.pipeline().addLast(new ServerBinaryEncryptDecoder(cmdFactoryCenter));
		ch.pipeline().addLast(new ServerBinaryEncryptEncoder());
		ch.pipeline().addLast(new TCPBinaryEncoder());
		ch.pipeline().addLast(new TCPCmdDispatcher(cmdFactoryCenter));
		ch.attr(Session.SECRRET_KEY).set(secretKey);
		
		
	}

}
