package com.ace.ng.examples.server;

import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.WithCodeMessage;

/**
 * Created by Administrator on 2014/8/19.
 */
public class Message001 extends WithCodeMessage{
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

    @Override
    public void encode(CustomBuf buf) {

    }
}
