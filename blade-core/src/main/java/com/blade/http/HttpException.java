package com.blade.http;

public class HttpException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public HttpException() {
		super();
	}

	public HttpException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Throwable throwable) {
		super(throwable);
	}

}
