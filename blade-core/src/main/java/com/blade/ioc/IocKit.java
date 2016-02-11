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
package com.blade.ioc;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.blade.ioc.annotation.Inject;

import blade.kit.reflect.ClassDefine;

public class IocKit {

	// @Inject标注的字段
    public static List<Field> getInjectFields(ClassDefine classDefine) {
        List<Field> injectors = new ArrayList<Field>(8);
        for (Field field : classDefine.getDeclaredFields()) {
            for (Annotation annotation : field.getAnnotations()) {
                if (annotation.annotationType().equals(Inject.class)) {
                	injectors.add(field);
                }
            }
        }
        if (injectors.size() == 0) {
            return Collections.emptyList();
        }
        return injectors;
    }
    
    public static Object getBean(Ioc ioc, BeanDefine beanDefine) {
    	ClassDefine classDefine = ClassDefine.create(beanDefine.getType());
		List<Field> fields = IocKit.getInjectFields(classDefine);
		try {
			Object bean = beanDefine.getBean();
			for (Field field : fields) {
				String name = field.getType().getName();
				Object value = ioc.getBean(name);
				if (value == null) {
					throw new IllegalStateException("Can't inject bean: " + name + " for field: " + field);
				}
				field.setAccessible(true);
				field.set(bean, value);
			}
			return bean;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
        return null;
    }
	
}
