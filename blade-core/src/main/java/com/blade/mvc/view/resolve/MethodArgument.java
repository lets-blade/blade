/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.view.resolve;

import com.blade.exception.RouteException;
import com.blade.kit.AsmKit;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.wrapper.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.ModelAndView;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;

public final class MethodArgument {

    public static Object[] getArgs(Request request, Response response, Method actionMethod) throws Exception {

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

            if (argType == Session.class) {
                args[i] = request.session();
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
            if (null != annotation) {
                // query param
                if (annotation.annotationType() == QueryParam.class) {
                    QueryParam queryParam = (QueryParam) annotation;
                    String paramName = queryParam.value();
                    String val = request.query(paramName);
                    boolean required = queryParam.required();

                    if (StringKit.isBlank(paramName)) {
                        assert paramaterNames != null;
                        paramName = paramaterNames[i];
                        val = request.query(paramName);
                    }
                    if (StringKit.isBlank(val)) {
                        val = queryParam.defaultValue();
                    }

                    if (required && StringKit.isBlank(val)) {
                        throw new RouteException("query param [" + paramName + "] not is empty.");
                    }

                    args[i] = getRequestParam(argType, val);
                    continue;
                }

                // path param
                if (annotation.annotationType() == PathParam.class) {
                    PathParam pathParam = (PathParam) annotation;
                    String paramName = pathParam.value();
                    String val = request.pathParam(paramName);

                    if (StringKit.isBlank(paramName)) {
                        assert paramaterNames != null;
                        paramName = paramaterNames[i];
                        val = request.pathParam(paramName);
                    }
                    if (StringKit.isBlank(val)) {
                        val = pathParam.defaultValue();
                    }
                    args[i] = getRequestParam(argType, val);
                }

                // header param
                if (annotation.annotationType() == HeaderParam.class) {
                    HeaderParam headerParam = (HeaderParam) annotation;
                    String paramName = headerParam.value();
                    String val = request.header(paramName);
                    boolean required = headerParam.required();

                    if (StringKit.isBlank(paramName)) {
                        assert paramaterNames != null;
                        paramName = paramaterNames[i];
                        val = request.header(paramName);
                    }
                    if (StringKit.isBlank(val)) {
                        val = headerParam.defaultValue();
                    }

                    if (required && StringKit.isBlank(val)) {
                        throw new RouteException("header param [" + paramName + "] not is empty.");
                    }

                    args[i] = getRequestParam(argType, val);
                    continue;
                }

                // cookie param
                if (annotation.annotationType() == CookieParam.class) {
                    CookieParam cookieParam = (CookieParam) annotation;
                    String paramName = cookieParam.value();
                    String val = request.cookie(paramName);
                    boolean required = cookieParam.required();

                    if (StringKit.isBlank(paramName)) {
                        assert paramaterNames != null;
                        paramName = paramaterNames[i];
                        val = request.cookie(paramName);
                    }
                    if (StringKit.isBlank(val)) {
                        val = cookieParam.defaultValue();
                    }

                    if (required && StringKit.isBlank(val)) {
                        throw new RouteException("cookie param [" + paramName + "] not is empty.");
                    }
                    args[i] = getRequestParam(argType, val);
                    continue;
                }

                // form multipart
                if (annotation.annotationType() == MultipartParam.class && argType == FileItem.class) {

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
            }
        }
        return args;
    }

    public static Object getRequestParam(Class<?> parameterType, String val) {
        Object result = null;
        if (parameterType.equals(String.class)) {
            return val;
        }
        if (StringKit.isBlank(val)) {
            if (parameterType.equals(int.class) || parameterType.equals(double.class) ||
                    parameterType.equals(long.class) || parameterType.equals(byte.class) || parameterType.equals(float.class)) {
                result = 0;
            }
            if (parameterType.equals(boolean.class)) {
                result = false;
            }
        } else {
            if (parameterType.equals(Integer.class) || parameterType.equals(int.class)) {
                result = Integer.parseInt(val);
            }
            if (parameterType.equals(Long.class) || parameterType.equals(long.class)) {
                result = Long.parseLong(val);
            }
            if (parameterType.equals(Double.class) || parameterType.equals(double.class)) {
                result = Double.parseDouble(val);
            }
            if (parameterType.equals(Float.class) || parameterType.equals(float.class)) {
                result = Float.parseFloat(val);
            }
            if (parameterType.equals(Boolean.class) || parameterType.equals(boolean.class)) {
                result = Boolean.parseBoolean(val);
            }
            if (parameterType.equals(Byte.class) || parameterType.equals(byte.class)) {
                result = Byte.parseByte(val);
            }
        }
        return result;
    }

}
