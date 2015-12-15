/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade;

import com.blade.ioc.AopCreator;

import blade.kit.Assert;
import blade.kit.ReflectKit;

/**
 * 作为AOP的基础，用于创建对象
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings("unchecked")
public final class Aop {
	
	private static boolean AOP_OPEN = false;
	
	static{
		try {
			Class.forName("com.blade.aop.AopProxy");
			AOP_OPEN = true; 
		} catch (ClassNotFoundException e) {
			AOP_OPEN = false;
		}
	}
	
	public static Object create(Class<?> clazz){
		Assert.notNull(clazz);
		if(AOP_OPEN){
			return AopCreator.create(clazz);
		} else {
			try {
				return ReflectKit.newInstance(clazz);
			} catch (Exception e) {
				new RuntimeException("create object error", e);
			}
		}
		return null;
	}
	
	public static <T> T createT(Class<T> clazz){
		Assert.notNull(clazz);
		if(AOP_OPEN){
			return AopCreator.create(clazz);
		} else {
			try {
				Object object = ReflectKit.newInstance(clazz);
				if(null != object){
					return (T) object;
				}
			} catch (Exception e) {
				new RuntimeException("create object error", e);
			}
		}
		return null;
	}
	
	public static Object create(String className){
		try {
			Class<?> clazz = Class.forName(className);
			return create(clazz);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}