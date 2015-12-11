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
package com.blade.aop;
/**
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 默认代理获取类
 * @author biezhi
 * @since 1.0
 */
public class DefaultProxy implements MethodInterceptor {

	private Object target;
	
	private List<AbstractMethodInterceptor> interceptorChain = new ArrayList<AbstractMethodInterceptor>();
	
	public DefaultProxy() {
		
	}
	
	public void addInterceptor(AbstractMethodInterceptor abstractMethodInterceptor){
		interceptorChain.add(abstractMethodInterceptor);
	}
	
	public Object getProxy(Object target) {
		this.target = target;
		// cglib 中加强器，用来创建动态代理
		Enhancer enhancer = new Enhancer();
		// 设置要创建动态代理的类
		enhancer.setSuperclass(target.getClass());
		// 设置回调，这里相当于是对于代理类上所有方法的调用，都会调用CallBack，而Callback则需要实行intercept()方法进行拦截
		enhancer.setCallback(this);
		Object proxy = enhancer.create();
		return proxy;
	}
	
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		DefaultMethodInvocation methodInvocation = new DefaultMethodInvocation(this, method, target, args, interceptorChain);
		return methodInvocation.proceed();
	}
	
}