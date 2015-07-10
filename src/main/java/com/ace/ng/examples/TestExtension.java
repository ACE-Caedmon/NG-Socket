package com.ace.ng.examples;

import com.ace.ng.boot.Extension;
import com.ace.ng.codec.CustomBuf;
import com.ace.ng.codec.Output;
import com.ace.ng.event.IEventHandler;
import com.ace.ng.session.ISession;
import com.ace.ng.session.SessionFire;

/**
 * Created by Administrator on 2014/6/9.
 */
public class TestExtension extends Extension {
    @Override
    public void init() {
        SessionFire.getInstance().registerEvent(SessionFire.SessionEvent.SESSION_CONNECT, new IEventHandler<ISession>() {
            @Override
            public void handleEvent(ISession entity) {
                entity.send((short) 1, new Output() {
                    @Override
                    public void encode(CustomBuf buf) {
                        buf.writeLong(1);
                    }
                });
            }
        });
    }
}
