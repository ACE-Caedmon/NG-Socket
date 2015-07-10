package scan;

import com.ace.ng.annotation.*;

/**
 * Created by Administrator on 2015/7/10.
 */
@CmdControl(control = 1)
public class TestCmdControl {
    @CmdMethod(method =1 )
    public @CmdResponse String login(@CmdUser User user, @JSONCmdParam LoginInput loginInput){
        return "test";
    }
}
