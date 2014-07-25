package com.ace.ng.event;

/**
 * 事件触发器接口
 * @author Chenlong
 * */
public interface Fire<T> {
    /**
     * 注册一个事件
     * @param name 事件类型
     * @param handler 对应的事件处理器
     * */
	void registerEvent(IEvent name, IEventHandler<T> handler);
    /**
     * 触发一个事件
     * @param name 事件类型
     * @param entity 触发事件时，传入的参数
     * @see com.ace.ng.event.IEventHandler#handleEvent(Object)
     * */
	void fireEvent(IEvent name, T entity);
}
