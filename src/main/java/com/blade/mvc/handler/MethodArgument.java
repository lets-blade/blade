package com.blade.mvc.handler;

import com.blade.exception.BladeException;
import com.blade.kit.*;
import com.blade.mvc.annotation.*;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpSession;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.ui.ModelAndView;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

public final class MethodArgument {

    public static Object[] getArgs(Signature signature) throws Exception {
        Method  actionMethod = signature.getAction();
        Request request      = signature.request();
        actionMethod.setAccessible(true);

        Parameter[] parameters     = actionMethod.getParameters();
        Object[]    args           = new Object[parameters.length];
        String[]    parameterNames = AsmKit.getMethodParamNames(actionMethod);

        for (int i = 0, len = parameters.length; i < len; i++) {
            Parameter parameter = parameters[i];
            String    paramName = parameterNames[i];
            Class<?>  argType   = parameter.getType();
            if (containsAnnotation(parameter)) {
                args[i] = getAnnotationParam(parameter, paramName, request);
                continue;
            }
            if (ReflectKit.isPrimitive(argType)) {
                args[i] = request.query(paramName);
                continue;
            }
            args[i] = getCustomType(parameter, signature);
        }
        return args;
    }

    private static boolean containsAnnotation(Parameter parameter) {
        return parameter.getAnnotation(QueryParam.class) != null ||
                parameter.getAnnotation(PathParam.class) != null ||
                parameter.getAnnotation(Param.class) != null ||
                parameter.getAnnotation(HeaderParam.class) != null ||
                parameter.getAnnotation(BodyParam.class) != null ||
                parameter.getAnnotation(CookieParam.class) != null ||
                parameter.getAnnotation(MultipartParam.class) != null;
    }

    private static Object getCustomType(Parameter parameter, Signature signature) throws Exception {
        Class<?> argType = parameter.getType();
        if (argType == Signature.class) {
            return signature;
        } else if (argType == Request.class) {
            return signature.request();
        } else if (argType == Response.class) {
            return signature.response();
        } else if (argType == Session.class || argType == HttpSession.class) {
            return signature.request().session();
        } else if (argType == FileItem.class) {
            return new ArrayList<>(signature.request().fileItems().values()).get(0);
        } else if (argType == ModelAndView.class) {
            return new ModelAndView();
        } else if (argType == Map.class) {
            return signature.request().parameters();
        } else if (argType == Optional.class) {
            ParameterizedType firstParam           = (ParameterizedType) parameter.getParameterizedType();
            Type              paramsOfFirstGeneric = firstParam.getActualTypeArguments()[0];
            Class<?>          modelType            = ReflectKit.form(paramsOfFirstGeneric.getTypeName());
            return Optional.ofNullable(parseModel(modelType, signature.request(), null));
        } else {
            return parseModel(argType, signature.request(), null);
        }
    }

