package com.blade.render;
public class RenderException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RenderException() {
		super();
	}

	public RenderException(String message, Throwable cause) {
		super(message, cause);
	}

	public RenderException(String message) {
		super(message);
	}

	public RenderException(Throwable cause) {
		super(cause);
	}

}
