/**
 * Copyright (c) 2014-2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.aop.intercept;

import java.util.ArrayList;
import java.util.List;

/**
 * 拦截器方法工厂
 * @author biezhi
 * @since 1.0
 */
public final class AbstractMethodInterceptorFactory {
	
	/**
	 * 普通拦截器列表
	 */
	private static final List<AbstractMethodInterceptor> interceptorList = new ArrayList<AbstractMethodInterceptor>();
	
	/**
	 * 获取所有普通拦截器
	 * @return
	 */
	public static List<AbstractMethodInterceptor> getAbstractInterceptors() {
		return interceptorList;
	}

	/**
	 * 添加一个拦截器
	 * @param routeInterceptors
	 */
	public static void addInterceptor(List<AbstractMethodInterceptor> routeInterceptors) {
		interceptorList.addAll(routeInterceptors);
	}
	
	/**
	 * 添加一组拦截器
	 * @param routeInterceptor
	 */
	public static void addInterceptor(AbstractMethodInterceptor routeInterceptor) {
		interceptorList.add(routeInterceptor);
	}
	
}