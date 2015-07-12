package com.ace.ng.codec;

import com.google.protobuf.AbstractMessage.Builder;
import io.netty.buffer.ByteBuf;

import java.util.List;

/**
 * @author Chenlong
 * 缓冲区包装装饰器类，提供在Bytebuf之上更常用的接口
 * */
public interface DataBuffer {

	byte readByte();
	boolean readBoolean();
	short readShort();
	int readInt();
	float readFloat();
	long readLong();
	double readDouble();
	String readString();
	Builder readProtoBuf(Builder builder);
	<T> T readJSON(Class<T> clazz);
	DataBuffer readBinary(int length);
	void writeByte(byte b);
	void writeBoolean(boolean b);
	void writeShort(short s);
	void writeInt(int i);
	void writeFloat(float f);
	void writeLong(long l);
	void writeDouble(double d);
	void writeString(String s);
	void writeProtoBuf(Builder<?> builder);
	void writeJSON(Object bean);
	void writeBinary(DataBuffer buffer);
	ByteBuf getByteBuf();
}
