package com.blade.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Config annotation can be injected
 *
 * @author <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 */
@Target({ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Value {
    /**
     * config for key
     * @return
     */
    String name() default "";
}
