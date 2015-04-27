package com.ace.ng.codec.encrypt;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.DefaultCmdHandlerFactory;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2015/4/25.
 */
public class ClientBinaryEncryptDecoder extends MessageToMessageDecoder<BinaryPacket> {
    private static final Logger log = LoggerFactory.getLogger(ClientBinaryEncryptDecoder.class);
    private CmdFactoryCenter cmdFactoryCenter;
    public ClientBinaryEncryptDecoder(CmdFactoryCenter cmdFactoryCenter){
        this.cmdFactoryCenter=cmdFactoryCenter;
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
        CmdHandler cmdHandler=BinaryCodecApi.decodeCmdHandler(cmdFactoryCenter, bufForDecode);
        out.add(cmdHandler);
    }

}