    private static Object getAnnotationParam(Parameter parameter, String paramName, Request request) throws Exception {
        Class<?>   argType    = parameter.getType();
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        if (null != queryParam) {
            return getQueryParam(argType, queryParam, paramName, request);
        }
        Param param = parameter.getAnnotation(Param.class);
        if (null != param) {
            return getParam(argType, param, paramName, request);
        }
        BodyParam bodyParam = parameter.getAnnotation(BodyParam.class);
        if (null != bodyParam) {
            return getBodyParam(argType, request);
        }
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        if (null != pathParam) {
            return getPathParam(argType, pathParam, paramName, request);
        }
        HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);
        if (null != headerParam) {
            return getHeader(argType, headerParam, paramName, request);
        }
        // cookie param
        CookieParam cookieParam = parameter.getAnnotation(CookieParam.class);
        if (null != cookieParam) {
            return getCookie(argType, cookieParam, paramName, request);
        }
        // form multipart
        MultipartParam multipartParam = parameter.getAnnotation(MultipartParam.class);
        if (null != multipartParam && argType == FileItem.class) {
            String name = StringKit.isBlank(multipartParam.value()) ? paramName : multipartParam.value();
            return request.fileItem(name).orElse(null);
        }
        return null;
    }

    private static Object getBodyParam(Class<?> argType, Request request) throws BladeException {
        if (ReflectKit.isPrimitive(argType)) {
            return ReflectKit.convert(argType, request.bodyToString());
        } else {
            String json = request.bodyToString();
            return StringKit.isNotBlank(json) ? JsonKit.formJson(request.bodyToString(), argType) : null;
        }
    }

    private static Object getQueryParam(Class<?> argType, QueryParam queryParam, String paramName, Request request) throws BladeException {
        String name = StringKit.isBlank(queryParam.name()) ? paramName : queryParam.name();

        if (ReflectKit.isPrimitive(argType)) {
            Optional<String> val      = request.query(name);
            boolean          required = queryParam.required();
            if (!val.isPresent()) {
                val = Optional.of(queryParam.defaultValue());
            }
            if (required && !val.isPresent()) {
                Assert.throwException(String.format("query param [%s] not is empty.", paramName));
            }
            return ReflectKit.convert(argType, val.get());
        } else {
            name = queryParam.name();
            return parseModel(argType, request, name);
        }
    }

    private static Object getParam(Class<?> argType, Param param, String paramName, Request request) throws BladeException {
        String name = StringKit.isBlank(param.name()) ? paramName : param.name();
        if (ReflectKit.isPrimitive(argType)) {
            Optional<String> val      = request.query(name);
            boolean          required = param.required();
            if (!val.isPresent()) {
                val = Optional.of(param.defaultValue());
            }
            if (required && !val.isPresent()) {
                Assert.throwException(String.format("query param [%s] not is empty.", paramName));
            }
            return ReflectKit.convert(argType, val.get());
        } else {
            name = param.name();
            return parseModel(argType, request, name);
        }
    }


    private static Object getCookie(Class<?> argType, CookieParam cookieParam, String paramName, Request request) throws BladeException {
        String           cookieName = StringKit.isBlank(cookieParam.value()) ? paramName : cookieParam.value();
        Optional<String> val        = request.cookie(cookieName);
        boolean          required   = cookieParam.required();
        if (!val.isPresent()) {
            val = Optional.of(cookieParam.defaultValue());
        }
        if (required && !val.isPresent()) {
            Assert.throwException(String.format("cookie param [%s] not is empty.", paramName));
        }
        return ReflectKit.convert(argType, val.get());
    }

    private static Object getHeader(Class<?> argType, HeaderParam headerParam, String paramName, Request request) throws BladeException {
        String  key      = StringKit.isBlank(headerParam.value()) ? paramName : headerParam.value();
        String  val      = request.header(key);
        boolean required = headerParam.required();
        if (StringKit.isBlank(val)) {
            val = headerParam.defaultValue();
        }
        if (required && StringKit.isBlank(val)) {
            Assert.throwException(String.format("header param [%s] not is empty.", paramName));
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object getPathParam(Class<?> argType, PathParam pathParam, String paramName, Request request) {
        String name = StringKit.isBlank(pathParam.name()) ? paramName : pathParam.name();
        String val  = request.pathString(name);
        if (StringKit.isBlank(val)) {
            val = pathParam.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object parseModel(Class<?> argType, Request request, String name) throws BladeException {
        try {
            Field[] fields = argType.getDeclaredFields();
            if (null == fields || fields.length == 0) {
                return null;
            }
            Object  obj      = ReflectKit.newInstance(argType);
            boolean hasField = false;

            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getName().equals("serialVersionUID")) {
                    continue;
                }
                Optional<String> fieldValue = request.query(field.getName());
                if (StringKit.isNotBlank(name)) {
                    String fieldName = name + "[" + field.getName() + "]";
                    fieldValue = request.query(fieldName);
                }
                if (fieldValue.isPresent() && StringKit.isNotBlank(fieldValue.get())) {
                    Object value = ReflectKit.convert(field.getType(), fieldValue.get());
                    field.set(obj, value);
                    hasField = true;
                }
            }
            return hasField ? obj : null;
        } catch (Exception e) {
            throw new BladeException(e);
        }
    }

}