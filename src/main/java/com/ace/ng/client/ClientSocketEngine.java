package com.ace.ng.client;

import com.ace.ng.boot.Extension;
import com.ace.ng.boot.ServerSettings;
import com.ace.ng.dispatch.message.Cmd;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.HandlerFactory;
import com.ace.ng.dispatch.tcp.TCPServerInitializer;
import com.ace.ng.dispatch.websocket.WebSocketServerInitalizer;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.session.SessionFire;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2015/4/24.
 */
public class ClientSocketEngine {
    private ServerSettings settings;
    private HandlerFactory handlerFactory;
    private Set<Extension> extensions;
    private static final Logger log = LoggerFactory.getLogger(ClientSocketEngine.class);
    public ClientSocketEngine(ServerSettings settings,HandlerFactory handlerFactory){
        this.settings=settings;
        this.handlerFactory = handlerFactory;
        this.extensions=new HashSet<Extension>();
    }
    /**
     * 启动网络服务
     * */
    public void start(){
        log.info("NG-Socket 初始化!");
        final EventLoopGroup group = new NioEventLoopGroup(settings.bossThreadSize);
        try {
            ChannelInitializer<SocketChannel> initializer=null;
            switch (settings.protocol.toLowerCase()){
                case ServerSettings.TCP_PROTOCOL:
                    initializer=new TCPServerInitializer(handlerFactory,settings.cmdTaskFactory,settings.secretKey);
                    break;
                case ServerSettings.WEBSOCKET_PROTOCOL:
                    initializer=new WebSocketServerInitalizer(handlerFactory,settings.cmdTaskFactory,settings.secretKey);
                    break;
            }

            Bootstrap b = new Bootstrap();
            b.group(group).handler(new TCPServerInitializer(handlerFactory,settings.cmdTaskFactory,settings.secretKey));
            b.channel(NioSocketChannel.class);
            b.connect("localhost",settings.port);
        } catch (Exception e) {
            log.error("<<<<<<<网络服务启动异常>>>>>>", e);
            group.shutdownGracefully();
            return;
        }
        for(Extension extension:extensions){
            log.info("Load extension:{}", StringUtil.simpleClassName(extension));
            extension.init();
            List<Class<? extends CmdHandler>> classes=extension.getCmdHandlers();
            for(Class c:classes){
                Cmd annotation= (Cmd) c.getAnnotation(Cmd.class);
                if(annotation==null){
                    throw new NullPointerException("Class has no Cmd:"+c.getName());
                }
                handlerFactory.registerHandler(annotation.id(), c);
            }
        }
        //如果系统配置不加密则不发送密码表
        if(settings.encrypt){
            //用来给客户端发送密码表
            SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_LOGIN, new ValidateOKHandler());
        }
        log.info("NG-Socket 启动完毕!");
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
        extensions.add(extension);
    }
}
