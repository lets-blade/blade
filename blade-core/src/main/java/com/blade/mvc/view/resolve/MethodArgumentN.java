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

import com.blade.exception.NotFoundException;
import com.blade.kit.StringKit;
import com.blade.mvc.annotation.*;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.view.ModelAndView;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Map;

public final class MethodArgumentN {

    private static boolean isParameterFinal(final Parameter parameter) {
        return Modifier.isFinal(parameter.getModifiers());
    }

    public static Object[] getArgs(Request request, Response response, Method actionMethod) throws Exception {

        int parameterCount = 0;
        for (final Parameter parameter : actionMethod.getParameters()) {
            System.out.println(
                    "\targ" + parameterCount++ + ": "
                            + (parameter.isNamePresent() ? parameter.getName() : "Parameter Name not provided,")
                            + (isParameterFinal(parameter) ? " IS " : " is NOT ")
                            + "final, type " + parameter.getType().getCanonicalName()
                            + ", and parameterized type of " + parameter.getParameterizedType()
                            + " and " + (parameter.isVarArgs() ? "IS " : "is NOT ")
                            + "variable.");
        }

        Parameter[] parameters = actionMethod.getParameters();
        Object[] args = new Object[parameters.length];
        actionMethod.setAccessible(true);

        for (int i = 0, len = parameters.length; i < len; i++) {

            Parameter parameter = parameters[i];

            Class<?> argType = parameter.getType();
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

            QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
            if (null != queryParam) {
                String paramName = queryParam.value();
                String val = request.query(paramName);

                if (StringKit.isBlank(paramName)) {
                    paramName = parameter.getName();
                    val = request.query(paramName);
                }
                if (StringKit.isBlank(val)) {
                    val = queryParam.defaultValue();
                }
                args[i] = MethodArgument.getRequestParam(argType, val);
            }

            PathParam pathParam = parameter.getAnnotation(PathParam.class);
            if (null != pathParam) {
                String paramName = pathParam.value();
                String val = request.pathParam(paramName);
                if (StringKit.isBlank(paramName)) {
                    paramName = parameter.getName();
                    val = request.pathParam(paramName);
                }
                if (StringKit.isBlank(val)) {
                    throw new NotFoundException("path param [" + paramName + "] is null");
                }
                args[i] = MethodArgument.getRequestParam(argType, val);
            }

            HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);
            if (null != headerParam) {
                String paramName = headerParam.value();
                String val = request.header(paramName);

                if (StringKit.isBlank(paramName)) {
                    paramName = parameter.getName();
                    val = request.header(paramName);
                }
                args[i] = MethodArgument.getRequestParam(argType, val);
            }

            CookieParam cookieParam = parameter.getAnnotation(CookieParam.class);
            if (null != cookieParam) {
                String paramName = cookieParam.value();
                String val = request.cookie(paramName);

                if (StringKit.isBlank(paramName)) {
                    paramName = parameter.getName();
                    val = request.cookie(paramName);
                }
                args[i] = MethodArgument.getRequestParam(argType, val);
            }

            MultipartParam multipartParam = parameter.getAnnotation(MultipartParam.class);
            if (null != multipartParam && argType == FileItem.class) {
                String paramName = multipartParam.value();
                FileItem val = request.fileItem(paramName);

                if (StringKit.isBlank(paramName)) {
                    paramName = parameter.getName();
                    val = request.fileItem(paramName);
                }
                args[i] = val;
            }
        }
        return args;
    }

}
