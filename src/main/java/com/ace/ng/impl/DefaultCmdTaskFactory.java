package com.ace.ng.impl;

import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTask;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.session.ISession;
import com.jcwx.frm.current.CurrentUtils;
import com.jcwx.frm.current.IActorManager;
import com.jcwx.frm.current.QueueActorManager;

/**
 * Created by Administrator on 2014/5/23.
 */
public class DefaultCmdTaskFactory implements CmdTaskFactory<SessionCmdHandler> {
    private IActorManager actorManager;
    public DefaultCmdTaskFactory(int cmdThreadSize){
        actorManager=new QueueActorManager(cmdThreadSize,CurrentUtils.createThreadFactory("NG-Socket-"));
    }
    @Override
    public CmdTask createMessageTask(ISession session, CmdHandler<SessionCmdHandler> handler) {
        return new SessionCmdTask(session,handler);
    }

    @Override
    public IActorManager getActorManager() {
        return actorManager;
    }
}
