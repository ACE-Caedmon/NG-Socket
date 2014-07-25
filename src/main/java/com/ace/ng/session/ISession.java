/**
 * 封装的ISession接口，一个客户端连接对应一个Session
 * @author Chenlong
 * */
package com.ace.ng.session;

import com.ace.ng.dispatch.Online;
import com.ace.ng.codec.OutMessage;
import io.netty.channel.Channel;
import io.netty.util.concurrent.Future;

import java.util.Map;
import java.util.concurrent.locks.Lock;


public interface ISession extends Online {
	/**
	 * @return Session创建时间
	 * */
	long getCreateTime();
	/**
	 * @return 得到SocketChannel
	 * */
	Channel getChannel();
	/**
	 * @return 得到客户端远程IP地址
	 * */
	String getClientAddress();
	/**
	 * @return 客户端远程端口
	 * */
	int getClientPort();
	/**
	 * 断开客户端连接
	 * @param immediately 是否立即断开
	 * */
	Future<?> disconnect(boolean immediately);
	/**
	 * @return session最后活动时间
	 * */
	long getLastActiveTime();
	/**
	 * @param lastActiveTime Session最后活动时间
	 * */
	void setLastActiveTime(long lastActiveTime);
	/**
     * 发送信息给客户端
     * @param outMessage 发送的数据对象
     * @return 任务执行结果
	 * */
	Future<?> write(OutMessage outMessage);
	/**
	 * @return 线程锁
	 * */
	Lock getLock();
	/**
	 * @return 是否活动状态
	 * */
	boolean isActive();
    /**
     * @param  active 是否活动状态
     * */
	void setActive(boolean active);
	/**
	 * 设置一个变量到Session中保存，Session断开连接后会销毁
	 * 此方法是线程安全的
     * @param key key
     * @param value value
	 * */
	void setVar(String key, Object value);
	/**
     * @param key key
	 * @return 是否包含指定变量
	 * */
	boolean containsVar(String key);
	/**
     * @param key key
	 * @return 获取指定变量
	 * */
	Object getVar(String key);
	/**
	 * 设置多个变量到Session中
     * @param  vars vars
	 * */
	void setVars(Map<String, Object> vars);
	/**
	 * 移除指定变量
     * @param key
	 * */
	void removeVar(String key);
	/**
	 * 清除Session相关信息
	 * */
	void clear();
}
