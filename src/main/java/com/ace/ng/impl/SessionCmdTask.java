package com.ace.ng.impl;

import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTask;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/5/23.
 */
public class SessionCmdTask extends CmdTask {
    public SessionCmdTask(ISession session, CmdHandler handler){
       super(session,handler);
    }
    @Override
    public void run() {
        SessionCmdHandler sessionMessageHandler=(SessionCmdHandler)handler;
        sessionMessageHandler.excute(session);
    }
}
