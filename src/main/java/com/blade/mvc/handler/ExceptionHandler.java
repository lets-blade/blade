package com.blade.mvc.handler;

/**
 * Exception Handler interface
 *
 * @author biezhi
 * @date 2017/9/18
 */
@FunctionalInterface
public interface ExceptionHandler {

    void handle(Exception e);

}
