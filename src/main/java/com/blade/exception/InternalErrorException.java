package com.blade.exception;

/**
 * 500 internal error exception
 *
 * @author biezhi
 * @date 2017/9/18
 */
public class InternalErrorException extends BladeException {

    public InternalErrorException(String name) {
        super(500, name);
    }

}
