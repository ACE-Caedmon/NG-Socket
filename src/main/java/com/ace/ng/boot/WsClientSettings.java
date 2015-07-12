package com.ace.ng.boot;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

/**
 * Created by Administrator on 2015/4/25.
 */
public class WsClientSettings extends EngineSettings{
    public String url;
    public WebSocketVersion webSocketVersion=WebSocketVersion.V13;
    public boolean allowExtensions=true;
    public HttpHeaders httpHeaders=new DefaultHttpHeaders();
}
