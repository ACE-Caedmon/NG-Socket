package com.ace.ng.codec.encrypt;

import com.ace.ng.codec.BinaryCodecApi;
import com.ace.ng.codec.OutputPacket;
import com.ace.ng.codec.binary.BinaryPacket;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2015/4/25.
 */
@ChannelHandler.Sharable
public class ClientBinaryEncryptEncoder extends MessageToMessageEncoder<OutputPacket> {
    private static Logger logger= LoggerFactory.getLogger(ClientBinaryEncryptEncoder.class);
    @Override
    protected void encode(ChannelHandlerContext ctx, OutputPacket output, List<Object> out)
            throws Exception {
        BinaryCodecApi.EncryptBinaryPacket packet=BinaryCodecApi.encodeContent(output, ctx.channel());
        ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        int increment=session.getAttribute(Session.INCREMENT).intValue();
        ByteBuf buf= PooledByteBufAllocator.DEFAULT.buffer(packet.content.length + 4);//开辟新Buff
        buf.writeBoolean(packet.isEncrypt);//写入加密标识
        buf.writeByte(packet.passwordIndex);//写入密码索引
        buf.writeInt(increment);//写入自增ID
        buf.writeBytes(packet.content);//写入dst
        BinaryPacket finalPacket=new BinaryPacket(buf);
        out.add(finalPacket);
        //自增
        session.setAttribute(Session.INCREMENT, session.getAttribute(Session.INCREMENT) + 1);
    }
}
