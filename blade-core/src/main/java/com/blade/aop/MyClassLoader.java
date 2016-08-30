package com.blade.aop;
public class MyClassLoader extends ClassLoader {
	public MyClassLoader() {
		super(Thread.currentThread().getContextClassLoader());
	}

	public Class<?> defineClassForName(String name, byte[] data) {
		return this.defineClass(name, data, 0, data.length);
	}
	
}