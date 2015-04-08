package com.ace.ng.boot;

import com.ace.ng.dispatch.CmdDispatcher;
import com.ace.ng.dispatch.ServerChannelInitializer;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.TCPHandlerFactory;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.session.SessionFire;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Chenlong on 2014/5/19.
 * 网络引擎核心启动类
 */
public class SocketEngine {
    private ServerSettings settings;
    private TCPHandlerFactory tcpHandlerFactory;
    private Set<Extension> extensions;
    private static final Logger log =LoggerFactory.getLogger(SocketEngine.class);
    public SocketEngine(ServerSettings settings,TCPHandlerFactory tcpHandlerFactory){
        this.settings=settings;
        this.tcpHandlerFactory=tcpHandlerFactory;
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
            CmdDispatcher dispatcher=new CmdDispatcher(settings.cmdTaskFactory);
            ServerChannelInitializer initializer=new ServerChannelInitializer(dispatcher,tcpHandlerFactory);
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(initializer);
            // Bind and start to accept incoming connections.
            ChannelFuture f =  b.bind(settings.port).sync();
            log.info("Boss thread : {}",settings.bossThreadSize);
            log.info("Worker thread : {}",settings.workerThreadSize);
            log.info("Logic thread:{}",settings.messageThreadSize);
            log.info("Socket package encrypt : {}", settings.encrypt);
            log.info("MessageFactory : {}", settings.cmdTaskFactory.getClass().getCanonicalName());
            log.info("TCP port :{}",settings.port);
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
    public void registerHandler(short cmd,Class<? extends CmdHandler> handler){
        tcpHandlerFactory.registerHandler(cmd,handler);
    }
    /**
     * 停止网络服务
     * */
    public void shutdown(){
        for(Extension extension:extensions){
            extension.destory();
        }
        extensions.clear();
        tcpHandlerFactory.destory();
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
        Map<Short,Class> handlerMap=extension.getCmdHandlers();
        for(Map.Entry<Short,Class> entry:handlerMap.entrySet()){
            tcpHandlerFactory.registerHandler(entry.getKey(),entry.getValue());
        }

    }
}
