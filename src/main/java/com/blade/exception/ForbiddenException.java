package com.blade.exception;

/**
 * 403 forbidden exception
 *
 * @author biezhi
 * @date 2017/9/18
 */
public class ForbiddenException extends BladeException {

    public ForbiddenException() {
        super(403, "");
    }

}
