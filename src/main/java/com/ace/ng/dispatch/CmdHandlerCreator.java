package com.ace.ng.dispatch;

import com.ace.ng.dispatch.message.CmdHandler;

public interface CmdHandlerCreator {
	public CmdHandler<?> create(Short cmd);
}
