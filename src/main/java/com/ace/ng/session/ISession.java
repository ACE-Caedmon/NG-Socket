/**
 * 封装的ISession接口，一个客户端连接对应一个Session
 * @author Chenlong
 * */
package com.ace.ng.session;

import com.ace.ng.codec.Output;
import com.jcwx.frm.current.IActor;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;

import java.util.concurrent.TimeUnit;


public interface ISession {
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

	Future<?> disconnect(boolean immediately, short cmd,Output output,byte code);
	/**
	 * @return session最后活动时间
	 * */
	long getLastActiveTime();
	/**
	 * @param lastActiveTime Session最后活动时间
	 * */
	void setLastActiveTime(long lastActiveTime);

	Future<?> send(short cmd,byte code);
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
	<T> void setAttribute(AttributeKey<T> key, T value);
	/**
     * @param key key
	 * @return 是否包含指定变量
	 * */
	boolean containsAttribute(AttributeKey<?> key);
	/**
     * @param key key
	 * @return 获取指定变量
	 * */
	<T> T getAttribute(AttributeKey<T> key);
	/**
	 * 移除指定变量
     * @param key
	 * */
	<T> void removeAttribute(AttributeKey<T> key);
	/**
	 * 清除Session相关信息
	 * */
	void clear();

    void waitForCloseComplete() throws InterruptedException;

    void waitForCloseComplete(long timeout,TimeUnit unit) throws InterruptedException;

    void noticeCloseComplete();

	void setActor(IActor actor);

	IActor getActor();
	Future<?> send(short cmd,Object output);
	Future<?> send(short cmd, Object output,byte code);
}
