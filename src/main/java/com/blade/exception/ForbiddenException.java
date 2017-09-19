package com.blade.exception;

/**
 * 403 forbidden exception
 *
 * @author biezhi
 * @date 2017/9/18
 */
public class ForbiddenException extends BladeException {

    private static final int STATUS = 403;
    private static final String NAME = "Forbidden";

    public ForbiddenException() {
        super(STATUS, NAME);
    }

    public ForbiddenException(String message){
        super(STATUS, NAME, message);
    }

}
