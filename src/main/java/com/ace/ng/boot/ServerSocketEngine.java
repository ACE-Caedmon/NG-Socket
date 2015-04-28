package com.ace.ng.boot;

import com.ace.ng.dispatch.CmdHandlerFactory;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.dispatch.message.DefaultCmdHandlerFactory;
import com.ace.ng.dispatch.tcp.TCPServerInitializer;
import com.ace.ng.dispatch.websocket.WsServerInitalizer;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.impl.DefaultCmdFactoryCenter;
import com.ace.ng.session.SessionFire;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2015/4/25.
 */
public class ServerSocketEngine extends SocketEngine{
    private static final Logger log= LoggerFactory.getLogger(ServerSocketEngine.class);
    private ServerSettings settings;
    public ServerSocketEngine(ServerSettings settings,CmdFactoryCenter cmdFactoryCenter) {
        super(cmdFactoryCenter);
        this.settings=settings;
    }
    public ServerSocketEngine(ServerSettings settings){
        this(settings,new DefaultCmdFactoryCenter(settings.cmdThreadSize));
    }
    /**
     * 启动网络服务
     * */
    public void startSocket(){
        log.info("NG-Socket 初始化!");
        final EventLoopGroup bossGroup = new NioEventLoopGroup(settings.bossThreadSize);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(settings.workerThreadSize);
        try {
            ChannelInitializer<SocketChannel> initializer=null;
            switch (settings.protocol.toLowerCase()){
                case TCP_PROTOCOL:
                    initializer=new TCPServerInitializer(cmdFactoryCenter,settings);
                    break;
                case WEBSOCKET_PROTOCOL:
                    initializer=new WsServerInitalizer(cmdFactoryCenter,settings);
                    break;
            }

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer);
            ChannelFuture f =  b.bind(settings.port).sync();
            log.info("Protocol type: {}",settings.protocol);
            log.info("Boss thread : {}",settings.bossThreadSize);
            log.info("Worker thread : {}",settings.workerThreadSize);
            log.info("Logic thread:{}",settings.cmdThreadSize);
            log.info("Socket package encrypt : {}", settings.encrypt);
            log.info("CmdFactoryCenter : {}", cmdFactoryCenter.getClass().getCanonicalName());
            log.info("Socket port :{}",settings.port);

            //f.channel().closeFuture().sync();
        } catch (Exception e) {
            log.error("<<<<<<<网络服务启动异常>>>>>>", e);
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            return;
        }
        //如果系统配置不加密则不发送密码表
        if(settings.encrypt){
            //用来给客户端发送密码表
            SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_LOGIN, new ValidateOKHandler());
        }
        log.info("NG-Socket 启动完毕!");
    }
}
