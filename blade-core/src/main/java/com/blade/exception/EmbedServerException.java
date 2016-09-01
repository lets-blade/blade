package com.blade.exception;

public class EmbedServerException extends BladeException {

	private static final long serialVersionUID = 1L;

	public EmbedServerException() {
		super();
	}

	public EmbedServerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public EmbedServerException(String message, Throwable cause) {
		super(message, cause);
	}

	public EmbedServerException(String message) {
		super(message);
	}

	public EmbedServerException(Throwable cause) {
		super(cause);
	}

}
