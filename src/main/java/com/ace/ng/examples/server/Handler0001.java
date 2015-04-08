package com.ace.ng.examples.server;

import com.ace.ng.dispatch.message.CmdAnnotation;
import com.ace.ng.impl.SessionCmdHandler;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/6/9.
 */
@CmdAnnotation(id=1,desc="≤‚ ‘”√")
public class Handler0001 extends SessionCmdHandler {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void excute(ISession playerOnline) {
        System.out.println("Server recived:"+message);
        Message001 message001=new Message001((short)1);
        message001.setContent("content");
        playerOnline.send(message001);
    }
}
