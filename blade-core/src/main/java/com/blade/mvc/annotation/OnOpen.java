package com.blade.mvc.annotation;

import java.lang.annotation.*;

/**
 * @author darren
 * @description invoke websocketHandler onConnect method
 * @date 2018/12/17 18:41
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OnOpen {
}
