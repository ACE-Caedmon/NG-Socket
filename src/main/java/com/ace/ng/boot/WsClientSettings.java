package com.ace.ng.boot;

import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

/**
 * Created by Administrator on 2015/4/25.
 */
public class WsClientSettings {
    public String url;
    public int cmdThreadSize=Runtime.getRuntime().availableProcessors();
    public WebSocketVersion webSocketVersion=WebSocketVersion.V13;
    public boolean allowExtensions=true;
    public HttpHeaders httpHeaders=new DefaultHttpHeaders();
    public int workerThreadSize=Runtime.getRuntime().availableProcessors();
    public String secretKey="ng-socket";
}
