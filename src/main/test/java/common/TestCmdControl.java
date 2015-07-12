package common;

import com.ace.ng.annotation.*;
import com.ace.ng.session.ISession;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2015/7/10.
 */
@CmdControl
@Component
public class TestCmdControl {
    public TestCmdControl(){

    }
    @CmdMethod(cmd =1 )
    @CmdResponse(type = MsgType.JSON)
    public LoginInput login(@CmdUser ISession user, @CmdRequest(type = MsgType.JSON) LoginInput loginInput){
        System.out.println("user:"+user.toString());
        System.out.println("content:"+loginInput.getUsername());
        return loginInput;
    }
}
