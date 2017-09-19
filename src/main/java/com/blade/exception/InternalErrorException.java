package com.blade.exception;

/**
 * 500 internal error exception
 *
 * @author biezhi
 * @date 2017/9/18
 */
public class InternalErrorException extends BladeException {

    private static final int STATUS = 500;
    private static final String NAME = "Internal Error";

    public InternalErrorException() {
        super(STATUS, NAME);
    }

    public InternalErrorException(String message) {
        super(STATUS, NAME, message);
    }

}
