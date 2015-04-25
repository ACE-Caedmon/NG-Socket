package com.ace.ng.dispatch.message;

import com.ace.ng.session.ISession;
import com.jcwx.frm.current.IActor;
import com.jcwx.frm.current.IActorManager;

import java.util.ArrayList;
import java.util.List;


public abstract class CmdTaskFactory<T> {
	private List<CmdTaskInterceptor> interceptors=new ArrayList<>();
	private IActorManager actorManager;
	public CmdTaskFactory(IActorManager actorManager){
		this.actorManager=actorManager;
	}
	void addCmdInterceptor(CmdTaskInterceptor interceptor){
		interceptors.add(interceptor);
	}
	public final void executeCmd(final ISession session,final CmdHandler<T> handler){
		IActor actor=session.getActor();
		if(actor==null){
			actor=actorManager.createActor();
			session.setActor(actor);
		}
		actor.execute(new Runnable() {
			@Override
			public void run() {
				boolean vaildExecute=true;
				for(CmdTaskInterceptor interceptor:interceptors){
					vaildExecute=vaildExecute&&interceptor.beforeExecute(session,handler);
					if(!vaildExecute){
						return;
					}
				}

				try{
					T user=getUser(session);
					if(user==null){
						((CmdHandler<ISession>)handler).execute(session);
					}else{
						handler.execute(getUser(session));
					}

				}catch (Throwable cause){
					for(CmdTaskInterceptor interceptor:interceptors){
						interceptor.exceptionCaught(session, handler,cause);
					}
					return;
				}
				for(CmdTaskInterceptor interceptor:interceptors){
					interceptor.afterExecute(session,handler);
				}
			}
		});

	}
	public abstract T getUser(ISession session);

}
