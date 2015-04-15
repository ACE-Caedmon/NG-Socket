package com.ace.ng.handler;


import com.ace.ng.codec.binary.BinaryEncryptUtil;
import com.ace.ng.codec.PassportTable;
import com.ace.ng.constant.VarConst;
import com.ace.ng.event.IEventHandler;
import com.ace.ng.session.ISession;
import io.netty.util.concurrent.Future;

import java.util.List;
import java.util.concurrent.ExecutionException;
/**
 * 业务逻辑登录成功后要触发此事件，同步将密码表发送给客户端
 * @author Chenlong
 * */
public class ValidateOKHandler implements IEventHandler<ISession> {
	@Override
	public void handleEvent(ISession session) {
		List<Short> passports= BinaryEncryptUtil.getPassBody();
		//session.setVar(VarConst.INCREMENT, new AtomicInteger(passports.get(0)));
		session.setVar(VarConst.PASSPORT, passports);
		PassportTable passportMessage=new PassportTable((short)-1,(byte)0);
		passportMessage.setPassports(passports);
		Future<?> future=session.write(passportMessage);
		try {
			future.get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		session.setVar(VarConst.NEED_ENCRYPT, true);
		
	}

 
}
