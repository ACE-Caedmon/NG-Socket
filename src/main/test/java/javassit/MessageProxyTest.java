package javassit;

import com.ace.ng.annotation.MsgType;
import com.ace.ng.codec.DataBuffer;
import com.ace.ng.proxy.MessageProxy;
import com.ace.ng.proxy.MessageProxyFactory;
import common.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by Caedmon on 2015/7/12.
 */
public class MessageProxyTest {
    private static MessageProxyFactory holder;
    @Before
    public void init(){
        holder=MessageProxyFactory.ONLY_INSTANCE;
    }
    public MessageProxy createUserProxy() throws Exception{
        return holder.createMessageProxy(MsgType.JSON, User.class);
    }
    @Test
    public void createMessageProxy() throws Exception{
        MessageProxy proxy=createUserProxy();
        final String username="testname";
        User encodeUser=new User();
        encodeUser.setUsername(username);
        DataBuffer buffer=proxy.encode(encodeUser);
        User decodeUser=(User)proxy.decode(buffer);
        Assert.assertEquals(encodeUser.getUsername(),decodeUser.getUsername());
    }
    @Test
    public void getMessageProxy() throws Exception{
        MessageProxy proxy=createUserProxy();
        Assert.assertEquals(proxy,holder.getMessageProxy(MsgType.JSON,User.class));
    }
}
