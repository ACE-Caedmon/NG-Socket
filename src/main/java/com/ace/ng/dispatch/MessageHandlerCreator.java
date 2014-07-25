package com.ace.ng.dispatch;

import com.ace.ng.dispatch.message.MessageHandler;

public interface MessageHandlerCreator {
	public MessageHandler<?> create(Short cmd);
}
