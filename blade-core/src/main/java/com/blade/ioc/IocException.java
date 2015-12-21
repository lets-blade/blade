package com.blade.ioc;

public class IocException extends RuntimeException {

	private static final long serialVersionUID = -1896357479295031509L;

	public IocException() {
	}
	
	public IocException(String msg) {
		super(msg);
	}
	
	public IocException(Throwable t) {
		super(t);
	}
	
	public IocException(String msg, Throwable t) {
		super(msg, t);
	}
	
}
