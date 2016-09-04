package com.blade.aop;

import java.lang.reflect.Method;

import com.blade.aop.annotation.Aop;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public abstract class AbstractMethodInterceptor implements MethodInterceptor {

	public abstract Object doInvoke(Invocaction invocaction) throws Throwable;
	
	public void before(Invocaction invocaction) {}

	public void after(Invocaction invocaction) {}

	/**
	 * 切面逻辑 obj 代理对象实例 method 源对象的方法名 args 传递给方法的实际入参 proxyMethod
	 * 与源对象中的method相对应的代理对象中的方法
	 */
	public Object intercept(Object target, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		// 执行源对象的method方法
		try {
			Aop aop = method.getDeclaringClass().getAnnotation(Aop.class);
			if(null != aop){
				String methodPrefix = aop.methodPrefix();
				String methodName = method.getName();
				if(!"".equals(methodPrefix) && !methodName.startsWith(methodPrefix)){
					return proxy.invokeSuper(target, args);
				}
			} else {
				return proxy.invokeSuper(target, args);
			}
			
			Invocaction invocaction = new Invocaction(target, args, proxy);
			before(invocaction);
			Object returnValue = doInvoke(invocaction);
			after(invocaction);
			return returnValue;
		} catch (Exception e) {
			throw e;
		}
	}

}
