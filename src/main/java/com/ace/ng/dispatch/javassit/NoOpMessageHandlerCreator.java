package com.ace.ng.dispatch.javassit;


import com.ace.ng.dispatch.javassit.CmdHandlerCreator;
import com.ace.ng.dispatch.message.CmdHandler;

/**
 * MessageHandler创建类，通过Javassit动态修改字节码来实现创建不同的MessageHandler
 * @author Chenlong
 * */
public class NoOpMessageHandlerCreator implements CmdHandlerCreator {
	public CmdHandler<?> create(Short cmd){
		return null;
	}
}
