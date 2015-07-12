package com.ace.ng.boot;

import com.ace.ng.annotation.CmdControl;
import com.ace.ng.proxy.ControlProxyFactory;
import com.ace.ng.utils.ClassUtils;
import io.netty.util.internal.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
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
    protected ControlProxyFactory controlProxyFactory;
    protected EngineSettings settings;
    public SocketEngine(EngineSettings settings,ControlProxyFactory controlProxyFactory){
        this.settings=settings;
        this.extensions=new HashSet<>();
        this.controlProxyFactory = controlProxyFactory;
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
        load();
        startSocket();

    }
    public abstract void startSocket();
    public void load(){
        for(Extension extension:extensions){
            log.info("Load extension:{}", StringUtil.simpleClassName(extension));
            extension.init();
        }
        List<Class> cmdControls=new ArrayList<>();
        //生成
        try{
            cmdControls= ClassUtils.findClassesByAnnotation(settings.scanPackage, CmdControl.class);
            controlProxyFactory.loadClasses(cmdControls);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
