package com.ace.ng.dispatch.javassit;

import com.ace.ng.dispatch.message.CmdHandler;

public interface CmdHandlerCreator {
	CmdHandler<?> create(Integer cmd);
}
