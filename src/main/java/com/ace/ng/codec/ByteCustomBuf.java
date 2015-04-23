package com.ace.ng.codec;

import com.google.protobuf.AbstractMessage.Builder;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.buffer.ByteBuf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
/**
 * @author Chenlong
 * ByteBuf 封装类，提供基本对象读取写入缓冲流的方法
 * */
public class ByteCustomBuf implements CustomBuf{
	private ByteBuf buf;
	private Logger logger=LoggerFactory.getLogger(ByteCustomBuf.class);
	public ByteCustomBuf(ByteBuf buf){
		this.buf=buf;
	}
	@Override
	public byte readByte() {
		// TODO Auto-generated method stub
		return buf.readByte();
	}

	@Override
	public boolean readBoolean() {
		// TODO Auto-generated method stub
		return buf.readBoolean();
	}

	@Override
	public short readShort() {
		// TODO Auto-generated method stub
		return buf.readShort();
	}

	@Override
	public int readInt() {
		// TODO Auto-generated method stub
		return buf.readInt();
	}

	@Override
	public float readFloat() {
		// TODO Auto-generated method stub
		return buf.readFloat();
	}

	@Override
	public long readLong() {
		// TODO Auto-generated method stub
		return buf.readLong();
	}

	@Override
	public double readDouble() {
		// TODO Auto-generated method stub
		return buf.readDouble();
	}

	@Override
	public String readString() {
		short length=buf.readShort();
		byte[] content=buf.readBytes(length).array();
		return new String(content,Charset.forName("UTF-8"));
	}

	@Override
	public Builder readProtoBuf(Builder message) {
		short length=buf.readShort();
		byte[] dst=new byte[length];
		buf.readBytes(dst);
		try {
			message.mergeFrom(dst);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
		message.build();
		return  message;
	}

	@Override
	public void writeByte(byte b) {
		buf.writeByte(b);
		
	}

	@Override
	public void writeBoolean(boolean b) {
		buf.writeBoolean(b);
		
	}

	@Override
	public void writeShort(short s) {
		buf.writeShort(s);
		
	}

	@Override
	public void writeInt(int i) {
		buf.writeInt(i);
		
	}

	@Override
	public void writeFloat(float f) {
		buf.writeFloat(f);
		
	}

	@Override
	public void writeLong(long l) {
		buf.writeLong(l);
		
	}

	@Override
	public void writeDouble(double d) {
		buf.writeDouble(d);
		
	}

	@Override
	public void writeString(String s) {
		// TODO Auto-generated method stub
		byte[] arr=s.getBytes(Charset.forName("UTF-8"));
		int length=arr.length;
		buf.writeShort(length);
		buf.writeBytes(arr);
	}

	@Override
	public void writeProtoBuf(Builder<?> builder) {
		byte[] dst=builder.build().toByteArray();
		buf.writeShort(dst.length);
		buf.writeBytes(dst);
	}

}
