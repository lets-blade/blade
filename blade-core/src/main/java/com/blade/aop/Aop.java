package com.blade.aop;

import java.util.HashSet;
import java.util.Set;

import com.blade.kit.reflect.ReflectKit;

public class Aop {

	private Set<String> packages = new HashSet<String>();

	public Aop() {
		packages.add("com.javachina.controller.api");
	}
	
	public void addPackage(AopConfig aopConfig){
		packages.addAll(aopConfig.getPackages());
	}
	
	public void addPackage(String packageName){
		packages.add(packageName);
	}
	
	public Object createBean(Class<?> clazz) {
		try {
			Object value = ReflectKit.newInstance(clazz);
			return value;
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

}
