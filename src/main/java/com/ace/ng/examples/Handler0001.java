package com.ace.ng.examples;

import com.ace.ng.dispatch.message.Cmd;
import com.ace.ng.impl.SessionCmdHandler;
import com.ace.ng.session.ISession;

/**
 * Created by Administrator on 2014/6/9.
 */
@Cmd(id=1,desc="测试用")
public class Handler0001 extends SessionCmdHandler {
    private long playerId;
    private String content;
    private int gold;
    public void setContent(String content) {
        this.content = content;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getContent() {
        return content;
    }

    public int getGold() {
        return gold;
    }

    public void setGold(int gold) {
        this.gold = gold;
    }

    @Override
    public void execute(ISession user) {
        System.out.println("接收消息:" +content);
    }
}
