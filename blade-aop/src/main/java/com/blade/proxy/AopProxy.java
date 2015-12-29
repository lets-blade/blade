package com.blade.proxy;

import java.util.ArrayList;
import java.util.List;

public class AopProxy {

	private static final List<Aspect> INTERCEPTORS = new ArrayList<Aspect>();
	
	public static void addInterceptor(Aspect aspect){
		INTERCEPTORS.add(aspect);
	}
	
	public static <T> T create(Class<T> target){
		T obj = AspectFactory.newProxy(target, INTERCEPTORS);
		return obj;
	}
}
