package com.blade.security.web.csrf;

import com.blade.mvc.route.RouteHandler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ValidToken {

    Class<? extends RouteHandler> value() default RouteHandler.class;

}