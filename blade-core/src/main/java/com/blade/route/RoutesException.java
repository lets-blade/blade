package com.blade.route;

/**
 * Generally thrown when a problem occurs loading the routes (different from a parsing error)
 *
 * @author German Escobar
 */
public class RoutesException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RoutesException(String message) {
		super(message);
	}

	public RoutesException(String message, Throwable cause) {
		super(message, cause);
	}

	public RoutesException(Throwable cause) {
		super(cause);
	}

}
