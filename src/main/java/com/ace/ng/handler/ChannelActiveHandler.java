package com.ace.ng.handler;


import com.ace.ng.event.IEventHandler;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;

public class ChannelActiveHandler implements IEventHandler<ISession> {

	@Override
	public void handleEvent(ISession session) {
		session.setAttribute(Session.NEED_ENCRYPT, false);//开始设置为false
	}

}
