/**
 * 消息任务抽象类，提供消息任务业务扩展
 * 扩展run()方法,负责MessageHandler.execute()中参数类型指定以及构造
 * @author Chenlong
 * */
package com.ace.ng.dispatch.message;


import com.ace.ng.session.ISession;

public abstract class CmdTask implements Runnable{
	protected CmdHandler<?> handler;
	protected ISession session;
	public CmdTask(ISession session, CmdHandler<?> handler){
		this.handler=handler;
		this.session=session;
	}
	public CmdHandler<?> getHandler() {
		return handler;
	}
	public ISession getSession() {
		return session;
	}
}
