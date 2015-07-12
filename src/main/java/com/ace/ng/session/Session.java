package com.ace.ng.session;


/**
 * @author Chenlong
 * 客户端连接对应的Session
 * */

import com.ace.ng.annotation.MsgType;
import com.ace.ng.codec.DataBuffer;
import com.ace.ng.codec.Output;
import com.ace.ng.codec.OutputPacket;
import com.ace.ng.proxy.MessageProxy;
import com.ace.ng.proxy.MessageProxyFactory;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.Message;
import com.jcwx.frm.current.IActor;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.handler.codec.UnsupportedMessageTypeException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class Session implements ISession{
	/**Session对象Key**/
	public static final AttributeKey<ISession> SESSION_KEY=new AttributeKey<ISession>("sessionkey");
	/**秘钥*/
	public static final AttributeKey<String> SECRRET_KEY=new AttributeKey<String>("secretKey");
	/**发送给客户端的数据是否需要加密**/
	public static final AttributeKey<Boolean> NEED_ENCRYPT=new AttributeKey<>("needencrypt");
	/**每个客户端连接存储的密码表**/
	public static final AttributeKey<List<Short>>  PASSPORT=new AttributeKey<>("passport");
	private Channel channel;//连接通道
	private long createTime;//创建时间
	private long lastActiveTime;//最后活动时间
	private volatile boolean active=true;
	private IActor actor;
	//Netty已经触发过inActive
	private AtomicBoolean nettyInActive=new AtomicBoolean(false);
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
		ChannelFuture future=null;
		try{
			if(!nettyInActive.get()){
				nettyInActive.compareAndSet(false,true);
				future=channel.disconnect();
				if(immediately){
					future.sync();
				}
				if(actor==null){
					SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_DISCONNECT,Session.this);
				}else{
					java.util.concurrent.Future actorFuture= actor.execute(new Runnable() {
						@Override
						public void run() {
							SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_DISCONNECT, Session.this);
						}
					});
					if(immediately){
						actorFuture.get();
					}
				}
				if(actor!=null){
					actor.releaseExecutor();
				}
			}
		}catch (Exception e){
			e.printStackTrace();
			logger.error("断线异常",e);
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
	private Future<?> send(int cmd, Output output) {
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
	public Future<?> disconnect(boolean immediately, short cmd,Object... objects) {
		Future<?> future=channel.newSucceededFuture();
		if(channel.isActive()){
			future=this.sendBinary(cmd, objects);
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
	public Future<?> sendBinary(final int cmd, final Object... objects) {
 		Output message=new Output() {
			@Override
			public void encode(DataBuffer buf) {
				for(final Object output:objects){
					Class clazz=output.getClass();
					if(output instanceof Output){
						((Output) output).encode(buf);
					}else if(output instanceof AbstractMessage.Builder){
						buf.writeProtoBuf((AbstractMessage.Builder)output);
					}else if(clazz==byte.class||clazz==Byte.class){
						buf.writeByte((byte)output);
					}else if(clazz==boolean.class||clazz==Boolean.class){
						buf.writeBoolean((boolean)output);
					}else if(clazz==short.class||clazz==Short.class){
						buf.writeShort((short) output);
					}else if(clazz==int.class||clazz==Integer.class){
						buf.writeInt((int) output);
					}else if(clazz==float.class||clazz==Float.class){
						buf.writeFloat((float)output);
					}else if(clazz==double.class||clazz==Double.class){
						buf.writeDouble((double)output);
					}else if(clazz==long.class||clazz==Long.class){
						buf.writeLong((long)output);
					}else if(clazz==String.class){
						buf.writeString(String.valueOf(output));
					}else{
						throw new UnsupportedMessageTypeException("暂不支持该类型自动解码"+output.getClass().getName());
					}
				}
			}

		};
		return  send(cmd,message);
	}

	@Override
	public Future<?> sendJSON(int cmd, Object object) {
		try {
			MessageProxy proxy=MessageProxyFactory.ONLY_INSTANCE.getMessageProxy(MsgType.JSON,object.getClass());
			final DataBuffer buffer=proxy.encode(object);
			sendBinary(cmd,new Output(){
				@Override
				public void encode(DataBuffer buf) {
					buf.writeBinary(buffer);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public Future<?> sendProtobuf(int cmd, Message.Builder protobuf) {
		return null;
	}
}
