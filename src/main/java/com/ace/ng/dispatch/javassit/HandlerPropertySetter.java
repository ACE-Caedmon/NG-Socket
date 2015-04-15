package com.ace.ng.dispatch.javassit;

import com.ace.ng.codec.CustomBuf;
import com.ace.ng.dispatch.message.CmdHandler;

/**
 * Created by Administrator on 2014/6/6.
 */
public interface HandlerPropertySetter {
    void setHandlerProperties(CustomBuf buf, CmdHandler handler);
}