package com.ace.ng.examples.server;

import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.WithCodeMessage;
import com.ace.ng.impl.SessionMessageHandler;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/6/9.
 */
public class Handler0001 extends SessionMessageHandler {
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void excute(ISession playerOnline) {
        System.out.println("Server recived:"+message);
        playerOnline.send(new WithCodeMessage((short)1) {
            @Override
            public void encode(CustomBuf buf) {
                buf.writeString("The message from server");
            }
        });
    }
}
