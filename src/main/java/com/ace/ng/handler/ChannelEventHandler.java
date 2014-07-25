package com.ace.ng.handler;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;

public class ChannelEventHandler extends ChannelDuplexHandler{

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
			throws Exception {
		if(evt instanceof IdleStateEvent){
			IdleStateEvent idleEvent=(IdleStateEvent)evt;
			ctx.close();
		}
	}

}
