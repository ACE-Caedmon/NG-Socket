package com.ace.ng.codec;

/**
 * @author Chenlong
 * 输出对象
 * */
public interface OutMessage extends CmdMessage{
	/**
	 * 将对象属性值编码写入Bytebuf
     * @param buf 自定义的ByteBuf装饰器
	 * */
	void encode(CustomBuf buf);
	/**
     * @return  针对每个客户端指令返回的错误码
     * */
	byte getCode();
    /**
     * @param code  针对每个客户端指令返回的错误码
     * */
	void setCode(byte code);
}
