package com.blade.aop;

import java.util.ArrayList;
import java.util.List;

import com.blade.aop.annotation.Aop;
import com.blade.ioc.IocApplication;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.NoOp;

/**
 * 代理工厂
 */
public final class ProxyFactory {

	private ProxyFactory() {
	}

	private static MethodInterceptor[] aopInterceptors;

	static {
		List<Object> aops = IocApplication.getAopInterceptors();
		if (null != aops && aops.size() > 0) {
			aopInterceptors = new MethodInterceptor[aops.size()];
			for (int i = 0, len = aops.size(); i < len; i++) {
				aopInterceptors[i] = (MethodInterceptor) aops.get(i);
			}
		}
	}

	/*
	 * 获得代理对象
	 */
	public static Object getProxyObj(Class<?> clazz) throws Exception {
		Enhancer hancer = new Enhancer();
		// 设置代理对象的父类
		hancer.setSuperclass(clazz);
		// 设置回调对象，即调用代理对象里面的方法时，实际上执行的是回调对象（里的intercept方法）。
		MethodInterceptor[] methodInterceptors = filter(clazz);
		if (null != methodInterceptors && methodInterceptors.length > 0) {
			hancer.setCallbacks(methodInterceptors);
		} else {
			hancer.setCallback(NoOp.INSTANCE);
		}
		// 创建代理对象
		return hancer.create();
	}

	private static MethodInterceptor[] filter(Class<?> clazz) {
		if (null != aopInterceptors) {
			Aop aop = clazz.getAnnotation(Aop.class);
			if (null != aop) {
				Class<?> inteceptorType = aop.value();
				List<MethodInterceptor> methodInterceptors = new ArrayList<MethodInterceptor>();
				for (MethodInterceptor methodInterceptor : aopInterceptors) {
					if (inteceptorType.equals(methodInterceptor.getClass())) {
						methodInterceptors.add(methodInterceptor);
					}
				}
				return methodInterceptors.toArray(new MethodInterceptor[methodInterceptors.size()]);
			}
		}
		return null;
	}

}