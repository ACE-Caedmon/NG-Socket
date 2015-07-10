package com.ace.ng.dispatch.websocket;

import com.ace.ng.boot.CmdFactoryCenter;
import com.ace.ng.codec.encrypt.ServerBinaryEncryptDecoder;
import com.ace.ng.codec.encrypt.ServerBinaryEncryptEncoder;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.session.SessionFire;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ChenLong on 2015/4/3.
 */
public class WsServerInboundHandler extends SimpleChannelInboundHandler<Object>{

    private static final String WEBSOCKET_PATH = "/websocket";
    private WebSocketServerHandshaker handshaker;
    private CmdFactoryCenter cmdFactoryCenter;
    private static final Logger log= LoggerFactory.getLogger(WsServerInboundHandler.class);
    public WsServerInboundHandler(CmdFactoryCenter cmdFactoryCenter){
        this.cmdFactoryCenter=cmdFactoryCenter;
    }

    @Override
    public void channelActive(final ChannelHandlerContext ctx) throws Exception {
        final ISession session=new Session(ctx.channel());
        ctx.channel().attr(Session.SESSION_KEY).set(session);
        cmdFactoryCenter.executeCmd(session, new CmdHandler() {
            @Override
            public void execute(Object user) {
                SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_CONNECT, session);
            }
        });

    }
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
        session.disconnect(true);
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(ctx, (WebSocketFrame) msg);
        }else if(msg instanceof CmdHandler){
            ISession session=ctx.channel().attr(Session.SESSION_KEY).get();
            cmdFactoryCenter.executeCmd(session, (CmdHandler)msg);
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        log.error("Netty层捕获到异常",cause);
        ctx.close();
    }
    private void handleHttpRequest(ChannelHandlerContext ctx, FullHttpRequest req) {
        // Handle a bad request.
        if (!req.getDecoderResult().isSuccess()) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,HttpResponseStatus.BAD_REQUEST));
            return;
        }
        // Allow only GET methods.
        if (req.getMethod() != HttpMethod.GET) {
            sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
            return;
        }
        // Send the demo page and favicon.ico
        if ("/".equals(req.getUri())) {
            ByteBuf content = WsServerIndexPage.getContent(getWebSocketLocation(req));
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, content);

            res.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8");
            HttpHeaders.setContentLength(res, content.readableBytes());

            sendHttpResponse(ctx, req, res);
            return;
        }
        if ("/favicon.ico".equals(req.getUri())) {
            FullHttpResponse res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
            sendHttpResponse(ctx, req, res);
            return;
        }
        handshake(ctx,req);

    }
    private void handshake(ChannelHandlerContext ctx,FullHttpRequest req){
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
            ctx.pipeline().addAfter("wsdecoder", "decoder1", new WsPacketDecoder());
            ctx.pipeline().addAfter("decoder1", "decoder2", new ServerBinaryEncryptDecoder(cmdFactoryCenter));
            ctx.pipeline().addAfter("wsencoder", "encoder1", new WsPacketEncoder());
            ctx.pipeline().addAfter("encoder1", "encoder2", new ServerBinaryEncryptEncoder());


        }
    }
    private static void sendHttpResponse(
            ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        // Generate an error page if response getStatus code is not OK (200).
        if (res.getStatus().code() != 200) {
            ByteBuf buf = Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8);
            res.content().writeBytes(buf);
            buf.release();
            HttpHeaders.setContentLength(res, res.content().readableBytes());
        }
        // Send the response and close the connection if necessary.
        ChannelFuture f = ctx.channel().writeAndFlush(res);
        if (!HttpHeaders.isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }
    private static String getWebSocketLocation(FullHttpRequest req) {
        String location =  req.headers().get(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
        return "ws://" + location;
    }
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) {
        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content()));
            return;
        }

        throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }

}
