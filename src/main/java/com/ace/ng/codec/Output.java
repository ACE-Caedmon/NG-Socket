package com.ace.ng.codec;

import io.netty.buffer.ByteBuf;

/**
 * Created by Administrator on 2015/4/23.
 */
public interface Output {
    Output NULL_CONTENT_OUTPUT=new Output() {
        @Override
        public void encode(CustomBuf buf) {

        }
    };
    void encode(CustomBuf buf);
}
