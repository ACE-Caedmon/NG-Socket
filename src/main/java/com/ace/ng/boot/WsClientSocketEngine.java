package com.ace.ng.boot;

import com.ace.ng.dispatch.websocket.WsClientInitalizer;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.session.SessionFire;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;

/**
 * Created by Administrator on 2015/4/25.
 */
public class WsClientSocketEngine extends SocketEngine{
    private static final Logger log= LoggerFactory.getLogger(TCPClientSocketEngine.class);
    private WsClientSettings settings;
    private ISession session;
    private Channel channel;
    private EventLoopGroup eventExecutors;
    public WsClientSocketEngine(WsClientSettings settings, CmdFactoryCenter cmdFactoryCenter) {
        super(cmdFactoryCenter);
        this.settings=settings;
    }
    public WsClientSocketEngine(WsClientSettings settings,CmdFactoryCenter cmdFactoryCenter,EventLoopGroup eventExecutors){
        super(cmdFactoryCenter);
        this.settings=settings;
        this.eventExecutors=eventExecutors;
    }
    @Override
    public void startSocket() {
        log.info("NG-Socket WebSocket Client 初始化!");
        EventLoopGroup workerGroup =null;
        if(eventExecutors==null){
            workerGroup=new NioEventLoopGroup(settings.workerThreadSize);
        }else{
            workerGroup=eventExecutors;
        }
        try {
            ChannelInitializer<SocketChannel> initializer=new WsClientInitalizer(settings,cmdFactoryCenter);;
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(initializer);
            URI uri=new URI(settings.url);
            this.channel=b.connect(uri.getHost(),uri.getPort()).sync().channel();
            log.info("Protocol type: {}",WEBSOCKET_PROTOCOL);
            log.info("Worker thread : {}",settings.workerThreadSize);
            log.info("Logic thread:{}",settings.cmdThreadSize);
            log.info("Socket package encrypt : {}", settings.encrypt);
            log.info("CmdFatoryCenter : {}", cmdFactoryCenter.getClass().getCanonicalName());
            log.info("Socket port :{}",uri.getPort());
        } catch (Exception e) {
            log.error("<<<<<<<网络服务启动异常>>>>>>", e);
            workerGroup.shutdownGracefully();
            return;
        }
        //如果系统配置不加密则不发送密码表
        if(settings.encrypt){
            //用来给客户端发送密码表
            SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_LOGIN, new ValidateOKHandler());
        }
        log.info("NG-Socket WebSocket Client 启动完毕!");
    }
    public ISession getSession(){
        return channel.attr(Session.SESSION_KEY).get();
    }
}
