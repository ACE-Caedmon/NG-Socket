package com.ace.ng.proxy;

import com.ace.ng.codec.DataBuffer;
import com.ace.ng.session.ISession;

/**
 * Created by Caedmon on 2015/7/11.
 */
public class DefaultControlMethodProxy implements ControlMethodProxy<ISession> {
    private DataBuffer content;
    public DefaultControlMethodProxy(DataBuffer content){
        this.content=content;
    }
    @Override
    public final void doCmd(ISession session) {
        //自动解码
        //调用control对应方法
        //将返回值发送回去
    }

    @Override
    public ISession getCmdUser(ISession session) {
        return session;
    }
}
