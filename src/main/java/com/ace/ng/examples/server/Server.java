package com.ace.ng.examples.server;

import com.ace.ng.boot.SocketEngine;
import com.ace.ng.boot.ServerSettings;
import com.ace.ng.dispatch.message.TCPHandlerFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by Administrator on 2014/6/9.
 */
public class Server {
    public static void main(String[] args){
        PropertyConfigurator.configure("conf/log4j.properties");
        TCPHandlerFactory tcpHandlerFactory=new TCPHandlerFactory();
        SocketEngine engine=new SocketEngine(ServerSettings.DEFAULT_INSTANCE,tcpHandlerFactory);
        engine.registerExtension(new TestExtension());
        engine.start();
    }
}
