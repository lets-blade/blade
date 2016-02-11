package com.blade.ioc.injector;

import java.lang.reflect.Field;

import com.blade.ioc.Ioc;

public class FieldInjector implements Injector {
	
	private Ioc ioc;
	
	private Field field;
	
	public FieldInjector(Ioc ioc, Field field) {
		this.ioc = ioc;
		this.field = field;
	}
	
	@Override
	public void injection(Object bean) {
		try {
			String name = field.getType().getName();
			Object value = ioc.getBean(name);
			if (value == null) {
				throw new IllegalStateException("Can't inject bean: " + name + " for field: " + field);
			}
			field.setAccessible(true);
			field.set(bean, value);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}