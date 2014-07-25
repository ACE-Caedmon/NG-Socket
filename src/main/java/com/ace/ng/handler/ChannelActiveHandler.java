package com.ace.ng.handler;


import com.ace.ng.constant.VarConst;
import com.ace.ng.event.IEventHandler;
import com.ace.ng.session.ISession;

public class ChannelActiveHandler implements IEventHandler<ISession> {

	@Override
	public void handleEvent(ISession session) {
		session.setVar(VarConst.NEED_ENCRYPT, false);//开始设置为false
	}

}
