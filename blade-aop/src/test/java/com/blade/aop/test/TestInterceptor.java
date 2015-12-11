package com.blade.aop.test;

import com.blade.aop.AbstractMethodInterceptor;
import com.blade.aop.annotation.AfterAdvice;
import com.blade.aop.annotation.BeforeAdvice;

public class TestInterceptor extends AbstractMethodInterceptor {

	@Override
	@BeforeAdvice
	protected void beforeAdvice() {
		System.out.println("before");
	}

	@Override
	@AfterAdvice
	protected void afterAdvice() {
		System.out.println("after");
	}
	
}
