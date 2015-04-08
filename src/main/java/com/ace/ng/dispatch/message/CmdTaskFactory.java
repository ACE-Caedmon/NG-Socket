package com.ace.ng.dispatch.message;

import com.ace.ng.session.ISession;
import com.jcwx.frm.current.IActorManager;


public interface CmdTaskFactory<T> {
	CmdTask createMessageTask(ISession session, CmdHandler<T> handler);
	IActorManager getActorManager();
}
