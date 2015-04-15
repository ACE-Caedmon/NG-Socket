package com.ace.ng.codec.binary;

import com.ace.ng.codec.SocketPacket;
import io.netty.buffer.ByteBuf;

/**
 * Created by ChenLong on 2015/4/14.
 */
public class BinaryPacket implements SocketPacket{
    private ByteBuf content;
    public BinaryPacket(ByteBuf content) {
        this.content=content;
    }
    public ByteBuf getContent() {
        return content;
    }
}
