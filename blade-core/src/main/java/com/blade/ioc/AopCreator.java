package com.blade.ioc;

import com.blade.aop.AopProxy;

public final class AopCreator {
	
	/**
	 * 根据Class创建一个代理
	 * @param clazz	Class对象
	 * @return		返回代理对象
	 */
	public static <T> T create(Class<?> clazz){
		return AopProxy.create(clazz);
	}
	
	/**
	 * 根据Class创建一个代理
	 * @param clazz	Class对象
	 * @return		返回代理对象
	 */
	public static Object createProxy(Class<?> clazz){
		return AopProxy.create(clazz);
	}
	
	/**
	 * 创建一个代理对象
	 * @param target	原始java对象
	 * @return			返回代理对象
	 */
	public static <T> T create(Object target){
		return AopProxy.create(target);
	}
	
	/**
	 * 创建一个代理对象
	 * @param target	原始java对象
	 * @return			返回代理对象
	 */
	public static Object createProxy(Object target){
		return AopProxy.create(target);
	}
	
}
