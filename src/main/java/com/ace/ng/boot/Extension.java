package com.ace.ng.boot;
import com.ace.ng.dispatch.message.CmdHandler;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Chenlong
 * 模块扩展上层抽象类，新增功能模块继承此类作为模块入口以及提供初始化方法
 * */
public abstract class Extension {
	private List<Class<? extends CmdHandler>> cmdHandlers;
	public Extension(){
		this.cmdHandlers=new ArrayList<>(10);
	}
	public abstract void init();
	/**
	 * 注册消息处理器
	 * @param classes 对应指令ID的MessageHandler的Class
	 * */
	public Extension regiterCmd(Class<? extends CmdHandler<?>>...classes){
		for(Class<? extends CmdHandler<?>> c:classes){
			cmdHandlers.add(c);
		}
		return this;
	}
	public List<Class<? extends CmdHandler>> getCmdHandlers(){
		return cmdHandlers;
	}
	public void destory(){
		cmdHandlers.clear();
	}
}
