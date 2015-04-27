package com.ace.ng.examples;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.boot.WsClientSettings;
import com.ace.ng.boot.WsClientSocketEngine;
import com.ace.ng.impl.DefaultCmdFactoryCenter;
import com.jcwx.frm.current.CurrentUtils;
import org.apache.log4j.PropertyConfigurator;

import java.io.IOException;

/**
 * Created by Administrator on 2014/6/9.
 */
public class Client {
    public static void main(String[] args) throws IOException {
        PropertyConfigurator.configure("conf/log4j.properties");
        WsClientSettings settings=new WsClientSettings();
        settings.url="ws://localhost:8001/websocket";
        CmdFactoryCenter cmdFactoryCenter=new DefaultCmdFactoryCenter(2);
        WsClientSocketEngine socketEngine=new WsClientSocketEngine(settings,cmdFactoryCenter);
        socketEngine.registerExtension(new TestExtension());
        socketEngine.start();

    }
}
