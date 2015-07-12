package javassit;

import com.ace.ng.codec.ByteDataBuffer;
import com.ace.ng.codec.DataBuffer;
import com.ace.ng.proxy.ControlMethodProxy;
import com.ace.ng.proxy.ControlProxyFactory;
import com.ace.ng.proxy.JavassitControlProxyFactory;
import com.ace.ng.proxy.PrototypeBeanAccess;
import com.ace.ng.session.Session;
import com.alibaba.fastjson.JSONObject;
import common.LoginInput;
import io.netty.buffer.Unpooled;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.junit.Before;
import org.junit.Test;
import common.TestCmdControl;

/**
 * Created by Administrator on 2015/7/10.
 */
public class ControlMethodProxyTest {
    private static ControlProxyFactory controlProxyFactory;
    @Before
    public void initControlProxyFactory() throws Exception{
        controlProxyFactory=new JavassitControlProxyFactory(new PrototypeBeanAccess());
    }
    public void createMethodProxy(Class controlClass) throws Exception{
        String basePackage="common";
        controlProxyFactory.loadControlClass(controlClass);
    }
    @Test
    public void createProxyControl() throws Exception{
        createMethodProxy(TestCmdControl.class);
    }
    @Test
    public void getProxyControl() throws Exception{
        DataBuffer buffer=new ByteDataBuffer(Unpooled.buffer());
        LoginInput input=new LoginInput();
        input.setUsername("test");
        input.setPassword("123456");
        buffer.writeString(JSONObject.toJSONString(input));
        createMethodProxy(TestCmdControl.class);
        ControlMethodProxy proxy=controlProxyFactory.newControlMethodProxy(1,buffer);
        proxy.doCmd(new Session(new NioSocketChannel()));
        System.out.println(proxy.getClass().getName());
    }
}
