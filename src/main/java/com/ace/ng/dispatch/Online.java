package com.ace.ng.dispatch;

import com.ace.ng.codec.OutMessage;
import com.jcwx.frm.current.SubmiterHolder;

import java.util.concurrent.Future;


public interface Online extends SubmiterHolder{
	Future<?> disconnect(boolean imm, OutMessage message);
	Future<?> send(OutMessage message);
	Future<?> disconnect(boolean imm);
}
