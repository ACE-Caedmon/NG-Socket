package com.ace.ng.examples;

import com.ace.ng.boot.ServerSettings;
import com.ace.ng.boot.ServerSocketEngine;
import com.ace.ng.boot.SocketEngine;
import com.ace.ng.dispatch.message.DefaultCmdHandlerFactory;
import com.ace.ng.examples.TestExtension;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by Administrator on 2014/6/9.
 */
public class Server {
    public static void main(String[] args){
        PropertyConfigurator.configure("conf/log4j.properties");
        ServerSettings settings= new ServerSettings();
        settings.protocol= SocketEngine.WEBSOCKET_PROTOCOL;
        settings.port=8001;
        ServerSocketEngine engine=new ServerSocketEngine(settings);
        engine.registerExtension(new TestExtension());
        engine.start();
    }
}
