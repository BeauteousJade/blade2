package com.blade.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Inject {
    String value() default "";

    /**
     * 是否使用默认值，true表示使用默认值，在注入数据为空的时候，不会报错。
     *
     * 原生类型的默认值为0或者false，引用类型的默认值为null,
     */
    boolean useDefault() default false;
}
