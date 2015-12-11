package com.blade.aop.test;

import com.blade.aop.AopProxy;

public class TestMain {

	public static void main(String[] args) {
		
		AopProxy.addInterceptor(new TestInterceptor());
		AopProxy.addInterceptor(new AnnoInterceptor());
		Work work = AopProxy.create(Work.class);
		work.run();
		
		work.save();
		
	}
}
