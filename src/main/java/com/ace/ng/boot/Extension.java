package com.ace.ng.boot;
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
	 * @param cmd 指令ID
	 * @param clazz 对应指令ID的MessageHandler的Class
	 * */
	protected void registerMessageHandler(short cmd,Class clazz){
		messageHandlers.put(cmd, clazz);
	}
	public Map<Short, Class> getMessageHandlers(){
		return messageHandlers;
	}
	public void destory(){
		messageHandlers.clear();
	}
}
