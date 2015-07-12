//package com.ace.ng.boot;
//
//import com.ace.ng.dispatch.CmdHandlerFactory;
//import com.ace.ng.dispatch.message.CmdTaskInterceptor;
//import com.ace.ng.dispatch.message.DefaultCmdHandlerFactory;
//import com.ace.ng.session.ISession;
//import com.jcwx.frm.current.IActor;
//import com.jcwx.frm.current.IActorManager;
//import com.jcwx.frm.current.QueueActorManager;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.Future;
//import java.util.concurrent.ThreadFactory;
//
///**
// * Created by Administrator on 2015/4/27.
// */
//public abstract class CmdFactoryCenter<U> {
//    private CmdHandlerFactory handlerFactory;
//    public CmdFactoryCenter(int threadSize,ThreadFactory threadFactory,CmdHandlerFactory handlerFactory){
//        this.actorManager=new QueueActorManager(threadSize,threadFactory);
//        this.handlerFactory=handlerFactory;
//    }
//    public CmdFactoryCenter(int threadSize,CmdHandlerFactory handlerFactory){
//        this(threadSize, null,handlerFactory);
//    }
//    public CmdFactoryCenter(int threadSize){
//        this(threadSize,null,new DefaultCmdHandlerFactory());
//    }
//    public CmdHandlerFactory getHandlerFactory() {
//        return handlerFactory;
//    }
//
//    public IActorManager getActorManager() {
//        return actorManager;
//    }
//
////    public CmdHandler getCmdHandler(Integer cmd){
////        return (CmdHandler)this.handlerFactory.getHandler(cmd);
////    }
//    private List<CmdTaskInterceptor> interceptors=new ArrayList<>();
//    private IActorManager actorManager;
////    public void registerCmdHandler(Integer cmd,Class<? extends CmdHandler> handler){
////        handlerFactory.registerHandler(cmd, handler);
////    }
//    public void addCmdInterceptor(CmdTaskInterceptor interceptor){
//        interceptors.add(interceptor);
//    }
//    public final Future executeCmd(final ISession session,final CmdHandler<U> handler){
//        IActor actor=session.getActor();
//        if(actor==null){
//            actor=actorManager.createActor();
//            session.setActor(actor);
//        }
//        return actor.execute(new Runnable() {
//            @Override
//            public void run() {
//                boolean vaildExecute=true;
//                for(CmdTaskInterceptor interceptor:interceptors){
//                    vaildExecute=vaildExecute&&interceptor.beforeExecute(session,handler);
//                    if(!vaildExecute){
//                        return;
//                    }
//                }
//
//                try{
//                    U user=getUser(session);
//                    if(user==null){
//                        ((CmdHandler<ISession>)handler).execute(session);
//                    }else{
//                        handler.execute(user);
//                    }
//
//                }catch (Throwable cause){
//                    if(interceptors.isEmpty()){
//                        cause.printStackTrace();
//                    }else{
//                        for(CmdTaskInterceptor interceptor:interceptors){
//                            interceptor.exceptionCaught(session, handler,cause);
//                        }
//                        return;
//                    }
//                }
//                for(CmdTaskInterceptor interceptor:interceptors){
//                    interceptor.afterExecute(session,handler);
//                }
//            }
//        });
//
//    }
//    public abstract U getUser(ISession session);
//}
