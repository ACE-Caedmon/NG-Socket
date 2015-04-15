/**
 * @author Chenlong
 * 确保MesageDispatcher是Sharable的才能正确运行，不能每次Channel创建New一个新的
 * 该类负责一些系统事件处理，包括将Netty层消息转到自定义框架层进行处理
 * */
package com.ace.ng.dispatch.tcp;

import com.ace.ng.constant.VarConst;
import com.ace.ng.dispatch.message.CmdHandler;
import com.ace.ng.dispatch.message.CmdTaskFactory;
import com.ace.ng.session.ISession;
import com.ace.ng.session.Session;
import com.ace.ng.session.SessionFire;
import com.jcwx.frm.current.IActor;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Sharable
public class TCPCmdDispatcher extends SimpleChannelInboundHandler<CmdHandler<?>>{
	private static final Logger log = LoggerFactory.getLogger(TCPCmdDispatcher.class);
	private CmdTaskFactory<?> taskFactory;
	public TCPCmdDispatcher(CmdTaskFactory<?> taskFactory){
		this.taskFactory=taskFactory;
	}
    /**
     * 连接断开是会调用此方法，方法会将Session相关信息移除，并且从Channel删除保存的Session对象
     * 并且会触发应用层扩展的离线时间
     * @see com.ace.ng.session.SessionFire
     * */
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		final ISession session=ctx.channel().attr(VarConst.SESSION_KEY).get();
        final IActor actor=session.getActor();
		actor.execute(new Runnable() {
            @Override
            public void run() {
                SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_DISCONNECT, session);
                session.clear();
                session.noticeCloseComplete();
				actor.releaseExecutor();
            }
        });

	}
    /**
     * 有客户端数据发送到服务端时会调用此方法，负责创建MessageTask独享，提交给自定义线程池处理业务逻辑。
     * @param  ctx Channel对应的ChannelContext
     * @param handler 用户自定义实现的CmdHandler
     * */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, CmdHandler handler)
			throws Exception {
		ISession session=ctx.channel().attr(VarConst.SESSION_KEY).get();
		taskFactory.executeCmd(session,handler);
	}
    /**
     * 连接创建时会调用此方法，此时会负责创建ISession,并且为ISession分配一个Actor
     * @see com.jcwx.frm.current.IActor
     * */
	@Override
	public void channelActive(final ChannelHandlerContext ctx) throws Exception {
		final ISession session=new Session(ctx.channel());
		taskFactory.executeCmd(session, new CmdHandler() {
			@Override
			public void excute(Object user) {
				ctx.channel().attr(VarConst.SESSION_KEY).set(session);
				SessionFire.getInstance().fireEvent(SessionFire.SessionEvent.SESSION_CONNECT, session);
			}
		});
	
	}
    /**
     * 出现异常时
     * */
	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
			throws Exception {
		ISession session=ctx.channel().attr(VarConst.SESSION_KEY).get();
		log.error("出现异常:", cause);
	}


}
