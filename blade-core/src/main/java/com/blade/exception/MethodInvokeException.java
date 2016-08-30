package com.blade.exception;

public class MethodInvokeException extends BladeException {
	
	private static final long serialVersionUID = 1L;

	public MethodInvokeException() {
		super();
	}

	public MethodInvokeException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public MethodInvokeException(String message, Throwable cause) {
		super(message, cause);
	}

	public MethodInvokeException(String message) {
		super(message);
	}

	public MethodInvokeException(Throwable cause) {
		super(cause);
	}

}
