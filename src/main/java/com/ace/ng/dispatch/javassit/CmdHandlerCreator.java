package com.ace.ng.dispatch.javassit;

import com.ace.ng.proxy.ControlMethodProxy;

public interface CmdHandlerCreator {
	ControlMethodProxy<?> create(Integer cmd);
}
