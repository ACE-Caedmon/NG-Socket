package com.ace.ng.examples.server;

import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.WithCodeMessage;

/**
 * Created by Administrator on 2014/8/19.
 */
public class Message001 extends WithCodeMessage{
    private int number;
    private short id;
    private String content;
    public Message001(short cmd) {
        super(cmd);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public short getId() {
        return id;
    }

    public void setId(short id) {
        this.id = id;
    }

    @Override
    public void encode(CustomBuf buf) {
        buf.writeInt(number);
        buf.writeShort(id);
        buf.writeString(content);
    }
}
