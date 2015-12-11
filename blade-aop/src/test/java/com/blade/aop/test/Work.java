package com.blade.aop.test;

import javax.annotation.Resource;

public class Work {
	
	public void run(){
		System.out.println("run in work!!");
	}
	
	@Resource
	public void save(){
		System.out.println("save ...");
	}
}
