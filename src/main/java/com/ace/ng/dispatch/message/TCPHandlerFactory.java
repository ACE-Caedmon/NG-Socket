package com.ace.ng.dispatch.message;
/**
 * MessageHandler 工厂，负责产生MessageHandler实例以及注册 MessageHandler事件
 * @author Chenlong
 * */

import com.ace.ng.dispatch.MessageHandlerCreator;
import com.ace.ng.dispatch.MessageHandlerFactory;
import com.ace.ng.dispatch.NoOpHandlerPropertySetter;
import com.ace.ng.dispatch.NoOpMessageHandlerCreator;
import com.ace.ng.codec.NotDecode;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class TCPHandlerFactory implements MessageHandlerFactory<Short,MessageHandler<?>> {
	private static Logger logger=LoggerFactory.getLogger(TCPHandlerFactory.class);
	private Map<Short, String> messageHandlers;
	private static TCPHandlerFactory instance=new TCPHandlerFactory();
	private Map<Short, MessageHandlerCreator> handlerCreateorMap;
	private static ClassPool classPool=ClassPool.getDefault();
	public TCPHandlerFactory(){
		messageHandlers=new HashMap<Short, String>(100);
		handlerCreateorMap=new HashMap<Short, MessageHandlerCreator>(100);

	}

	@Override
	public MessageHandler<?> getHandler(Short cmd) {
		MessageHandlerCreator handlerCreator=handlerCreateorMap.get(cmd);
		try {
            String handlerClassName=messageHandlers.get(cmd);
            if(handlerClassName==null){
                return null;
            }
            //构造HandlerCreator
			if(handlerCreator==null){
				CtClass c=classPool.getAndRename(NoOpMessageHandlerCreator.class.getName(), "com.ace.ng.codec.MessageHandlerCreatorProxy$"+cmd);
                CtMethod m=c.getDeclaredMethod("create");
                CtClass handlerClass=classPool.get(handlerClassName);
                m.insertBefore("com.ace.ng.dispatch.message.MessageHandler handler = new " + handlerClassName + "();" + " if(handler!=null){ return handler;}");
                handlerCreator=(MessageHandlerCreator)c.toClass().newInstance();
                handlerCreateorMap.put(cmd,handlerCreator);
            }

			MessageHandler<?> result=handlerCreator.create(cmd);
			return result;
			
		} catch (Exception e) {
			logger.error("获取MessageHandler异常( cmd = "+cmd+")",e);
            e.printStackTrace();
		}
		return null;
	}

	@Override
	public void registerHandler(Short cmd, Class<?> clazz) {
		if(MessageHandler.class.isAssignableFrom(clazz)){
			if(messageHandlers.containsKey(cmd)){
				throw new IllegalArgumentException("消息指令已存在( cmd = "+cmd+",alreadyClass = "+messageHandlers.get(cmd)+",newClass = "+clazz.getName());
			}
			messageHandlers.put(cmd, clazz.getName());
		}else{
			throw new IllegalArgumentException("消息处理器类必须实现MessageHandler接口 ( cmd = "+cmd+",class = "+clazz.getName()+")");
		}	
		
	}

	@Override
	public void remove(Short cmd) {
		messageHandlers.remove(cmd);
		
	}
	@Override
	public void destory() {
		messageHandlers.clear();
		
	}
}
