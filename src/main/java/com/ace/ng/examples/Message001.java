package com.ace.ng.examples;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.Output;

/**
 * Created by Administrator on 2014/8/19.
 */
public class Message001 implements Output{
    private String content;
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void encode(CustomBuf buf) {
        buf.writeString(content);
    }
}
