package com.ace.ng.proxy;

import com.ace.ng.annotation.MsgType;
import com.ace.ng.codec.ByteDataBuffer;
import com.ace.ng.codec.DataBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Caedmon on 2015/7/12.
 * 全局消息体代理工厂，只允许存在一个实例
 */
public class MessageProxyFactory {
    private Map<MsgType,Map<Class,MessageProxy>> proxCacheByMsgType=new HashMap<>();
    private ClassPool classPool=ClassPool.getDefault();
    private static final String PROXY_SUFFIX="Proxy";
    public static MessageProxyFactory ONLY_INSTANCE=new MessageProxyFactory();
    private MessageProxyFactory(){
    }
    public void init(List<MesageProxyDefination> definations) throws Exception{
        for(MesageProxyDefination defination:definations){
            createMessageProxy(defination.type,defination.clazz);
        }
    }
    public MessageProxy getMessageProxy(MsgType type,Class clazz) throws Exception{
        Map<Class,MessageProxy> proxyCache=proxCacheByMsgType.get(type);
        MessageProxy proxy=null;
        if(proxyCache!=null){
            proxy=proxyCache.get(clazz);
        }else{
            proxyCache=new HashMap<>();
            proxCacheByMsgType.put(type,proxyCache);
        }
        if(proxy==null){
            proxy=createMessageProxy(type,clazz);
            proxyCache.put(clazz,proxy);
        }
        return proxy;
    }
    public MessageProxy createMessageProxy(MsgType type,Class clazz) throws Exception{
        String className=clazz.getName();
        MessageProxy proxy=null;
        switch (type){
            case JSON:
                String proxyClassName=className+MsgType.JSON+PROXY_SUFFIX;
                CtClass ctClass=classPool.getOrNull(proxyClassName);
                if(ctClass==null){
                    ctClass=classPool.getAndRename(MessageProxy.class.getName(),proxyClassName);
                    ctClass.setSuperclass(classPool.getCtClass(MessageProxy.class.getName()));
                    //encode $1 data
                    CtMethod decodeMethod=ctClass.getDeclaredMethod("decode");
                    String decodeMethodBody="{"+className+" result = ("+className+")$1.readJSON("+className+".class);return result;}";
                    decodeMethod.setBody(decodeMethodBody);
                    //encode $1 bean
                    CtMethod encodeMethod=ctClass.getDeclaredMethod("encode");
                    String encodeMethodBody="{"+
                            ByteBuf.class.getName()+" buffer = "+
                            PooledByteBufAllocator.class.getName()+".DEFAULT.buffer();"+
                            DataBuffer.class.getName()+" data=new "+ByteDataBuffer.class.getName()+"(buffer);"+
                            "data.writeJSON($1);return data;}";
                    encodeMethod.setBody(encodeMethodBody.toString());
                    ctClass.writeFile("javassit/");
                }
                proxy=(MessageProxy)ctClass.toClass().newInstance();
                break;
            case Binary:
                break;
            case ProtoBuf:
                break;
        }
        if(proxy!=null){
            Map<Class,MessageProxy> proxyCache=proxCacheByMsgType.get(type);
            if(proxyCache==null){
                proxyCache=new HashMap<>();
                proxCacheByMsgType.put(type,proxyCache);
            }
            proxyCache.put(clazz,proxy);
        }

        return proxy;
    }
}
