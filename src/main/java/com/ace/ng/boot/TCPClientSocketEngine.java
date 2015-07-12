package com.ace.ng.boot;

import com.ace.ng.dispatch.tcp.TCPClientInitializer;
import com.ace.ng.handler.ValidateOKHandler;
import com.ace.ng.proxy.ControlProxyFactory;
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

/**
 * Created by Administrator on 2015/4/25.
 */
public class TCPClientSocketEngine extends SocketEngine{
    private static final Logger log= LoggerFactory.getLogger(TCPClientSocketEngine.class);
    private EventLoopGroup eventExecutors;
    private Channel channel;
    public TCPClientSocketEngine(TCPClientSettings settings,ControlProxyFactory controlProxyFactory) {
        super(settings,controlProxyFactory);
    }
    public TCPClientSocketEngine(TCPClientSettings settings,ControlProxyFactory controlProxyFactory,EventLoopGroup eventExecutors) {
        super(settings,controlProxyFactory);
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
            ChannelInitializer<SocketChannel> initializer=new TCPClientInitializer(this.controlProxyFactory,(TCPClientSettings)settings);;
            Bootstrap b = new Bootstrap();
            b.group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .handler(initializer);
            ChannelFuture f =b.connect(((TCPClientSettings)settings).host,settings.port);
            this.channel=f.sync().channel();
            log.info("Protocol type: {}",TCP_PROTOCOL);
            log.info("Worker thread : {}",settings.workerThreadSize);
            log.info("Logic thread:{}",settings.cmdThreadSize);
            log.info("Socket package encrypt : {}", NGSocketParams.SOCKET_PACKET_ENCRYPT);
            log.info("CmdFactoryCenter : {}", controlProxyFactory.getClass().getCanonicalName());
            log.info("Socket port :{}",settings.port);

        } catch (Exception e) {
            e.printStackTrace();
            if(log.isErrorEnabled()){
                log.error("<<<<<<<网络服务启动异常>>>>>>", e);
            }
            workerGroup.shutdownGracefully();
            return;
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
