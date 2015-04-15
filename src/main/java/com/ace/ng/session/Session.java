package com.ace.ng.session;


/**
 * @author Chenlong
 * 客户端连接对应的Session
 * */

import com.ace.ng.codec.OutMessage;
import com.jcwx.frm.current.IActor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Session implements ISession{
	private Channel channel;//连接通道
	private long createTime;//创建时间
	private long lastActiveTime;//最后活动时间
	private volatile boolean active=true;
	private Map<String, Object> varMap=new Hashtable<String, Object>();
	private IActor actor;
    private CountDownLatch closeCompleteLatch=new CountDownLatch(1);
	private Lock lock=new ReentrantLock(true);
    private static Logger logger= LoggerFactory.getLogger(Session.class);
	public Session(Channel channel){
		this.channel=channel;
		this.createTime=System.currentTimeMillis();
	}
	@Override
	public long getCreateTime() {
		// TODO Auto-generated method stub
		return this.createTime;
	}
	@Override
	public Channel getChannel() {
		return channel;
	}
	@Override
	public String getClientAddress() {
		// TODO Auto-generated method stub
		return channel.remoteAddress().toString();
	}
	@Override
	public int getClientPort() {
		// TODO Auto-generated method stub
		return Integer.valueOf(channel.remoteAddress().toString().split(":")[1]).intValue();
	}
	@Override
	public Future<?> disconnect(boolean immediately) {
		ChannelFuture future=channel.disconnect();
		try {
			
			if(immediately){
				future.sync();
                waitForCloseComplete();
			}
		} catch (InterruptedException e) {
            logger.error("Wait disconnect task error",e);
		}
		return future;
	}
	@Override
	public long getLastActiveTime() {
		// TODO Auto-generated method stub
		return lastActiveTime;
	}
	@Override
	public void setLastActiveTime(long lastActiveTime) {
		// TODO Auto-generated method stub
		this.lastActiveTime=lastActiveTime;
	}
	@Override
	public Future<?> write(OutMessage message) {
		if(channel.isActive()){
			ChannelFuture future=channel.writeAndFlush(message);
			setLastActiveTime(System.currentTimeMillis());
			return future;
		}else{
			return channel.newSucceededFuture();
		}


	}
	@Override
	public Lock getLock() {
		// TODO Auto-generated method stub
		return lock;
	}
	@Override
	public void setVar(String key, Object value) {
		varMap.put(key, value);
		
	}
	@Override
	public boolean containsVar(String key) {
		// TODO Auto-generated method stub
		return varMap.containsKey(key);
	}
	@Override
	public Object getVar(String key) {
		// TODO Auto-generated method stub
		return varMap.get(key);
	}
	@Override
	public void clear() {
		varMap.clear();
	}
	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return channel.isActive()&&active;
	}
	@Override
	public void setActive(boolean active) {
		// TODO Auto-generated method stub
		this.active=active;
	}
	@Override
	public Future<?> disconnect(boolean immediately, OutMessage message) {
		Future<?> future=channel.newSucceededFuture();
		if(channel.isActive()){
			future=this.write(message);
			try {
				if(immediately){
					future.sync();
                    waitForCloseComplete(2,TimeUnit.SECONDS);
				}
				future=disconnect(immediately);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
                logger.error("Wait disconnect task error",e);
			}
		}
		return future;
	}
	@Override
	public void setVars(Map<String, Object> vars) {
		for(Entry<String, Object> entry:vars.entrySet()){
			varMap.put(entry.getKey(), entry.getValue());
		}
		
	}
	@Override
	public void removeVar(String key) {
		varMap.remove(key);
	}
	@Override
	public java.util.concurrent.Future<?> send(OutMessage message) {
		// TODO Auto-generated method stub
		return channel.writeAndFlush(message);
	}
	public IActor getActor() {
		return actor;
	}

	public void setActor(IActor actor){
		this.actor=actor;
	}


    @Override
    public void waitForCloseComplete() throws InterruptedException {
        closeCompleteLatch.await();
    }

    @Override
    public void waitForCloseComplete(long timeout,TimeUnit unit) throws InterruptedException {
        closeCompleteLatch.await(timeout,unit);
    }
    @Override
    public void noticeCloseComplete() {
        closeCompleteLatch.countDown();
    }

}
