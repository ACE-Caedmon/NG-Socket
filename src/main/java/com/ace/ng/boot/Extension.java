package com.ace.ng.boot;
import com.ace.ng.dispatch.message.CmdAnnotation;
import com.ace.ng.dispatch.message.CmdHandler;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Chenlong
 * 模块扩展上层抽象类，新增功能模块继承此类作为模块入口以及提供初始化方法
 * */
public abstract class Extension {
	private Map<Short, Class> messageHandlers;
	public Extension(){
		this.messageHandlers=new HashMap<Short, Class>(10);
	}
	protected abstract void init();
	/**
	 * 注册消息处理器
	 * @param clazz 对应指令ID的MessageHandler的Class
	 * */
	protected void regiterCmd(Class<? extends CmdHandler> clazz){
		CmdAnnotation annotation=(CmdAnnotation)clazz.getAnnotation(CmdAnnotation.class);
		if(annotation==null){
			throw new NullPointerException(clazz.getName()+" 必须加上 CmdAnnotation注解");
		}
		messageHandlers.put(annotation.id(), clazz);
	}
	public Map<Short, Class> getCmdHandlers(){
		return messageHandlers;
	}
	public void destory(){
		messageHandlers.clear();
	}
}
