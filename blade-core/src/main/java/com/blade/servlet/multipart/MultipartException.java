package com.blade.servlet.multipart;

public class MultipartException extends Exception {

	private static final long serialVersionUID = 1L;

	public MultipartException() {
	}

	public MultipartException(String message) {
		super(message);
	}

	public MultipartException(Throwable cause) {
		super(cause);
	}

	public MultipartException(String message, Throwable cause) {
		super(message, cause);
	}

}
