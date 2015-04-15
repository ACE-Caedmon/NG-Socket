/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.ace.ng.dispatch.tcp;


import com.ace.ng.codec.encrypt.BinaryEncryptDecoder;
import com.ace.ng.codec.encrypt.BinaryEncryptEncoder;
import com.ace.ng.codec.binary.BinaryDecoder;
import com.ace.ng.codec.binary.BinaryEncoder;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.dispatch.message.HandlerFactory;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

@Sharable
public class TCPServerInitializer extends ChannelInitializer<SocketChannel>{
	private CmdTaskFactory cmdTaskFactory;
    private HandlerFactory handlerFactory;
	public TCPServerInitializer(HandlerFactory handlerFactory,CmdTaskFactory cmdTaskFactory){
		this.handlerFactory=handlerFactory;
        this.cmdTaskFactory = cmdTaskFactory;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
		 //IdleStateHandler idleStateHandler=new IdleStateHandler(60, 30, 0);
		ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		//ch.pipeline().addLast(idleStateHandler);
//		ch.pipeline().addLast(new ChannelEventHandler());
		ch.pipeline().addLast(new BinaryDecoder());
		ch.pipeline().addLast(new BinaryEncryptDecoder(handlerFactory));
		ch.pipeline().addLast(new BinaryEncryptEncoder());
		ch.pipeline().addLast(new BinaryEncoder());
		ch.pipeline().addLast(new TCPCmdDispatcher(cmdTaskFactory));
		
		
	}

}
