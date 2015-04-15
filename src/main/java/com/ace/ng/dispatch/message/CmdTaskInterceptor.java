package com.ace.ng.dispatch.message;

import com.ace.ng.session.ISession;

/**
 * Created by ChenLong on 2015/4/15.
 */
public interface CmdTaskInterceptor {
    /**
     * @return true ����ͨ��  false ���˲�ͨ������������ִ��
     * */
    boolean beforeExecute(ISession session,CmdHandler cmdHandler);
    void afterExecute(ISession session,CmdHandler cmdHandler);
    void exceptionCaught(ISession session,CmdHandler cmdHandler,Throwable cause);
 }
