package com.blade.aop;

import com.blade.ioc.BeanDefine;
import com.blade.ioc.SimpleIoc;

public class ProxyIocImpl extends SimpleIoc {

    public BeanDefine getBeanDefine(Class<?> beanClass, boolean singleton) {
        try {
            Object object = ProxyFactory.getProxyObj(beanClass);
            return new BeanDefine(object, beanClass, singleton);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}