package com.blade.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import net.sf.cglib.proxy.MethodInterceptor;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Aop{
	
	Class<? extends MethodInterceptor> value();
    
	String methodPrefix() default "";
	
}