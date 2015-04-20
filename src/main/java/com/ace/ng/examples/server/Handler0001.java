package com.ace.ng.examples.server;

import com.ace.ng.dispatch.message.CmdAnnotation;
import com.ace.ng.impl.SessionCmdHandler;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/6/9.
 */
@CmdAnnotation(id=1,desc="测试用")
public class Handler0001 extends SessionCmdHandler {
    private int number;
    private short id;
    private String content;
    public void setContent(String content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    @Override
    public void execute(ISession user) {
        System.out.println("接受到客户端消息:"+number+","+id+","+ content);
        Message001 message001=new Message001((short)1);
        message001.setContent("Message from Server");
        user.send(message001);
    }
}
