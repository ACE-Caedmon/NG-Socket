package com.ace.ng.dispatch;


import com.ace.ng.dispatch.message.MessageHandler;

/**
 * MessageHandler创建类，通过Javassit动态修改字节码来实现创建不同的MessageHandler
 * @author Chenlong
 * */
public class NoOpMessageHandlerCreator implements MessageHandlerCreator {
	public MessageHandler<?> create(Short cmd){
		return null;
	}
}
