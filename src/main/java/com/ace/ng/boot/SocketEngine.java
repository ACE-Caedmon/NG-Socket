package com.ace.ng.boot;

import com.ace.ng.dispatch.message.CmdAnnotation;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.HandlerFactory;
import com.ace.ng.dispatch.tcp.TCPServerInitializer;
import com.ace.ng.dispatch.websocket.WebSocketServerInitalizer;
import com.ace.ng.handler.ValidateOKHandler;
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Chenlong on 2014/5/19.
 * 网络引擎核心启动类
 */
public class SocketEngine {
    private ServerSettings settings;
    private HandlerFactory handlerFactory;
    private Set<Extension> extensions;
    private static final Logger log =LoggerFactory.getLogger(SocketEngine.class);
    private static final String TCP_PROTOCOL="tcp",WEB_SOCKET_PROTOCOL="websocket";
    public SocketEngine(ServerSettings settings,HandlerFactory handlerFactory){
        this.settings=settings;
        this.handlerFactory = handlerFactory;
        this.extensions=new HashSet<Extension>();
    }
    /**
     * 启动网络服务
     * */
    public void start(){
        log.info("NG-Socket 初始化!");
        final EventLoopGroup bossGroup = new NioEventLoopGroup(settings.bossThreadSize);
        final EventLoopGroup workerGroup = new NioEventLoopGroup(settings.workerThreadSize);
        try {
            ChannelInitializer<SocketChannel> initializer=null;
            switch (settings.protocol.toLowerCase()){
                case TCP_PROTOCOL:
                    initializer=new TCPServerInitializer(handlerFactory,settings.cmdTaskFactory);
                    break;
                case WEB_SOCKET_PROTOCOL:
                    initializer=new WebSocketServerInitalizer(handlerFactory,settings.cmdTaskFactory);
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
            log.info("Logic thread:{}",settings.messageThreadSize);
            log.info("Socket package encrypt : {}", settings.encrypt);
            log.info("MessageFactory : {}", settings.cmdTaskFactory.getClass().getCanonicalName());
            log.info("Socket port :{}",settings.port);
            log.info("NG-Socket 启动完毕!");
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
    }
    /**
     * 注册消息处理器
     * @param cmd 指令ID
     * @param handler 处理器Class
     * */
    public void registerHandler(short cmd,Class<? extends CmdHandler<?>> handler){
        handlerFactory.registerHandler(cmd, handler);
    }
    /**
     * 停止网络服务
     * */
    public void shutdown(){
        for(Extension extension:extensions){
            extension.destory();
        }
        extensions.clear();
        handlerFactory.destory();
        extensions.clear();
    }
    /**
     * 注册功能模块
     * @param  extension 功能模块
     * */
    public void registerExtension(Extension extension){
        for(Extension e:extensions){
            if(e.getClass().getName().equals(extension.getClass().getName())){
                throw new IllegalArgumentException("重复加载Extension( extension = "+extension.getClass().getName()+")");
            }
        }
        extension.init();
        List<Class<? extends CmdHandler>> classes=extension.getCmdHandlers();
        for(Class c:classes){
            CmdAnnotation annotation= (CmdAnnotation) c.getAnnotation(CmdAnnotation.class);
            if(annotation==null){
                throw new NullPointerException("Class has no CmdAnnotation:"+c.getName());
            }
            handlerFactory.registerHandler(annotation.id(), c);
        }

    }
}
