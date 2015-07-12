package boot;

import com.ace.ng.boot.ServerSettings;
import com.ace.ng.boot.ServerSocketEngine;
import com.ace.ng.boot.SocketEngine;
import com.ace.ng.utils.NGSocketParams;
import io.netty.util.internal.SystemPropertyUtil;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by Caedmon on 2015/7/12.
 */
public class Server {
    public static void main(String[] args) {
        PropertyConfigurator.configure("conf/log4j.properties");
        ServerSettings serverSettings=new ServerSettings();
        serverSettings.protocol= SocketEngine.TCP_PROTOCOL;
        serverSettings.port=8001;
        serverSettings.scanPackage="boot.server";
        System.setProperty("ng.socket.netty.loggging", "false");
        ServerSocketEngine engine=new ServerSocketEngine(serverSettings);
        engine.start();
    }
}
