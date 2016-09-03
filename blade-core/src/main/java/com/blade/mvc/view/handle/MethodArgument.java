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
package com.blade.mvc.view.handle;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import com.blade.annotation.PathVariable;
import com.blade.annotation.RequestParam;
import com.blade.asm.AsmKit;
import com.blade.exception.BladeException;
import com.blade.exception.NotFoundException;
import com.blade.kit.StringKit;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.ModelAndView;

public final class MethodArgument {

	public static Object[] getArgs(Request request, Response response, Method actionMethod) throws Exception{
		Class<?>[] parameters = actionMethod.getParameterTypes();
		Annotation[][] paramterAnnotations = actionMethod.getParameterAnnotations();
		
		Object[] args = new Object[parameters.length];
		
		try {
			actionMethod.setAccessible(true);
			String[] paramaterNames = AsmKit.getMethodParamNames(actionMethod);
			
			for (int i = 0, len = parameters.length; i < len; i++) {
				
				Class<?> argType = parameters[i];
				if (argType.getName().equals(Request.class.getName())) {
					args[i] = request;
					continue;
				}
				
				if (argType.getName().equals(Response.class.getName())) {
					args[i] = response;
					continue;
				}

				if (argType.getName().equals(ModelAndView.class.getName())) {
					args[i] = new ModelAndView();
					continue;
				}
				
				if(argType.getName().equals(FileItem.class.getName())){
					args[i] = new ModelAndView();
					continue;
				}
				
				Annotation annotation = paramterAnnotations[i][0];
				
				if(null != annotation){
					if(annotation.annotationType().equals(RequestParam.class)){
						RequestParam requestParam = (RequestParam) annotation;
						String paramName = requestParam.value();
						String val = request.query(paramName);
						
						if (StringKit.isBlank(paramName)) {
							paramName = paramaterNames[i];
							val = request.query(paramName);
						} else {
							if (StringKit.isBlank(val)) {
								throw new NotFoundException("request param [" + paramName + "] is null");
							}
						}
						args[i] = getRequestParam(argType, val, requestParam.defaultValue());
						continue;
					}
					
					if(annotation.annotationType().equals(PathVariable.class)){
						PathVariable pathVariable = (PathVariable) annotation;
						String paramName = pathVariable.value();
						String val = request.param(paramName);
						
						if (StringKit.isBlank(paramName)) {
							paramName = paramaterNames[i];
							val = request.param(paramName);
						} else {
							if (StringKit.isBlank(val)) {
								throw new NotFoundException("path param [" + paramName + "] is null");
							}
						}
						args[i] = getRequestParam(argType, val, null);
						continue;
					}
				}
			}
			return args;
		} catch (BladeException e) {
			throw e;
		}
	}
	
	private static Object getRequestParam(Class<?> parameterType, String val, String defaultValue) {
		Object result = null;
		if (parameterType.equals(String.class)) {
			if (StringKit.isNotBlank(val)) {
				result = val;
			} else {
				if (null != defaultValue) {
					result = defaultValue;
				}
			}
		} else if (parameterType.equals(Integer.class)) {
			if (StringKit.isNotBlank(val)) {
				result = Integer.parseInt(val);
			} else {
				if (StringKit.isNotBlank(defaultValue)) {
					result = Integer.parseInt(defaultValue);
				}
			}
		} else if (parameterType.equals(int.class)) {
			if (StringKit.isNotBlank(val)) {
				result = Integer.parseInt(val);
			} else {
				if (StringKit.isNotBlank(defaultValue)) {
					result = Integer.parseInt(defaultValue);
				} else {
					result = 0;
				}
			}
		} else if (parameterType.equals(Long.class)) {
			if (StringKit.isNotBlank(val)) {
				result = Long.parseLong(val);
			} else {
				if (StringKit.isNotBlank(defaultValue)) {
					result = Long.parseLong(defaultValue);
				}
			}
		} else if (parameterType.equals(long.class)) {
			if (StringKit.isNotBlank(val)) {
				result = Long.parseLong(val);
			} else {
				if (StringKit.isNotBlank(defaultValue)) {
					result = Long.parseLong(defaultValue);
				} else {
					result = 0L;
				}
			}
		}
		return result;
	}
	
}
