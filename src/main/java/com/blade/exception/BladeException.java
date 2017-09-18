package com.blade.exception;

import lombok.Data;

/**
 * Blade Exception
 *
 * @author biezhi
 * 2017/5/31
 */
@Data
public class BladeException extends RuntimeException {

    protected int    status;
    protected String name;

    public BladeException(int status, String name) {
        this.status = status;
        this.name = name;
    }

    public BladeException(int status, Throwable t) {
        this.status = status;
        this.name = name;
    }

    public BladeException(String message, int status, String name) {
        super(message);
        this.status = status;
        this.name = name;
    }

    public BladeException(String message, Throwable cause, int status, String name) {
        super(message, cause);
        this.status = status;
        this.name = name;
    }

    public BladeException(Throwable cause, int status, String name) {
        super(cause);
        this.status = status;
        this.name = name;
    }

}
