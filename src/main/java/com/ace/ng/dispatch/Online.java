package com.ace.ng.dispatch;

import com.ace.ng.codec.OutMessage;
import com.jcwx.frm.current.ActorHolder;

import java.util.concurrent.Future;


public interface Online extends ActorHolder{
	Future<?> disconnect(boolean imm, OutMessage message);
    /**
     * @param  message 不要使用匿名内部类,否则程序无法正常运行
     * */
	Future<?> send(OutMessage message);
	Future<?> disconnect(boolean imm);
}
