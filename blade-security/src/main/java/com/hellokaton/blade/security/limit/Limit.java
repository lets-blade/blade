package com.hellokaton.blade.security.limit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Limit {

    int value() default 1;

    TimeUnit unit() default TimeUnit.SECONDS;

    LimitMode mode() default LimitMode.RATE;

    boolean disable() default false;

}
