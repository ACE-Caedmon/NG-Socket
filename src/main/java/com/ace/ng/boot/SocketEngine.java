package com.ace.ng.boot;

import com.ace.ng.dispatch.message.Cmd;
import com.ace.ng.dispatch.message.CmdHandler;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Chenlong on 2014/5/19.
 * 网络引擎核心启动类
 */
public abstract class SocketEngine {
    protected Set<Extension> extensions;
    private static Logger log= LoggerFactory.getLogger(SocketEngine.class);
    public static final String TCP_PROTOCOL="tcp",WEBSOCKET_PROTOCOL="websocket";
    protected CmdFactoryCenter cmdFactoryCenter;
    public SocketEngine(CmdFactoryCenter cmdFactoryCenter){
        this.extensions=new HashSet<>();
        this.cmdFactoryCenter=cmdFactoryCenter;
    }
    /**
     * 停止网络服务
     * */
    public void shutdown(){
        for(Extension extension:extensions){
            extension.destory();
        }
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
    public void start(){
        startSocket();
        loadExtensions();
    }
    public abstract void startSocket();
    public void loadExtensions(){
        for(Extension extension:extensions){
            log.info("Load extension:{}", StringUtil.simpleClassName(extension));
            extension.init();
            List<Class<? extends CmdHandler>> classes=extension.getCmdHandlers();
            for(Class c:classes){
                Cmd annotation= (Cmd) c.getAnnotation(Cmd.class);
                if(annotation==null){
                    throw new NullPointerException("Class has no Cmd:"+c.getName());
                }
                cmdFactoryCenter.registerCmdHandler(annotation.id(),c);
            }
        }
    }
}
