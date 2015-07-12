package com.ace.ng.proxy;

import com.ace.ng.codec.DataBuffer;
import com.ace.ng.session.ISession;

/**
 * Created by Caedmon on 2015/7/11.
 */
public interface ControlMethodProxy<T> {
    void doCmd(ISession session);
    T getCmdUser(ISession session);
}
