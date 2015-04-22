package com.ace.ng.dispatch.message;

import com.ace.ng.session.ISession;

/**
 * Created by ChenLong on 2015/4/15.
 */
public interface CmdTaskInterceptor {
    /**
     * @return true 过滤通过  false 过滤不通过，不能往后执行
     * */
    boolean beforeExecute(ISession session,CmdHandler cmdHandler);
    /**
     * 在CmdHandler.execute()之后执行
     * */
    void afterExecute(ISession session,CmdHandler cmdHandler);
    /**
     * 在CmdHandler.execute()中如果捕获到异常会调用此接口
     * */
    void exceptionCaught(ISession session,CmdHandler cmdHandler,Throwable cause);
 }
