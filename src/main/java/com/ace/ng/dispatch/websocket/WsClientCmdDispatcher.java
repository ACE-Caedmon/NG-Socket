package com.ace.ng.dispatch.websocket;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.codec.encrypt.ClientBinaryEncryptDecoder;
import com.ace.ng.codec.encrypt.ClientBinaryEncryptEncoder;
import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.session.SessionFire;
import com.google.protobuf.InvalidProtocolBufferException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

/**
 * Created by Administrator on 2015/4/25.
 */
public class WsClientCmdDispatcher extends SimpleChannelInboundHandler{
    private CmdFactoryCenter cmdFactoryCenter;
    private WebSocketClientHandshaker handshaker;
    private ChannelPromise handshakeFuture;
    public WsClientCmdDispatcher(WebSocketClientHandshaker handshaker, CmdFactoryCenter cmdFactoryCenter){
        this.handshaker=handshaker;
        this.cmdFactoryCenter=cmdFactoryCenter;
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }
    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final ISession session=new Session(ctx.channel());
        ctx.channel().attr(Session.SESSION_KEY).set(session);
        ctx.channel().attr(Session.INCREMENT).set(0);
        handshake(ctx.channel());

    }
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        session.getActor().execute(new Runnable() {
            public void run() {
                SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_DISCONNECT, session);
                session.clear();
                session.noticeCloseComplete();
            }
        });
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        Channel ch = ctx.channel();
        if (!handshaker.isHandshakeComplete()) {
            handshaker.finishHandshake(ch, (FullHttpResponse) msg);
            handshakeFuture.setSuccess();
            ch.pipeline().addAfter("ws-decoder", "decoder1", new WsPacketDecoder());
            ch.pipeline().addAfter("decoder1", "decoder2", new ClientBinaryEncryptDecoder(cmdFactoryCenter));
            ch.pipeline().addAfter("ws-encoder", "encoder2", new WsPacketEncoder());
            ch.pipeline().addAfter("encoder2", "encoder1", new ClientBinaryEncryptEncoder());
            final ISession session=ch.attr(Session.SESSION_KEY).get();
            cmdFactoryCenter.executeCmd(session, new CmdHandler() {
                @Override
                public void execute(Object user) {
                    SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_CONNECT, session);
                }
            });
            return;
        }

        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException(
                    "Unexpected FullHttpResponse (getStatus=" + response.getStatus() +
                            ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        if (msg instanceof WebSocketFrame) {
            try {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }else if(msg instanceof CmdHandler){
            ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
            cmdFactoryCenter.executeCmd(session, (CmdHandler)msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        if (!handshakeFuture.isDone()) {
            handshakeFuture.setFailure(cause);
        }
        ctx.close();
    }
    private void handshake(final Channel channel){
        handshaker.handshake(channel);
    }
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws InvalidProtocolBufferException {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }

        throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }

}
