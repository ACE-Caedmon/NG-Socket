package com.ace.ng.codec.binary;

import io.netty.buffer.ByteBuf;

/**
 * Created by ChenLong on 2015/4/14.
 */
public class BinaryPacket {
    private ByteBuf content;
    public BinaryPacket(ByteBuf content) {
        this.content=content;
    }
    public ByteBuf getContent() {
        return content;
    }
}
