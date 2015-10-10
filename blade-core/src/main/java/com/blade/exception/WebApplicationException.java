package com.blade.exception;

public class WebApplicationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected int status;

	protected String name;

	public WebApplicationException(int status, String name) {
		this.status = status;
		this.name = name;
	}

	public WebApplicationException(int status, String name, String message) {
		super(message);
		this.status = status;
		this.name = name;
	}

	public WebApplicationException(int status, String name, Throwable cause) {
		super(cause);
		this.status = status;
		this.name = name;
	}

	public WebApplicationException(int status, String name, String message, Throwable cause) {
		super(message, cause);
		this.status = status;
		this.name = name;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public int getStatus() {
		return status;
	}

	public String getName() {
		return name;
	}
}
