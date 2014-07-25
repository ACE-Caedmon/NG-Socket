package com.ace.ng.event;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
/**
 * @author Chenlong
 * 事件触发器抽象实现类，泛型提供类型指定扩展
 * */
public class AbstractFire<T> implements Fire<T>{
	protected Map<IEvent, List<IEventHandler<T>>> firesMap=new HashMap<IEvent, List<IEventHandler<T>>>();
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
