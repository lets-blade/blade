package com.blade.jdbc.exception;

public class CallException extends RuntimeException {

	private static final long serialVersionUID = 4813572976178058533L;

	public CallException(Throwable t) {
		super(t);
	}
	
	public CallException(String msg) {
		super(msg);
	}
	
}