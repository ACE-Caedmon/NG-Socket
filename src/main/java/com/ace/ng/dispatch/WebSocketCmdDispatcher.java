package com.ace.ng.dispatch;

import com.ace.ng.codec.encrypt.EncryptUtil;
import com.ace.ng.constant.VarConst;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTask;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.dispatch.message.TCPHandlerFactory;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.session.SessionFire;
import com.google.protobuf.InvalidProtocolBufferException;
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

import java.util.List;

/**
 * Created by Administrator on 2015/4/3.
 */
public class WebSocketCmdDispatcher extends SimpleChannelInboundHandler{

    private static final String WEBSOCKET_PATH = "/websocket";

    private WebSocketServerHandshaker handshaker;
    private CmdHandlerFactory<Short,? extends CmdHandler> handlerFactory;
    private CmdTaskFactory<?> taskFactory;
    private static final Logger log= LoggerFactory.getLogger(WebSocketCmdDispatcher.class);
    public WebSocketCmdDispatcher(TCPHandlerFactory handlerFactory){
        this.handlerFactory=handlerFactory;
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String address =ctx.channel().remoteAddress().toString();
        super.channelActive(ctx);
    }
    @Override
    public void channelInactive(final ChannelHandlerContext ctx) throws Exception {
        final ISession session=new Session(ctx.channel());
        session.setActor(taskFactory.getActorManager().createActor());
        Runnable runnable=new Runnable(){
            @Override
            public void run() {
                ctx.channel().attr(VarConst.SESSION_KEY).set(session);
                SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_CONNECT, session);
            }
        };
        session.getActor().execute(runnable);
    }
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }
    @Override
    public void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            handleHttpRequest(ctx, (FullHttpRequest) msg);
        } else if (msg instanceof WebSocketFrame) {
            try {
                handleWebSocketFrame(ctx, (WebSocketFrame) msg);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
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
            ByteBuf content = WebSocketServerIndexPage.getContent(getWebSocketLocation(req));
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
        // Handshake
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(req), null, false);
        handshaker = wsFactory.newHandshaker(req);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel());
        } else {
            handshaker.handshake(ctx.channel(), req);
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
    private void handleWebSocketFrame(ChannelHandlerContext ctx, WebSocketFrame frame) throws InvalidProtocolBufferException {

        if (frame instanceof CloseWebSocketFrame) {
            handshaker.close(ctx.channel(), (CloseWebSocketFrame) frame.retain());
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            ctx.channel().write(new PongWebSocketFrame(frame.content().retain()));
            return;
        }
        if(frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame bw=(BinaryWebSocketFrame)frame;
            ByteBuf in = bw.content();
            boolean isEncrypt=in.readBoolean();
            ISession session=ctx.channel().attr(VarConst.SESSION_KEY).get();
            byte entryptOffset=in.readByte();
            ByteBuf bufForDecode=Unpooled.buffer(in.readableBytes() - 1);//用来缓存一条报文的ByteBuf
            if(isEncrypt){
                byte[] dst=new byte[in.readableBytes()-1];//存储包体
                in.readBytes(dst);//读取包体内容
                short index = (short) (entryptOffset < 0 ? (256 + entryptOffset): entryptOffset);//获取密码表索引
                List<Short> passportList=(List<Short>)session.getVar(VarConst.PASSPORT);//得到密码表集合
                short passport=passportList.get(index);//得到密码
                EncryptUtil.decode(dst, dst.length, EncryptUtil.KEY, passport);//解密
                bufForDecode.writeBytes(dst);
            }else{
                bufForDecode.writeBytes(in,in.readableBytes()-1);
            }
            int ci=bufForDecode.readInt();//获取消息中的自增ID
            if(session.containsVar(VarConst.INCREMENT)){//如果已存在自增ID
                int si=(Integer)session.getVar(VarConst.INCREMENT);
                if(ci==si){//判断客户端传送自增ID是否与服务器相等
                    si=ci+1;//自增
                    session.setVar(VarConst.INCREMENT, si);
                }else{
                    log.error("自增ID不合法:ci ={},si={}", ci, si);
                    bufForDecode.skipBytes(bufForDecode.readableBytes());
                }
            }else{
                session.setVar(VarConst.INCREMENT, ci + 1);
            }
            short cmd=bufForDecode.readShort();
            CmdHandler handler=handlerFactory.getHandler(cmd);
            CmdTask task=taskFactory.createMessageTask(session, handler);
            session.getActor().execute(task);
            return;
        }
//        if (frame instanceof PongWebSocketFrame) {
//            BinaryWebSocketFrame request = ((BinaryWebSocketFrame) frame).text();
//            ctx.channel().write(new ByteBuf(request.toUpperCase()));
//            return;
//        }
        throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }



    public static void main(String[] args) {


    }
}
