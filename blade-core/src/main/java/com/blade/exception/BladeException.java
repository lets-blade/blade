package com.blade.exception;

public class BladeException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public BladeException() {
		super();
	}
	
	public BladeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public BladeException(String message, Throwable cause) {
		super(message, cause);
	}

	public BladeException(String message) {
		super(message);
	}

	public BladeException(Throwable cause) {
		super(cause);
	}
	
}
