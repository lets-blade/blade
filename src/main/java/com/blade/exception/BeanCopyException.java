package com.blade.exception;

/**
 * @author biezhi
 * @date 2018/4/9
 */
public class BeanCopyException extends RuntimeException {

    public BeanCopyException() {
    }

    public BeanCopyException(String message) {
        super(message);
    }

    public BeanCopyException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeanCopyException(Throwable cause) {
        super(cause);
    }
}
