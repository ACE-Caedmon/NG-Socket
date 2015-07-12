package com.ace.ng.boot;

/**
 * Created by Caedmon on 2015/7/12.
 */
public class EngineSettings {
    /**协议类型 'tcp/websocket'*/

    public String protocol=SocketEngine.TCP_PROTOCOL;
    /**TCP端口**/
    public int port=8001;
    /**Netty层Worker类线程数*/
    public int workerThreadSize=10;
    /**Message处理线程池大小**/
    public int cmdThreadSize =10;

    public String scanPackage;

    public String secretKey="ng-socket";
}
