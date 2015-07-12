package com.ace.ng.codec.encrypt;

import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.ByteDataBuffer;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.proxy.ControlMethodProxy;
import com.ace.ng.proxy.ControlProxyFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2015/4/25.
 */
public class ClientBodyDecoder extends MessageToMessageDecoder<BinaryPacket> {
    private static final Logger log = LoggerFactory.getLogger(ClientBodyDecoder.class);
    private ControlProxyFactory controlProxyFactory;
    public ClientBodyDecoder(ControlProxyFactory controlProxyFactory){
        this.controlProxyFactory=controlProxyFactory;
    }
    /**
     * 负责对数据包进行解码
     * @param ctx 对应Channel的上下文
     * @param packet 数据包
     * @param out 输出事件对象
     * */
    @Override
    protected void decode(ChannelHandlerContext ctx, BinaryPacket packet,
                          List<Object> out) throws Exception {
        ByteBuf content=BinaryCodecApi.decodeContent(packet, ctx);
        ControlMethodProxy methodProxy=controlProxyFactory.newControlMethodProxy(content.readInt(), new ByteDataBuffer(content));
        if(methodProxy!=null){
            out.add(methodProxy);
        }
    }

}

