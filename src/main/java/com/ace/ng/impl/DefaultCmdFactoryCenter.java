package com.ace.ng.impl;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.session.ISession;
import com.jcwx.frm.current.IActorManager;

import java.util.concurrent.ThreadFactory;

/**
 * Created by ChenLong on 2014/5/23.
 */
public class DefaultCmdFactoryCenter extends CmdFactoryCenter<ISession> {
    public DefaultCmdFactoryCenter(int threadSize) {
        super(threadSize);
    }

    @Override
    public ISession getUser(ISession session) {
        return session;
    }
}
