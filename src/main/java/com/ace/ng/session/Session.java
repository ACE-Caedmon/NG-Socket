package com.ace.ng.session;


/**
 * @author Chenlong
 * 客户端连接对应的Session
 * */

import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.Output;
import com.ace.ng.codec.OutputPacket;
import com.ace.ng.codec.ProtoBufOutput;
import com.google.protobuf.AbstractMessage;
import com.jcwx.frm.current.IActor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Session implements ISession{
	/**Session对象Key**/
	public static final AttributeKey<ISession> SESSION_KEY=new AttributeKey<ISession>("sessionkey");
	/**秘钥*/
	public static final AttributeKey<String> SECRRET_KEY=new AttributeKey<String>("secretKey");
	/**发送给客户端的数据是否需要加密**/
	public static final AttributeKey<Boolean> NEED_ENCRYPT=new AttributeKey<>("needencrypt");
	/**每个客户端连接存储的密码表**/
	public static final AttributeKey<List<Short>>  PASSPORT=new AttributeKey<>("passport");
	/**验证报文合法性的自增ID**/
	public static final AttributeKey<Integer>  INCREMENT=new AttributeKey<>("increment");
	private Channel channel;//连接通道
	private long createTime;//创建时间
	private long lastActiveTime;//最后活动时间
	private volatile boolean active=true;
	private IActor actor;
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
			}
		} catch (InterruptedException e) {
            e.printStackTrace();
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
	private Future<?> send(short cmd, Output output) {
		if(channel.isActive()){
			OutputPacket packet=new OutputPacket(cmd,output);
			ChannelFuture future=channel.writeAndFlush(packet);
			setLastActiveTime(System.currentTimeMillis());
			return future;
		}else{
			return channel.newSucceededFuture();
		}
	}
	@Override
	public boolean containsAttribute(AttributeKey<?> key) {
		// TODO Auto-generated method stub
		return channel.attr(key).get()!=null;
	}
	@Override
	public <T> T getAttribute(AttributeKey<T> key) {
		// TODO Auto-generated method stub
		return channel.attr(key).get();
	}
	@Override
	public void clear() {

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
	public Future<?> disconnect(boolean immediately, short cmd,Object output) {
		Future<?> future=channel.newSucceededFuture();
		if(channel.isActive()){
			future=this.send(cmd, output);
			try {
				if(immediately){
					future.sync();
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
	public <T> void removeAttribute(AttributeKey<T> key) {
		channel.attr(key).remove();
	}
	public IActor getActor() {
		return actor;
	}

	public void setActor(IActor actor){
		this.actor=actor;
	}

	@Override
	public <T> void setAttribute(AttributeKey<T> key, T value) {
		channel.attr(key).set(value);
	}
	@Override
	public Future<?> send(short cmd, final Object output) {
		Class clazz=output.getClass();
		if(output instanceof Output){
			return send(cmd,(Output)output);
		}else if(output instanceof AbstractMessage.Builder){
			return send(cmd,new ProtoBufOutput((AbstractMessage.Builder)output));
		}else if(clazz==byte.class||clazz==Byte.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeByte((byte) output);
				}
			});
		}else if(clazz==boolean.class||clazz==Boolean.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeBoolean((boolean)output);
				}
			});
		}else if(clazz==short.class||clazz==Short.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeShort((short) output);
				}
			});
		}else if(clazz==int.class||clazz==Integer.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeInt((int) output);
				}
			});
		}else if(clazz==float.class||clazz==Float.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeFloat((float)output);
				}
			});
		}else if(clazz==double.class||clazz==Double.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeDouble((double)output);
				}
			});
		}else if(clazz==long.class||clazz==Long.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeLong((long)output);
				}
			});
		}else if(clazz==String.class){
			return send(cmd, new Output() {
				@Override
				public void encode(CustomBuf buf) {
					buf.writeString(String.valueOf(output));
				}
			});
		}else{
			throw new UnsupportedMessageTypeException("暂不支持该类型自动解码"+output.getClass().getName());
		}
	}
}
