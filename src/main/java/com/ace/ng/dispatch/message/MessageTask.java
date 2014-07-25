/**
 * 消息任务抽象类，提供消息任务业务扩展
 * 扩展run()方法,负责MessageHandler.execute()中参数类型指定以及构造
 * @author Chenlong
 * */
package com.ace.ng.dispatch.message;


import com.ace.ng.session.ISession;

public abstract class MessageTask implements Runnable{
	protected MessageHandler<?> handler;
	protected ISession session;
	public MessageTask(ISession session,MessageHandler<?> handler){
		this.handler=handler;
		this.session=session;
	}
	public MessageHandler<?> getHandler() {
		return handler;
	}
	public ISession getSession() {
		return session;
	}
}
