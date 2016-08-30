package com.blade.kit.resource;

public class BladeClassLoader extends ClassLoader {

	public BladeClassLoader() {
	}
	
	public Class<?> defineClassByName(String name) throws ClassNotFoundException{
		return Class.forName(name);
	}
	
}
