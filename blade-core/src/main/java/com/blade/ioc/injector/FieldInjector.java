/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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