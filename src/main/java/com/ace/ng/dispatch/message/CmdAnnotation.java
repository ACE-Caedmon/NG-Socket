package com.ace.ng.dispatch.message;

import java.lang.annotation.*;

/**
 * Created by Administrator on 2015/4/3.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface CmdAnnotation {
    short id();
    String desc();
}
