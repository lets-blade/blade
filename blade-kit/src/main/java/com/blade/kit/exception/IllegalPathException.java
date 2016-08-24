package com.blade.kit.exception;

/**
 * 代表非法的路径。
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class IllegalPathException extends IllegalArgumentException {

    /**
	 * 
	 */
    private static final long serialVersionUID = 1434004725746713564L;

    public IllegalPathException() {
        super();
    }

    public IllegalPathException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalPathException(String s) {
        super(s);
    }

    public IllegalPathException(Throwable cause) {
        super(cause);
    }

    public Throwable fillInStackTrace() {
        return null;
    }
}