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
package com.blade.mvc.view.resolve;

import com.blade.exception.NotFoundException;
import com.blade.kit.AsmKit;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.ModelAndView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public final class MethodArgument {

	public static Object[] getArgs(Request request, Response response, Method actionMethod) throws Exception{

		Class<?>[] parameters = actionMethod.getParameterTypes();
		Annotation[][] annotations = actionMethod.getParameterAnnotations();
		
		Object[] args = new Object[parameters.length];
		
		actionMethod.setAccessible(true);
		String[] paramaterNames = AsmKit.getMethodParamNames(actionMethod);

		for (int i = 0, len = parameters.length; i < len; i++) {

			Class<?> argType = parameters[i];
			if (argType == Request.class) {
				args[i] = request;
				continue;
			}

			if (argType == Response.class) {
				args[i] = response;
				continue;
			}

			if (argType == ModelAndView.class) {
				args[i] = new ModelAndView();
				continue;
			}

			if (argType == Map.class) {
				args[i] = request.querys();
				continue;
			}

			Annotation annotation = annotations[i][0];
			if(null != annotation){
				// query param
				if(annotation.annotationType() == QueryParam.class){
					QueryParam queryParam = (QueryParam) annotation;
					String paramName = queryParam.value();
					String val = request.query(paramName);

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.query(paramName);
					}
					if (StringKit.isBlank(val)) {
						val = queryParam.defaultValue();
					}
					args[i] = getRequestParam(argType, val);
					continue;
				}

				// header param
				if(annotation.annotationType() == HeaderParam.class){
					HeaderParam headerParam = (HeaderParam) annotation;
					String paramName = headerParam.value();
					String val = request.header(paramName);

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.header(paramName);
					}
					args[i] = getRequestParam(argType, val);
					continue;
				}

				// cookie param
				if(annotation.annotationType() == CookieParam.class){
					CookieParam cookieParam = (CookieParam) annotation;
					String paramName = cookieParam.value();
					String val = request.cookie(paramName);

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.cookie(paramName);
					}
					args[i] = getRequestParam(argType, val);
					continue;
				}

				// form multipart
				if(annotation.annotationType() == MultipartParam.class && argType == FileItem.class){
					MultipartParam multipartParam = (MultipartParam) annotation;
					String paramName = multipartParam.value();
					FileItem val = request.fileItem(paramName);

					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.fileItem(paramName);
					}
					args[i] = val;
					continue;
				}

				// path param
				if(annotation.annotationType() == PathParam.class){
					PathParam pathParam = (PathParam) annotation;
					String paramName = pathParam.value();
					String val = request.pathParam(paramName);
					if (StringKit.isBlank(paramName)) {
						assert paramaterNames != null;
						paramName = paramaterNames[i];
						val = request.pathParam(paramName);
					}
					if (StringKit.isBlank(val)) {
						throw new NotFoundException("path param [" + paramName + "] is null");
					}
					args[i] = getRequestParam(argType, val);
				}
			}
		}
		return args;
	}
	
	private static Object getRequestParam(Class<?> parameterType, String val) {
		Object result = null;
		if (parameterType.equals(String.class)) {
			result = val;
		} else if (parameterType.equals(Integer.class) && StringKit.isNotBlank(val)) {
			result = Integer.parseInt(val);
		} else if (parameterType.equals(int.class) && StringKit.isNotBlank(val)) {
			if(StringKit.isBlank(val)){
				result = 0;
			} else {
				result = Integer.parseInt(val);
			}
		} else if (parameterType.equals(Long.class) && StringKit.isNotBlank(val)) {
			result = Long.parseLong(val);
		} else if (parameterType.equals(long.class)) {
			if(StringKit.isBlank(val)){
				result = 0L;
			} else {
				result = Integer.parseInt(val);
			}
		}
		return result;
	}
	
}
