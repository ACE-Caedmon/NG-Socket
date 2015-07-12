package com.ace.ng.boot;

/**
 * Created by Chenlong on 2014/5/20.
 * TCP服务器启动配置信息类
 */
public class ServerSettings extends EngineSettings{
    public String protocol=SocketEngine.WEBSOCKET_PROTOCOL;
    /**TCP端口**/
    public int port=8001;
    /**Netty层Boss类线程数**/
    public int bossThreadSize=2;
}
