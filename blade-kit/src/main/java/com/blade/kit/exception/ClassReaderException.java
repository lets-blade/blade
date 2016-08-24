package com.blade.kit.exception;

/**
 * 类读取异常
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ClassReaderException extends RuntimeException {

	private static final long serialVersionUID = -1L;
	
	public ClassReaderException() {
		super();
	}
	
	public ClassReaderException(Exception e) {
		super(e);
	}
	
	public ClassReaderException(String msg) {
		super(msg);
	}
	
	public ClassReaderException(String msg, Exception e) {
		super(msg, e);
	}
	
	public Throwable fillInStackTrace() {
        return null;
    }
}