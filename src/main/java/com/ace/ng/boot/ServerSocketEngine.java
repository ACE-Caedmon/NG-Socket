package com.ace.ng.boot;

import com.ace.ng.dispatch.tcp.TCPServerInitializer;
import com.ace.ng.dispatch.websocket.WsServerInitalizer;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.proxy.BeanAccess;
import com.ace.ng.proxy.ControlProxyFactory;
import com.ace.ng.proxy.JavassitControlProxyFactory;
import com.ace.ng.proxy.PrototypeBeanAccess;
import com.ace.ng.session.SessionFire;
import com.ace.ng.utils.NGSocketParams;
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
    public ServerSocketEngine(ServerSettings settings,ControlProxyFactory controlProxyFactory) {
        super(settings,controlProxyFactory);
        this.settings=settings;
    }
    public ServerSocketEngine(ServerSettings settings){
        this(settings,new JavassitControlProxyFactory(new PrototypeBeanAccess()));
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
                    initializer=new TCPServerInitializer(controlProxyFactory);
                    break;
                case WEBSOCKET_PROTOCOL:
                    initializer=new WsServerInitalizer(controlProxyFactory);
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
            log.info("Socket package encrypt : {}", NGSocketParams.SOCKET_PACKET_ENCRYPT);
            log.info("CmdFactoryCenter : {}", controlProxyFactory.getClass().getCanonicalName());
            log.info("Socket port :{}",settings.port);

            //f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
            if(log.isErrorEnabled()){
                log.error("<<<<<<<网络服务启动异常>>>>>>", e);
            }
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
            return;
        }
        //如果系统配置不加密则不发送密码表
        if(NGSocketParams.SOCKET_PACKET_ENCRYPT){
            //用来给客户端发送密码表
            SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_LOGIN, new ValidateOKHandler());
        }
        log.info("NG-Socket 启动完毕!");
    }
}
