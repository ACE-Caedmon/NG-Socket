package boot.server;

import com.ace.ng.annotation.*;
import com.ace.ng.session.ISession;
import common.LoginInput;

/**
 * Created by Caedmon on 2015/7/13.
 */
@CmdControl
public class ServerControl {
    @CmdMethod(cmd =1 )
    @CmdResponse(type = MsgType.JSON)
    public LoginInput login(@CmdUser ISession user, @CmdRequest(type = MsgType.JSON) LoginInput loginInput){
        System.out.println("content:"+loginInput.getUsername());
        return loginInput;
    }
}
