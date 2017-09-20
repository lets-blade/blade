package com.blade.mvc.handler;

/**
 * @author biezhi
 * @date 2017/9/18
 */
@FunctionalInterface
public interface ExceptionHandler {

    void handle(Throwable e);

}
