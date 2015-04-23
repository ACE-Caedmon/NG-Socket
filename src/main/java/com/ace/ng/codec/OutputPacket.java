package com.ace.ng.codec;

/**
 * @author Chenlong
 * 输出对象
 * */
public class OutputPacket{
    private short cmd;
    private byte code;
    private Output output;
    public OutputPacket(short cmd,Output output,byte code){
        this.cmd=cmd;
        this.output=output;
        this.code=code;
    }
    public OutputPacket(short cmd,Output output){
        this(cmd,output,(byte)0);
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

    public Output getOutput() {
        return output;
    }
}

