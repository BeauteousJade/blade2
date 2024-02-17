package com.blade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Module {
    /**
     * 当前注入源是根注入源。
     * 判断注入源是否是根注入源，就看该类是否继承于其他被本注解修饰的类，
     * 如果有继承，则不是根，反之则是。
     */
    boolean isRoot() default true;
}
