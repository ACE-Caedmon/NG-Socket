package com.ace.ng.constant;


import com.ace.ng.session.ISession;
import io.netty.util.AttributeKey;
/**
 * ISession保存信息相关的Key
 * @see com.ace.ng.session.ISession#setVar(String, Object)
 * @see com.ace.ng.session.ISession#getVar(String)
 * */
public class VarConst {
    /**Session对象Key**/
	public static final AttributeKey<ISession> SESSION_KEY=new AttributeKey<ISession>("sessionkey");
    /**发送给客户端的数据是否需要加密**/
	public static final String NEED_ENCRYPT="needencrypt";
    /**每个客户端连接存储的密码表**/
	public static final String PASSPORT="passport";
    /**验证报文合法性的自增ID**/
	public static final String INCREMENT="increment";
}
