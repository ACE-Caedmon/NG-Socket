package com.ace.ng.impl;

import com.ace.ng.dispatch.message.MessageHandler;
import com.ace.ng.dispatch.message.MessageTask;
import com.ace.ng.dispatch.message.MessageTaskFactory;
import com.ace.ng.session.ISession;
import com.jcwx.frm.current.CurrentUtils;
import com.jcwx.frm.current.QueueTaskService;
import com.jcwx.frm.current.TaskSubmiterService;

/**
 * Created by Administrator on 2014/5/23.
 */
public class DefaultMessageTaskFactory implements MessageTaskFactory<SessionMessageHandler>{
    private TaskSubmiterService submiterService= new QueueTaskService(2,CurrentUtils.createThreadFactory("NG-Socket-"));
    @Override
    public MessageTask createMessageTask(ISession session, MessageHandler<SessionMessageHandler> handler) {
        return new SessionMessageTask(session,handler);
    }

    @Override
    public TaskSubmiterService getSubmiterService() {
        return submiterService;
    }
}
