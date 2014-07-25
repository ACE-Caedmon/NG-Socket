package com.ace.ng.dispatch;


public interface MessageHandlerFactory<K,H> {
	/**
	 * 根据指令ID获取MessageHandler的实例化对象
	 * @param cmd 指令ID
     * @return 对应指定cmd的消息处理器
	 * */
	H getHandler(K cmd);
	/**
	 * 注册消息处理器
	 * @param cmd 指令ID
	 * @param clazz 消息处理器的Class对象
	 * */
	void registerHandler(K cmd, Class<?> clazz);
	/**
	 * 移除指定消息处理器
	 * @param cmd 指令ID
	 * */
	void remove(K cmd);
	
	void destory();
}
	
