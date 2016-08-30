package com.blade.aop;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.blade.exception.MethodInvokeException;
import com.blade.kit.reflect.ReflectKit;

public class Invocaction {
	
	private Object target;
	private Method method;
	private Object[] args;
	
	public Invocaction(Object target, Method method, Object[] args) {
		this.target = target;
		this.method = method;
		this.args = args;
	}
	
	public Object invoke() throws MethodInvokeException{
		try {
			return ReflectKit.invokeMehod(this.target, method, args);
		} catch (IllegalAccessException e) {
			throw new MethodInvokeException(e);
		} catch (IllegalArgumentException e) {
			throw new MethodInvokeException(e);
		} catch (InvocationTargetException e) {
			throw new MethodInvokeException(e);
		}
	}
	
}
