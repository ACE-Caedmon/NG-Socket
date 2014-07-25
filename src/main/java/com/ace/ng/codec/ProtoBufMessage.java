package com.ace.ng.codec;

import com.google.protobuf.AbstractMessage.Builder;
/**
 * @author Chenlong
 * Protobuf消息封装输出对象
 * */
public class ProtoBufMessage extends WithCodeMessage{
	private Builder<?> builder;
	public ProtoBufMessage(short cmd,byte code,Builder<?> builder){
		super(cmd, code);
		this.builder=builder;
	}
	public ProtoBufMessage(short cmd,Builder<?> builder){
		this(cmd, (byte)0, builder);
	}

	@Override
	public void encode(CustomBuf buf) {
		buf.writeProtoBuf(builder);
	}

}
