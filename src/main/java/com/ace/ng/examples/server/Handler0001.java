package com.ace.ng.examples.server;

import com.ace.ng.dispatch.message.Cmd;
import com.ace.ng.impl.SessionCmdHandler;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/6/9.
 */
@Cmd(id=1,desc="测试用")
public class Handler0001 extends SessionCmdHandler {
    private String content;

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void execute(ISession user) {
        System.out.println("接受到客户端消息:" +content);
        Message001 message001=new Message001();
        message001.setContent("Message from Server");
        user.send((short)1,message001);
    }
}
