package com.ace.ng.boot;

import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.impl.DefaultCmdTaskFactory;

/**
 * Created by Chenlong on 2014/5/20.
 * TCP服务器启动配置信息类
 */
public class ServerSettings {
    /**TCP端口**/
    public int port=8001;
    /**Netty层Boss类线程数**/
    public int bossThreadSize=5;
    /**Netty层Worker类线程数*/
    public int workerThreadSize=10;
    /**Message处理线程池大小**/
    public int messageThreadSize=10;
    /**网络数据是否加密**/
    public boolean encrypt=false;
    /**
     * 应用层自定义扩展的MessageTaskFactory实现类
     * @see CmdTaskFactory
     * */
    public CmdTaskFactory cmdTaskFactory =new DefaultCmdTaskFactory();

    public static ServerSettings DEFAULT_INSTANCE=new ServerSettings();
}
