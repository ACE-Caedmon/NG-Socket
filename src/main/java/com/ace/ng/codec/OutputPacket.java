package com.ace.ng.codec;

/**
 * @author Chenlong
 * 输出对象
 * */
public class OutputPacket{
    private int cmd;
    private Output output;
    public OutputPacket(int cmd,Output output){
        this.cmd=cmd;
        this.output=output;
    }
    public int getCmd(){
        return this.cmd;
    }

    public Output getOutput() {
        return output;
    }
}

