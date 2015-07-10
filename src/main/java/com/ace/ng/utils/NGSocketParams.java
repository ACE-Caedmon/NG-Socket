package com.ace.ng.utils;

import io.netty.util.internal.SystemPropertyUtil;

/**
 * Created by Administrator on 2015/5/7.
 */
public class NGSocketParams {
    /**是否开启Netty的LoggingHandler*/
    public static final boolean NETTY_LOGGING = SystemPropertyUtil.getBoolean("ng.socket.netty.loggging",false);
    /**出现未知Cmd时，是否日志提醒*/
    public static final boolean WARN_UNKOWN_CMD=SystemPropertyUtil.getBoolean("ng.socket.warn.unkowncmd",true);
    /**是否加密数据包*/
    public static final boolean SOCKET_PACKET_ENCRYPT=SystemPropertyUtil.getBoolean("ng.socket.packet.encrypt",false);
    /**数据包加密解密的密钥*/
    public static final String SOCKET_SECRET_KEY=SystemPropertyUtil.get("ng.socket.secret.key","NG-Socket");
}
