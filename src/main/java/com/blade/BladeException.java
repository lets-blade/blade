package com.blade;

/**
 * Blade Runtime Exception
 *
 * @author biezhi
 *         2017/5/31
 */
public class BladeException extends RuntimeException {

    public BladeException() {
    }

    public BladeException(String message) {
        super(message);
    }

    public BladeException(String message, Throwable cause) {
        super(message, cause);
    }

    public BladeException(Throwable cause) {
        super(cause);
    }
}
