package com.ace.ng.dispatch.message;

import com.ace.ng.session.ISession;
import com.jcwx.frm.current.TaskSubmiterService;


public interface MessageTaskFactory<T> {
	MessageTask createMessageTask(ISession session, MessageHandler<T> handler);
	TaskSubmiterService getSubmiterService();
}
