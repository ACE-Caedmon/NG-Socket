package com.ace.ng.boot;

/**
 * Created by Chenlong on 2014/5/20.
 * TCP服务器启动配置信息类
 */
public class ServerSettings {
    /**协议类型 'tcp/websocket'*/

    public String protocol=SocketEngine.WEBSOCKET_PROTOCOL;
    /**TCP端口**/
    public int port=8001;
    /**Netty层Boss类线程数**/
    public int bossThreadSize=5;
    /**Netty层Worker类线程数*/
    public int workerThreadSize=10;
    /**Message处理线程池大小**/
    public int cmdThreadSize =10;
    /**网络数据是否加密**/
    public boolean encrypt=false;

    public String secretKey="ng-socket";
    /**是否加入LoggingHandler*/
    public boolean logging=false;
}
