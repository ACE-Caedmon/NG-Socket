package com.ace.ng.impl;

import com.ace.ng.dispatch.message.MessageHandler;
import com.ace.ng.dispatch.message.MessageTask;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/5/23.
 */
public class SessionMessageTask extends MessageTask {
    public SessionMessageTask(ISession session,MessageHandler handler){
       super(session,handler);
    }
    @Override
    public void run() {
        SessionMessageHandler sessionMessageHandler=(SessionMessageHandler)handler;
        sessionMessageHandler.excute(session);
    }
}
