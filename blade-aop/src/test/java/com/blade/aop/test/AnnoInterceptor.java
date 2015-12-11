package com.blade.aop.test;

import com.blade.aop.AbstractMethodInterceptor;
import com.blade.aop.annotation.AfterAdvice;
import com.blade.aop.annotation.BeforeAdvice;

public class AnnoInterceptor extends AbstractMethodInterceptor {

	@Override
	@BeforeAdvice(expression = "@javax.annotation.Resource")
	protected void beforeAdvice() {
		System.out.println("before Resource XXX");
	}
	
	@Override
	@AfterAdvice(expression = "@javax.annotation.Resource")
	protected void afterAdvice() {
		System.out.println("after Resource XXX");
	}
	
}
