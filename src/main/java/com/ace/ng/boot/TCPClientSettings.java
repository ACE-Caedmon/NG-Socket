package com.ace.ng.boot;

/**
 * Created by Administrator on 2015/4/25.
 */
public class TCPClientSettings {
    public String protocol=SocketEngine.TCP_PROTOCOL;
    public String host;
    /**TCP端口**/
    public int port=8001;
    /**Message处理线程池大小**/
    public int cmdThreadSize =10;
    /**网络数据是否加密**/
    public boolean encrypt=false;
    public int workerThreadSize=Runtime.getRuntime().availableProcessors();
    public String secretKey="ng-socket";
    public boolean logging=false;
}
