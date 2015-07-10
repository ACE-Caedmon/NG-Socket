package com.ace.ng.impl;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.session.ISession;

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
