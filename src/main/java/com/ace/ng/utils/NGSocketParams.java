package com.ace.ng.utils;

import io.netty.util.internal.SystemPropertyUtil;

/**
 * Created by Administrator on 2015/5/7.
 */
public class NGSocketParams {
    public static final boolean NETTY_LOGGING = SystemPropertyUtil.getBoolean("ng.socket.netty.loggging",false);
    public static final boolean WARN_UNKOWN_CMD=SystemPropertyUtil.getBoolean("ng.socket.warn.unkowncmd",true);
    public static final boolean SOCKET_PACKET_ENCRYPT=SystemPropertyUtil.getBoolean("ng.socket.packet.encrypt",false);
    public static final String SOCKET_SECRET_KEY=SystemPropertyUtil.get("ng.socket.secret.key","NG-Socket");
}
