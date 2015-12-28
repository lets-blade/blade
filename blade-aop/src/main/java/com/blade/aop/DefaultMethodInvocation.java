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
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.cglib.proxy.MethodInterceptor;

import com.blade.aop.annotation.AfterAdvice;
import com.blade.aop.annotation.BeforeAdvice;
import com.blade.aop.intercept.MethodInvocation;

/**
 * 默认的方法执行器实现
 * @author biezhi
 * @since 1.0
 */
public class DefaultMethodInvocation implements MethodInvocation {
	
	List<AbstractMethodInterceptor> interceptors;
	private MethodInterceptor proxy;
	private Method method;
	private Object target;
	private Object[] args;
	int index = 0;
	private boolean executed = false;

	public DefaultMethodInvocation(MethodInterceptor proxy, Method method, Object target, Object[] args, List<AbstractMethodInterceptor> interceptorChain) {
		this.interceptors = interceptorChain;
		this.method = method;
		this.target = target;
		this.args = args;
		this.proxy = proxy;
	}

	public Object proceed() throws Throwable {
		AbstractMethodInterceptor interceptor = null;
		Object result = null;
		if (interceptors.size() > 0 && index < interceptors.size()) {
			interceptor = interceptors.get(index++);
			if (new AdviceMatcher(interceptor, this).match(BeforeAdvice.class, "beforeAdvice")) {
				interceptor.beforeAdvice(); //     执行前置建议
			}
			proceed(); //    执行下一个拦截器
		}
		// 执行真正的方法调用
		if (!executed) {
			executed = true;
			result = method.invoke(target, args);
			/*try {
				result = method.invoke(target, args);
			} catch (RuntimeException e) {
				LOGGER.error(e.getMessage());
			}*/
		}
		if (index > 0) {
			interceptor = interceptors.get(--index);
			if (new AdviceMatcher(interceptor, this).match(AfterAdvice.class, "afterAdvice")) {
				interceptor.afterAdvice(); //     执行后置建议
			}
		}
		return result;
	}

	public Object getThis() {
		return target;
	}

	public AccessibleObject getStaticPart() {
		return null;
	}

	public Method getMethod() {
		return method;
	}

	public MethodInterceptor getProxy() {
		return proxy;
	}

	public Object[] getArguments() {
		return args;
	}
}