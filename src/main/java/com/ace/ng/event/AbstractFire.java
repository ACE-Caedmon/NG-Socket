package com.ace.ng.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Chenlong
 * 事件触发器抽象实现类，泛型提供类型指定扩展
 * */
public class AbstractFire<T> implements Fire<T>{
	protected Map<IEvent, List<IEventHandler<T>>> firesMap=new ConcurrentHashMap<>();
	private static final Logger log= LoggerFactory.getLogger(AbstractFire.class);
	/**
	 * 注册事件
	 * @param name 事件名
	 * @param handler 事件处理器类
	 * */
	@Override
	public void registerEvent(IEvent name, IEventHandler<T> handler) {
		// TODO Auto-generated method stub
		List<IEventHandler<T>> handlers=firesMap.get(name);
		if(handlers==null){
			handlers=new LinkedList<IEventHandler<T>>();
			firesMap.put(name, handlers);
		}
		for(IEventHandler<T> h:handlers){
			if(h.getClass()==handler.getClass()){
				log.warn("重复添加相同的监听事件处理器:handlerClass={}",handler.getClass().getName());
				return;
			}
		}
		handlers=firesMap.get(name);
		handlers.add(handler);
		
	}
	/**
	 * 触发事件
	 * @param name 事件名
	 * @param entity 事件触发需要传递的参数
	 * */
	@Override
	public void fireEvent(IEvent name,T entity) {
		// TODO Auto-generated method stub
		List<IEventHandler<T>> handlers=firesMap.get(name);
		if(handlers!=null){
			for(IEventHandler<T> handler:handlers){
				handler.handleEvent(entity);
			}
		}
	}


}
