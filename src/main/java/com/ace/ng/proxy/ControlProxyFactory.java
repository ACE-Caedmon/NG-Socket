package com.ace.ng.proxy;

import com.ace.ng.codec.DataBuffer;

import java.util.List;

/**
 * Created by Caedmon on 2015/7/11.
 */
public interface ControlProxyFactory {
    ControlMethodProxy newControlMethodProxy(int cmd, DataBuffer buffer);
    void loadClasses(List<Class> classes) throws Exception;
    void loadControlClass(Class controlClass) throws Exception;
    BeanAccess getBeanAccess();
    void setBeanAccess(BeanAccess beanAccess);
}
