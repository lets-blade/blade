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
package com.blade.ioc;

import com.blade.aop.AopProxy;

/**
 * Aop Create Object
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class AopCreator {
	
	/**
	 * Create an agent based on Class
	 * @param clazz	class Object
	 * @return		return proxy object
	 */
	public static <T> T create(Class<?> clazz){
		return AopProxy.create(clazz);
	}
	
	/**
	 * Create an agent based on Class
	 * @param clazz	class Object
	 * @return		return proxy object
	 */
	public static Object createProxy(Class<?> clazz){
		return AopProxy.create(clazz);
	}
	
	/**
	 * Create a proxy object
	 * @param target	original java object
	 * @return			return proxy object
	 */
	public static <T> T create(Object target){
		return AopProxy.create(target);
	}
	
	/**
	 * Create a proxy object
	 * @param target	original java object
	 * @return			return proxy object
	 */
	public static Object createProxy(Object target){
		return AopProxy.create(target);
	}
	
}
