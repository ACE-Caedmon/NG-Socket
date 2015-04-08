/**
 * @author Chenlong
 * MessageTask的生产和消费服务接口
 * @see link{MessageExecutor}
 * */
package com.ace.ng.dispatch.message;


import com.ace.ng.session.ISession;

public interface CmdTaskService<T> extends Runnable{
	CmdTask createTask(ISession session, CmdHandler<T> action);
	void submitTask(ISession session, CmdHandler<T> handler);
}
