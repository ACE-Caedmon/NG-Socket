package com.ace.ng.impl;

import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.session.ISession;
import com.jcwx.frm.current.IActorManager;

/**
 * Created by ChenLong on 2014/5/23.
 */
public class DefaultCmdTaskFactory extends CmdTaskFactory<ISession> {
    public DefaultCmdTaskFactory(IActorManager actorManager){
        super(actorManager);
    }
    @Override
    public ISession getUser(ISession session) {
        return session;
    }
}
