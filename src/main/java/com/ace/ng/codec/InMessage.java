package com.ace.ng.codec;


/**
 * @author Chenlong
 * 输入消息封装对象
 * */

 public interface InMessage {
	/**
	 * 解码
     * @param data ByteBuf装饰器，包含主要业务逻辑数据
	 * */
	void decode(CustomBuf data);
	
}
