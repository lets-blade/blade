package com.blade.view.handle;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import com.blade.annotation.PathVariable;
import com.blade.annotation.RequestParam;
import com.blade.asm.AsmKit;
import com.blade.exception.BladeException;
import com.blade.exception.NotFoundException;
import com.blade.kit.StringKit;
import com.blade.view.ModelAndView;
import com.blade.web.http.Request;
import com.blade.web.http.Response;
import com.blade.web.multipart.FileItem;

public final class MethodArgument {

	public static Object[] getArgs(Request request, Response response, Method actionMethod) throws BladeException{
		Parameter[] parameters = actionMethod.getParameters();
		Object[] args = new Object[parameters.length];
		
		try {
			actionMethod.setAccessible(true);
			String[] paramaterNames = AsmKit.getMethodParamNames(actionMethod);
			
			for (int i = 0, len = parameters.length; i < len; i++) {
				
				Class<?> argType = parameters[i].getType();
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
				
				RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
				if (null != requestParam) {
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

				PathVariable pathVariable = parameters[i].getAnnotation(PathVariable.class);
				if (null != pathVariable) {
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
