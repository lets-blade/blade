package com.blade.websocket;

import com.blade.exception.BladeException;

public class WebSocketException extends BladeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5109944185187744851L;

	public WebSocketException() {
		super();
	}

	public WebSocketException(String message, Throwable cause) {
		super(message, cause);
	}

	public WebSocketException(String message) {
		super(message);
	}

	public WebSocketException(Throwable cause) {
		super(cause);
	}

}
