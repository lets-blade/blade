package com.blade;

import blade.kit.ReflectKit;

@SuppressWarnings("unchecked")
public final class Aop {
	
	private static boolean isAop = false;
	
	public static <T> T create(Class<T> clazz){
		if(isAop){
			//...aop
		} else {
			Object object = ReflectKit.newInstance(clazz);
			if(null != object){
				return (T) object;
			}
		}
		return null;
	}
	
	public static <T> T create(String className){
		try {
			Class<?> clazz = Class.forName(className);
			if(isAop){
				//...aop
			} else {
				Object object = ReflectKit.newInstance(clazz);
				if(null != object){
					return (T) object;
				}
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
}