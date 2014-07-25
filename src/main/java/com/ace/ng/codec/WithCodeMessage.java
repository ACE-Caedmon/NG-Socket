package com.ace.ng.codec;
/**
 * @author Chenlong
 * 包括指令ID和处理结果Code的抽象类封装
 * */
public abstract class WithCodeMessage implements OutMessage{
	protected short cmd;
	protected byte code;
	public WithCodeMessage(short cmd){
		this(cmd,(byte)0);
	}
	public WithCodeMessage(short cmd,byte code){
		this.cmd=cmd;
		this.code=code;
	}
	public short getCmd(){
		return this.cmd;
	}
	public byte getCode(){
		return this.code;
	}
	public void setCode(byte code){
		this.code=code;
	}
}
