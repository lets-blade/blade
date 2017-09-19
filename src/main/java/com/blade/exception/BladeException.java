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

    public BladeException(int status, String name, String message) {
        super(message);
        this.status = status;
        this.name = name;
    }

}
