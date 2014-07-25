/**
 * Channel初始化的自定义类，用来定义解码编码以及相关事件处理器
 * @author Chenlong
 * */
package com.ace.ng.dispatch;


import com.ace.ng.codec.encrypt.EncryptDecoder;
import com.ace.ng.codec.encrypt.EncryptEncoder;
import com.ace.ng.dispatch.message.TCPHandlerFactory;
import com.ace.ng.handler.ChannelEventHandler;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
@Sharable
public class ServerChannelInitializer extends ChannelInitializer<SocketChannel>{
	private MessageDispatcher messageDispatcher;
    private TCPHandlerFactory handlerFactory;
	public ServerChannelInitializer(MessageDispatcher messageDispatcher,TCPHandlerFactory handlerFactory){
		this.handlerFactory=handlerFactory;
        this.messageDispatcher=messageDispatcher;
	}
	@Override
	protected void initChannel(SocketChannel ch) throws Exception {
		ch.config().setAllocator(UnpooledByteBufAllocator.DEFAULT);
		 //IdleStateHandler idleStateHandler=new IdleStateHandler(60, 30, 0);
		//ch.pipeline().addLast(new LoggingHandler(LogLevel.DEBUG));
		//ch.pipeline().addLast(idleStateHandler);
		ch.pipeline().addLast(new ChannelEventHandler());
		ch.pipeline().addLast(new EncryptDecoder(handlerFactory));
		ch.pipeline().addLast(new EncryptEncoder());
		ch.pipeline().addLast(messageDispatcher);
		//ch.pipeline().addLast(new OutBoundDispatcher());
		
		
	}

}
