package com.ace.ng.codec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Chenlong on 2014/5/22.
 * 类属性字段注解，用来表示MessageHandler中不需要自动解码的字段
 * example: 表示userid 不需要解码
 * public class User{
 *     @NotDecode
 *     private long userid;
 * }
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NotDecode {
    /**
     * @return 是否默认解码该字段
     * */
    boolean value() default true;
}
