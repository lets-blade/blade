package com.blade.aop;

public interface MethodInterceptor {

	void config(Aop aop);
	
	Object doInvoke(Invocaction method);
	
}
