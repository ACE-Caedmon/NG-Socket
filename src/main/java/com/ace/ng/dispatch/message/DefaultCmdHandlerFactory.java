package com.ace.ng.dispatch.message;
/**
 * MessageHandler 工厂，负责产生MessageHandler实例以及注册 MessageHandler事件
 * @author Chenlong
 * */

import com.ace.ng.dispatch.javassit.CmdHandlerCreator;
import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.javassit.NoOpCmdHandlerCreator;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class DefaultCmdHandlerFactory implements CmdHandlerFactory<Integer,CmdHandler> {
	private static Logger logger=LoggerFactory.getLogger(DefaultCmdHandlerFactory.class);
	private Map<Integer, String> messageHandlers;
	private Map<Integer, CmdHandlerCreator> handlerCreateorMap;
	private final Object lock=new Object();
	private static ClassPool classPool=ClassPool.getDefault();
	public DefaultCmdHandlerFactory(){
		messageHandlers=new HashMap(100);
		handlerCreateorMap=new HashMap(100);

	}

	@Override
	public CmdHandler<?> getHandler(Integer cmd) {
		CmdHandlerCreator handlerCreator=handlerCreateorMap.get(cmd);
		try {
            String handlerClassName=messageHandlers.get(cmd);
            if(handlerClassName==null){
                return null;
            }
            //构造HandlerCreator
			if(handlerCreator==null){
				//双重检查
				synchronized (lock){
					if((handlerCreator=handlerCreateorMap.get(cmd))==null){
						String creatorProxyName="com.ace.ng.codec.CmdHandlerCreatorProxy$"+cmd;
						CtClass c=classPool.getOrNull(creatorProxyName);
						if(c==null){
							c=classPool.getAndRename(NoOpCmdHandlerCreator.class.getName(),creatorProxyName );
						}
						CtMethod m=c.getDeclaredMethod("create");
						m.insertBefore("com.ace.ng.dispatch.message.CmdHandler handler = new " + handlerClassName + "();" + " if(handler!=null){ return handler;}");
						handlerCreator=(CmdHandlerCreator)c.toClass().newInstance();
						handlerCreateorMap.put(cmd,handlerCreator);
					}
				}

            }

			CmdHandler<?> result=handlerCreator.create(cmd);
			return result;
			
		} catch (Exception e) {
			logger.error("获取CmdHandler异常( cmd = "+cmd+")",e);
            e.printStackTrace();
		}
		return null;
	}

	@Override
	public void registerHandler(Integer cmd, Class<?> clazz) {
		if(CmdHandler.class.isAssignableFrom(clazz)){
//			if(messageHandlers.containsKey(cmd)){
//				logger.error("消息指令已存在( cmd = "+cmd+",alreadyClass = "+messageHandlers.get(cmd)+",newClass = "+clazz.getName());
//			}
			messageHandlers.put(cmd, clazz.getName());
		}else{
			throw new IllegalArgumentException("消息处理器类必须实现CmdHandler接口 ( cmd = "+cmd+",class = "+clazz.getName()+")");
		}	
		
	}

	@Override
	public void remove(Integer cmd) {
		messageHandlers.remove(cmd);
		
	}
	@Override
	public void destory() {
		messageHandlers.clear();
		
	}
}
