package com.blade.aop;

import com.blade.ioc.BeanDefine;
import com.blade.ioc.SimpleIoc;

public class ProxyIocImpl extends SimpleIoc {
	
	public BeanDefine getBeanDefine(Class<?> beanClass, boolean singleton) {
    	try {
			Object object = ProxyFactory.getProxyObj(beanClass);
			return new BeanDefine(object, beanClass, singleton);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return null;
    }
	
}
