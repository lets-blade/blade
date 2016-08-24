package com.blade.kit.exception;

public class ReflectException extends RuntimeException{

	private static final long serialVersionUID = -3979699728217399193L;

	public ReflectException() {
		super();
	}
	
	public ReflectException(String msg) {
		super(msg);
	}
	
	public ReflectException(Throwable t) {
		super(t);
	}
	
	public ReflectException(String msg, Throwable t) {
		super(msg, t);
	}
}
