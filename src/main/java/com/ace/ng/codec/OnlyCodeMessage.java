package com.ace.ng.codec;
/**
 * @author Chenlong
 * 只包含指令ID和错误Code的消息封装对象
 * */
public class OnlyCodeMessage extends WithCodeMessage{
    /**
     * @param  cmd 消息指令ID
     * */
	public OnlyCodeMessage(short cmd) {
		super(cmd);
		// TODO Auto-generated constructor stub
	}
    /**
     * @param cmd 消息指令ID
     * @param code 消息错误码
     * */
	public OnlyCodeMessage(short cmd, byte code) {
		super(cmd, code);
		// TODO Auto-generated constructor stub
	}
    /**
     * 组装数据包的空实现，因为OnlyCodeMessage中不包括除指令和错误码之外的数据
     * */
	@Override
	public void encode(CustomBuf buf) {
		// TODO Auto-generated method stub
		
	}

	
}
