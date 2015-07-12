package com.ace.ng.proxy;

import com.ace.ng.annotation.*;
import com.ace.ng.codec.DataBuffer;
import com.ace.ng.exception.ControlMethodCreateException;
import com.ace.ng.session.ISession;
import com.ace.ng.utils.ClassUtils;
import javassist.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Caedmon on 2015/7/11.
 */
public class JavassitControlProxyFactory implements ControlProxyFactory{
    private List<Class> classes;
    private static ClassPool classPool=ClassPool.getDefault();
    private BeanAccess beanAccess;
    private Map<Integer,ControlMethodProxyCreater> proxyCreatorMap =new HashMap<>();
    public JavassitControlProxyFactory(BeanAccess beanAccess){
        this.beanAccess=beanAccess;
    }
    @Override
    public ControlMethodProxy newControlMethodProxy(int cmd, DataBuffer buffer) {
        ControlMethodProxyCreater creator= proxyCreatorMap.get(cmd);
        if(creator!=null){
            return creator.create(cmd,buffer);
        }
        return null;
    }

    @Override
    public void loadClasses(List<Class> classes) throws Exception{
        this.classes=classes;
        for(Class controlClass:classes){
            loadControlClass(controlClass);

        }
    }

    @Override
    public void loadControlClass(Class controlClass) throws Exception {
        List<Method> cmdMethods= ClassUtils.findMethodsByAnnotation(controlClass, CmdMethod.class);
        String proxyClassName=controlClass.getName()+"Proxy";
        for(Method method:cmdMethods){
            CmdMethod ma=method.getAnnotation(CmdMethod.class);
            //$1 session
            int cmd=ma.cmd();
            //要去重
            CtClass ctProxyClass=classPool.getAndRename(DefaultControlMethodProxy.class.getName(),proxyClassName+cmd);
            CtField beanFactoryField=CtField.make("private static final "+ beanAccess.getClass().getName()+" beanAccess= new "+ beanAccess.getClass().getName()+"();",ctProxyClass);
            ctProxyClass.addField(beanFactoryField);
            CtField controlField=CtField.make("private "+controlClass.getName()+" control=("+controlClass.getName()+")this.beanAccess.getBean("+controlClass.getName()+".class);",ctProxyClass);
            ctProxyClass.addField(controlField);
            CtMethod ctMethod=ctProxyClass.getDeclaredMethod("doCmd");
            StringBuilder methodBody=new StringBuilder();

            methodBody.append(getMethodInvokeSrc(method));
            CmdResponse cmdResponse=method.getAnnotation(CmdResponse.class);
            //发送消息
            if(cmdResponse!=null){
                MsgType msgType=cmdResponse.type();
                switch(msgType){
                    case JSON:
                        methodBody.append("$1.sendJSON("+cmd+",response);");
                        break;
                    case Binary:
                        methodBody.append("$1.sendBinary("+cmd+",response);");
                        break;
                    case ProtoBuf:
                        methodBody.append("$1.sendProtoBuf("+cmd+",response);");
                        break;
                    default:
                        throw new IllegalArgumentException("不支持的数据类型");
                }
            }
            System.out.println(methodBody);
            ctMethod.insertAfter(methodBody.toString());

            ctProxyClass.writeFile("javassit/");
            Class resultClass=ctProxyClass.toClass();
            ControlMethodProxyCreater creator=buildMethodProxyCreator(cmd,resultClass);
            proxyCreatorMap.put(cmd, creator);
        }
    }
    private ControlMethodProxyCreater buildMethodProxyCreator(int cmd,Class<ControlMethodProxy> proxy) throws ControlMethodCreateException{
        ControlMethodProxyCreater creator=null;
        String creatorClassName=ControlMethodProxyCreater.class.getName()+"$"+proxy.getSimpleName();
        try{
            CtClass creatorClass=classPool.getAndRename(ControlMethodProxyCreater.class.getName(), creatorClassName);
            creatorClass.setSuperclass(classPool.getCtClass(ControlMethodProxyCreater.class.getName()));
            CtMethod createMethod=creatorClass.getDeclaredMethod("create");
            createMethod.setBody("{" +
                    proxy.getName()+" proxy=new "+proxy.getName()+"($2);return proxy;"+
                    "}");
            creator=(ControlMethodProxyCreater)creatorClass.toClass().newInstance();

        }catch (Exception e){
            throw new  ControlMethodCreateException(e);
        }
        return creator;
    }
    private String getMethodInvokeSrc(Method method) throws Exception{
        //$1 session
        StringBuilder invokeSrc=new StringBuilder();
        Parameter[] parameters=method.getParameters();
        String[] invokeParams=new String[parameters.length];
        for(int i=0;i<parameters.length;i++) {
            Parameter parameter = parameters[i];
            CmdRequest cmdRequest = ClassUtils.getAnnotation(parameter, CmdRequest.class);
            boolean isCmdRequest = (cmdRequest != null);
            boolean isCmdUser = ClassUtils.hasAnnotation(parameter, CmdUser.class);
            if (isCmdUser) {
                if (parameter.getType().isAssignableFrom(ISession.class)) {
                    invokeParams[i] = "$1";
                } else {
                    //获取User对象
                    invokeParams[i] = "this.getCmdUser($1)";
                }
                continue;
            }
            //根据data解码到JavaBean,二进制流的方式
            if (isCmdRequest) {
                //注册MessageProxy
                MsgType requestType = cmdRequest.type();
                MessageProxyFactory.ONLY_INSTANCE.createMessageProxy(requestType,parameter.getType());
                String paramClassName=parameter.getType().getName();
                invokeParams[i] = "(" +paramClassName+ ")("+getGetMessageProxySrc(requestType,paramClassName)+").decode(this.content))";
            }
        }
        String methodName=method.getName();

        //是否自动将返回值发送给客户端
        Annotation cmdResponse=ClassUtils.getAnnotation(method, CmdResponse.class);
        boolean isCmdResponse=(cmdResponse!=null);
        //判断返回值类型
        Class returnType=method.getReturnType();
        if(isCmdResponse){
            invokeSrc.append(returnType.getName())
                    .append(" response= ");
        }
        invokeSrc.append("this.control." + methodName + "(");
        for(int i=0;i<invokeParams.length;i++){
            invokeSrc.append(invokeParams[i]);
            if(i<invokeParams.length-1){
                invokeSrc.append(",");
            }
            if(i==invokeParams.length-1){
                invokeSrc.append(");");
            }
        }
        return invokeSrc.toString();
    }
    private static String getGetMessageProxySrc(MsgType type,String className){
        return MessageProxyFactory.class.getName()+
                ".ONLY_INSTANCE.getMessageProxy("+MsgType.class.getName()+
                "."+type.name()+","+className + ".class";
    }
    @Override
    public BeanAccess getBeanAccess() {
        return beanAccess;
    }

    @Override
    public void setBeanAccess(BeanAccess beanAccess) {
        this.beanAccess = beanAccess;
    }
}
