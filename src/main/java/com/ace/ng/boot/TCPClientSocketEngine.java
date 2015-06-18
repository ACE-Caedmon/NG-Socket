package com.ace.ng.boot;

import com.ace.ng.dispatch.message.Cmd;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.tcp.TCPClientInitializer;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.session.SessionFire;
import com.ace.ng.utils.NGSocketParams;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created by Administrator on 2015/4/25.
 */
public class TCPClientSocketEngine extends SocketEngine{
    private static final Logger log= LoggerFactory.getLogger(TCPClientSocketEngine.class);
    private TCPClientSettings settings;
    private EventLoopGroup eventExecutors;
    private Channel channel;
    public TCPClientSocketEngine(TCPClientSettings settings,CmdFactoryCenter cmdFactoryCenter) {
        super(cmdFactoryCenter);
        this.settings=settings;
    }
    public TCPClientSocketEngine(TCPClientSettings settings,CmdFactoryCenter cmdFactoryCenter,EventLoopGroup eventExecutors) {
        super(cmdFactoryCenter);
        this.settings=settings;
        this.eventExecutors=eventExecutors;
    }
    @Override
    public void startSocket() {
        log.info("NG-Socket TCP Client 初始化!");
        EventLoopGroup workerGroup=null;
        if(this.eventExecutors==null){
            workerGroup= new NioEventLoopGroup(settings.workerThreadSize);
        }else{
            workerGroup=this.eventExecutors;
        }
        try {
            ChannelInitializer<SocketChannel> initializer=new TCPClientInitializer(this.cmdFactoryCenter,settings);;
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(initializer);
            ChannelFuture f =b.connect(settings.host,settings.port);
            this.channel=f.sync().channel();
            log.info("Protocol type: {}",TCP_PROTOCOL);
            log.info("Worker thread : {}",settings.workerThreadSize);
            log.info("Logic thread:{}",settings.cmdThreadSize);
            log.info("Socket package encrypt : {}", NGSocketParams.SOCKET_PACKET_ENCRYPT);
            log.info("CmdFactoryCenter : {}", cmdFactoryCenter.getClass().getCanonicalName());
            log.info("Socket port :{}",settings.port);

        } catch (Exception e) {
            e.printStackTrace();
            if(log.isErrorEnabled()){
                log.error("<<<<<<<网络服务启动异常>>>>>>", e);
            }
            workerGroup.shutdownGracefully();
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
                cmdFactoryCenter.registerCmdHandler(annotation.id(), c);
            }
        }
        //如果系统配置不加密则不发送密码表
        if(NGSocketParams.SOCKET_PACKET_ENCRYPT){
            //用来给客户端发送密码表
            SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_LOGIN, new ValidateOKHandler());
        }
        log.info("NG-Socket TCP Client 启动完毕!");
    }
    public Channel getChannel(){
        return this.channel;
    }
}
