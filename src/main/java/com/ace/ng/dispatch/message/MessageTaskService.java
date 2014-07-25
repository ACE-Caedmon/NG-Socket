/**
 * @author Chenlong
 * MessageTask的生产和消费服务接口
 * @see link{MessageExecutor}
 * */
package com.ace.ng.dispatch.message;


import com.ace.ng.session.ISession;

public interface MessageTaskService<T> extends Runnable{
	MessageTask createTask(ISession session, MessageHandler<T> action);
	void submitTask(ISession session, MessageHandler<T> handler);
}
