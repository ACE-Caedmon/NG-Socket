package com.ace.ng.client;

import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.ByteCustomBuf;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.NotDecode;
import com.ace.ng.codec.binary.BinaryEncryptUtil;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.dispatch.javassit.HandlerPropertySetter;
import com.ace.ng.dispatch.javassit.NoOpHandlerPropertySetter;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.HandlerFactory;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.utils.CommonUtils;
import com.google.protobuf.AbstractMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import javassist.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/4/25.
 */
public class ClientBinaryEncryptDecoder extends MessageToMessageDecoder<BinaryPacket> {
    private static final Logger log = LoggerFactory.getLogger(ClientBinaryEncryptDecoder.class);
    private boolean incred=false;//是否已自增
    private HandlerFactory handlerFactory;

    public ClientBinaryEncryptDecoder(HandlerFactory handlerFactory){
        this.handlerFactory=handlerFactory;
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
        ByteBuf bufForDecode=BinaryCodecApi.decodeContent(packet, ctx);
        CmdHandler cmdHandler=BinaryCodecApi.decodeCmdHandler(handlerFactory, bufForDecode);
        out.add(cmdHandler);
    }

}

