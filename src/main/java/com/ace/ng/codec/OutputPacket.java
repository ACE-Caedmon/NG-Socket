package com.ace.ng.codec;

/**
 * @author Chenlong
 * 输出对象
 * */
public class OutputPacket{
    private short cmd;
    private Output output;
    public OutputPacket(short cmd,Output output){
        this.cmd=cmd;
        this.output=output;
    }
    public short getCmd(){
        return this.cmd;
    }

    public Output getOutput() {
        return output;
    }
}

