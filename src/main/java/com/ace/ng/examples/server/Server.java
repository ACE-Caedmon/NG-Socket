package com.ace.ng.examples.server;

import com.ace.ng.boot.SocketEngine;
import com.ace.ng.boot.ServerSettings;
import com.ace.ng.dispatch.message.HandlerFactory;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by Administrator on 2014/6/9.
 */
public class Server {
    public static void main(String[] args){
        PropertyConfigurator.configure("conf/log4j.properties");
        HandlerFactory handlerFactory =new HandlerFactory();
        ServerSettings settings=ServerSettings.DEFAULT_INSTANCE;
        settings.protocol=ServerSettings.TCP_PROTOCOL;
        settings.port=8001;
        SocketEngine engine=new SocketEngine(settings, handlerFactory);
        engine.registerExtension(new TestExtension());
        engine.start();
    }
}
