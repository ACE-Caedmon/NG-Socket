package com.ace.ng.dispatch.message;


import com.ace.ng.dispatch.IAction;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.InMessage;

/**
 * 消息处理器接口
 * 泛型用来定义execute()中的参数类型
 * */


public abstract class MessageHandler<T> implements InMessage,IAction<T> {

    public void decode(CustomBuf data){

    }

}
