package com.blade.ioc.annotation;

import java.lang.annotation.*;

/**
 * @author <a href="mailto:fishlikewater@126.com" target="_blank">zhangx</a>
 * @since 2.0.15
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Configuration {

    String name() default "";
}
