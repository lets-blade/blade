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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.blade.aop.annotation.AfterAdvice;
import com.blade.aop.annotation.BeforeAdvice;
import com.blade.aop.exception.AdviceMatcherException;
import com.blade.aop.intercept.MethodInvocation;

import blade.kit.log.Logger;

/**
 * 方法和切点的匹配器
 * @author biezhi
 * @since 1.0
 */
public class AdviceMatcher {
	
	private Logger logger = Logger.getLogger(AdviceMatcher.class);
	
	private AbstractMethodInterceptor interceptor;
	private MethodInvocation invocation;
	
	public AdviceMatcher(AbstractMethodInterceptor interceptor, MethodInvocation invocation) {
		this.interceptor = interceptor;
		this.invocation = invocation;
	}

	public boolean match(Class<?> adviceAnnotationType, String joinPoint) {
		// 要执行的方法
		try {
			Method adviceMethod = interceptor.getClass().getDeclaredMethod(joinPoint, new Class[] {});
			if (adviceAnnotationType == BeforeAdvice.class) {
				BeforeAdvice before = adviceMethod.getAnnotation(BeforeAdvice.class);
				if (before == null)
					return false;
				String pointcut = before.expression();
				if(null == pointcut || pointcut.equals("")){
					return true;
				}
				return beforeOrAfterMatch(pointcut, invocation.getMethod());
			} else if (adviceAnnotationType == AfterAdvice.class) {
				AfterAdvice after = adviceMethod.getAnnotation(AfterAdvice.class);
				if (after == null)
					return false;
				String pointcut = after.expression();
				if(null == pointcut || pointcut.equals("")){
					return true;
				}
				return beforeOrAfterMatch(pointcut, invocation.getMethod());
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			logger.warn(joinPoint + ":" + e.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * 支持的方法名表达式有：
	 *  1)*xxx, 以*开头
	 *  2)xxx*, 以*结尾
	 *  3)*    所有
	 *  4)没有*,指定方法名
	 *  5)指定的annotation
	 *  
	 * @param pointcut
	 * @param methodName
	 * @return
	 */
	private boolean beforeOrAfterMatch(String pointcut, Method method) {
		
		if(null == pointcut){
			return false;
		}
		// 注解/方法
		String[] pointcuts = pointcut.split(":");
		if(pointcuts.length == 1){
			// 注解
			return isMethod(pointcut, method);
		} else{
			if(pointcuts.length == 2){
				String point1 = pointcuts[0];
				String point2 = pointcuts[1];
				// 第一个是注解，(expression = "注解:方法")
				if(point1.startsWith("@")){
					return isAnnotationAndMatchMehtod(point1, point2, method);
				} else{
					//(expression = "类:方法")
					//(expression = "类:注解")
					//(expression = "包:方法")
					//(expression = "包:注解")
					
					// 类或者包级别
					if(isClass(point1, method) || isPackage(point1, method)){
						return isClassOrPackageMethod(point1, point2, method);
					}
					return false;
				}
			}
			if(pointcuts.length == 3){
				//类:注解:方法
				String point1 = pointcuts[0];
				String point2 = pointcuts[1];
				String point3 = pointcuts[2];
				if(isClass(point1, method)){
					return isAnnotationAndMatchMehtod(point2, point3, method);
				}
			}
			return false;
		}
	}
	
	private boolean isClassOrPackageMethod(String classPoint, String methodPoint, Method method){
		if(methodPoint.startsWith("@")){
			return isAnnotationMehtod(methodPoint, method);
		} else{
			return isMatchMethod(methodPoint, method.getName());
		}
	}
	
	private boolean isMethod(String pointcut, Method method){
		// 注解
		if(pointcut.startsWith("@")){
			return isAnnotationMehtod(pointcut, method);
		} else{
			return isMatchMethod(pointcut, method.getName());
		}
	}
	
	/**
	 * 是否匹配一个带注解的方法
	 * @param pointcut
	 * @param method
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private boolean isAnnotationMehtod(String pointcut, Method method){
		try {
			Class<? extends Annotation> annotationClass = (Class<? extends Annotation>) Class.forName(pointcut.substring(1));
			if(null != method.getAnnotation(annotationClass)){
				return true;
			}
		} catch (ClassNotFoundException e) {
			throw new AdviceMatcherException("错误的注解类型");
		}
		return false;
	}
	
	/**
	 * 是否匹配一个表达式中的方法
	 * @param pointcut
	 * @param methodName
	 * @return
	 */
	private boolean isMatchMethod(String pointcut, String methodName){
		int indexOfStar = pointcut.indexOf("*");
		if (indexOfStar != -1) {// 方法名中有*号
			if (indexOfStar == 0) {// 以*开头
				if ("*".equals(pointcut)) {// 只有*
					return true;
				} else {
					return methodName.endsWith(pointcut.substring(1));
				}
			} else {// 以*结尾，中间有*也算以*结尾
				return methodName.startsWith(pointcut.substring(0, indexOfStar));
			}
		} else {
			if(pointcut.indexOf(".") != -1){
				throw new AdviceMatcherException("错误的方法表达式");
			}
			return methodName.equals(pointcut);
		}
	}
	
	/**
	 * 是否同时匹配annotation和method
	 * @param annotationPointcut
	 * @param methodPointcut
	 * @param method
	 * @return
	 */
	private boolean isAnnotationAndMatchMehtod(String annotationPointcut, String methodPointcut, Method method){
		boolean isMatch = isMatchMethod(methodPointcut, method.getName());
		boolean isAnnotation = isAnnotationMehtod(annotationPointcut, method);
		return isMatch && isAnnotation;
	}
	
	/**
	 * 判断给出的包名是否是方法所属包
	 * @param packageName
	 * @param method
	 * @return
	 */
	private boolean isPackage(String packageName, Method method){
		if(null != packageName){
			String packName = method.getDeclaringClass().getPackage().getName();
			return packName.equals(packageName);
		}
		return false;
	}
	
	/**
	 * 判断给出的类名是否是方法所属类
	 * @param className
	 * @param method
	 * @return
	 */
	private boolean isClass(String className, Method method){
		if(null != className){
			String CanonicalName = method.getDeclaringClass().getCanonicalName();
			return CanonicalName.equals(className);
		}
		return false;
	}
	
}