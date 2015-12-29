package com.blade.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.List;

import blade.kit.ReflectKit;
import blade.kit.log.Logger;

public class AspectFactory {

	private static final Logger logger = Logger.getLogger(AspectFactory.class);

	/**
	 * 私有构造方法
	 */
	private AspectFactory() {
	}

	/**
	 * 工厂方法
	 *
	 * @param target
	 *            代理目标对象
	 * @param aspects
	 *            切面集合
	 */
	public static Object newInstance(Object target, List<Aspect> aspects) {
		AspectHandler hander = new AspectHandler(target, aspects);
		Class<?> clazz = target.getClass();
		if (logger.isDebugEnabled()) {
			logger.debug("Instance of " + clazz + "," + clazz.getInterfaces());
		}
		return Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), hander);
	}
	
	public static <T> T newProxy(Class<T> target, List<Aspect> aspects) {
		Object object = ReflectKit.newInstance(target);
		AspectHandler handler = new AspectHandler(object, aspects);
		
		object = Proxy.newProxyInstance(target.getClassLoader(), new Class<?>[] { target }, handler);
		return target.cast(object);
	}
}