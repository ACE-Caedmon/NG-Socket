package com.ace.ng.examples.server;

import com.ace.ng.dispatch.message.CmdAnnotation;
import com.ace.ng.impl.SessionCmdHandler;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/6/9.
 */
@CmdAnnotation(id=1,desc="测试用")
public class Handler0001 extends SessionCmdHandler {
    private String message;
    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public void excute(ISession user) {
        System.out.println("接受到客户端消息:"+message);
        Message001 message001=new Message001((short)1);
        message001.setContent("服务端回发消息");
        user.send(message001);
    }
}
