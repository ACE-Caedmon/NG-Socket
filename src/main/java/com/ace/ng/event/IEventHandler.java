package com.ace.ng.event;
/**
 * @author Chenlong
 * 事件处理器接口
 * */
public interface IEventHandler<T> {
	public void handleEvent(T entity);
}
