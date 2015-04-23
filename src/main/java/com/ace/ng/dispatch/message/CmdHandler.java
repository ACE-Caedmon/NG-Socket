package com.ace.ng.dispatch.message;


import com.ace.ng.codec.Input;
import com.ace.ng.dispatch.IAction;
import com.ace.ng.codec.CustomBuf;

/**
 * 消息处理器接口
 * 泛型用来定义execute()中的参数类型
 * */


public abstract class CmdHandler<T> implements Input,IAction<T> {

    public void decode(CustomBuf data){

    }

}
