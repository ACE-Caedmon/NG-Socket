package com.ace.ng.codec;

import com.google.protobuf.AbstractMessage.Builder;
/**
 * @author Chenlong
 * Protobuf消息封装输出对象
 * */
public class ProtoBufOutput implements Output{
	private Builder<?> builder;
	public ProtoBufOutput(Builder<?> builder){
		this.builder=builder;
	}
	@Override
	public void encode(DataBuffer buf) {
		buf.writeProtoBuf(builder);
	}

}
