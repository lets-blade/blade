package com.blade.exception;

/**
 * 404 not found exception
 *
 * @author biezhi
 * @date 2017/9/18
 */
public class NotFoundException extends BladeException {

    private static final int    STATUS = 404;
    private static final String NAME   = "Not Found";

    public NotFoundException() {
        super(STATUS, NAME);
    }

    public NotFoundException(String message) {
        super(STATUS, NAME, message);
    }

}
