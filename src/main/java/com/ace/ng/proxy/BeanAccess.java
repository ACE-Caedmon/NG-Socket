package com.ace.ng.proxy;

/**
 * Created by Caedmon on 2015/7/12.
 */
public interface BeanAccess {
   <T> T getBean(Class<T> clazz) throws IllegalAccessException, InstantiationException;
}
