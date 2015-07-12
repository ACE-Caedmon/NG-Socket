package boot;

import com.ace.ng.boot.Extension;
import com.ace.ng.boot.TCPClientSettings;
import com.ace.ng.boot.TCPClientSocketEngine;
import com.ace.ng.codec.DataBuffer;
import com.ace.ng.codec.Output;
import com.ace.ng.event.IEventHandler;
import com.ace.ng.proxy.JavassitControlProxyFactory;
import com.ace.ng.proxy.PrototypeBeanAccess;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.session.SessionFire;
import common.LoginInput;
import org.apache.log4j.PropertyConfigurator;

/**
 * Created by Caedmon on 2015/7/13.
 */
public class Client {
    public static void main(String[] args) throws Exception{
        PropertyConfigurator.configure("conf/log4j.properties");
        TCPClientSettings settings=new TCPClientSettings();
        settings.port=8001;
        settings.host="localhost";
        settings.scanPackage="boot.client";
        final TCPClientSocketEngine engine=new TCPClientSocketEngine(settings,new JavassitControlProxyFactory(new PrototypeBeanAccess()));
        engine.registerExtension(new FirstExtension());
        engine.start();


    }
    public static class FirstExtension extends Extension{

        @Override
        public void init() {
            SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_CONNECT, new IEventHandler<ISession>() {
                @Override
                public void handleEvent(ISession entity) {
                    LoginInput input = new LoginInput();
                    input.setUsername("clientusername");
                    entity.sendJSON(1,input);
                }
            });
        }
    }
}
