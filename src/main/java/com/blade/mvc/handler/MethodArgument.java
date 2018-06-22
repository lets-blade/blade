package com.blade.mvc.handler;

import com.blade.exception.BladeException;
import com.blade.kit.AsmKit;
import com.blade.kit.JsonKit;
import com.blade.kit.ReflectKit;
import com.blade.kit.StringKit;
import com.blade.mvc.RouteContext;
import com.blade.mvc.annotation.*;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpSession;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.http.Session;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.ui.ModelAndView;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Route method param parse
 *
 * @author biezhi
 * 2017/9/20
 */
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
            Type      argType   = parameter.getParameterizedType();
            if (containsAnnotation(parameter)) {
                args[i] = getAnnotationParam(parameter, paramName, request);
                continue;
            }
            if (ReflectKit.isBasicType(argType)) {
                args[i] = request.query(paramName);
                continue;
            }
            args[i] = getCustomType(parameter, paramName, signature);
        }
        return args;
    }

    private static boolean containsAnnotation(Parameter parameter) {
        return parameter.getAnnotation(PathParam.class) != null ||
                parameter.getAnnotation(Param.class) != null ||
                parameter.getAnnotation(HeaderParam.class) != null ||
                parameter.getAnnotation(BodyParam.class) != null ||
                parameter.getAnnotation(CookieParam.class) != null ||
                parameter.getAnnotation(MultipartParam.class) != null;
    }

    private static Object getCustomType(Parameter parameter, String paramName, Signature signature) {
        Type argType = parameter.getParameterizedType();
        if (argType == Signature.class) {
            return signature;
        } else if (argType == RouteContext.class) {
            return signature.routeContext();
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
        } else if (ParameterizedType.class.isInstance(argType)) {
            String       name   = parameter.getName();
            List<String> values = signature.request().parameters().get(name);
            return getParameterizedTypeValues(values, argType);
        } else if (ReflectKit.isArray(argType)) {
            List<String> values = signature.request().parameters().get(paramName);
            if (null == values) {
                return null;
            }
            Class  arrayCls = (Class) argType;
            Object aObject  = Array.newInstance(arrayCls.getComponentType(), values.size());
            for (int i = 0; i < values.size(); i++) {
                Array.set(aObject, i, ReflectKit.convert(arrayCls.getComponentType(), values.get(i)));
            }
            return aObject;
        } else {
            return parseModel(ReflectKit.typeToClass(argType), signature.request(), null);
        }
    }

    private static Object getAnnotationParam(Parameter parameter, String paramName, Request request) {
        Type  argType = parameter.getParameterizedType();
        Param param   = parameter.getAnnotation(Param.class);
        if (null != param) {
            return getQueryParam(ParamStruct.builder().argType(argType).param(param).paramName(paramName).request(request).build());
        }
        BodyParam bodyParam = parameter.getAnnotation(BodyParam.class);
        if (null != bodyParam) {
            return getBodyParam(ParamStruct.builder().argType(argType).request(request).build());
        }
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        if (null != pathParam) {
            return getPathParam(ParamStruct.builder().argType(argType).pathParam(pathParam).paramName(paramName).request(request).build());
        }
        HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);
        if (null != headerParam) {
            return getHeader(ParamStruct.builder().argType(argType).headerParam(headerParam).paramName(paramName).request(request).build());
        }
        // cookie param
        CookieParam cookieParam = parameter.getAnnotation(CookieParam.class);
        if (null != cookieParam) {
            return getCookie(ParamStruct.builder().argType(argType).cookieParam(cookieParam).paramName(paramName).request(request).build());
        }
        // form multipart
        MultipartParam multipartParam = parameter.getAnnotation(MultipartParam.class);
        if (null != multipartParam && argType == FileItem.class) {
            String name = StringKit.isBlank(multipartParam.value()) ? paramName : multipartParam.value();
            return request.fileItem(name).orElse(null);
        }
        return null;
    }

    private static Object getBodyParam(ParamStruct paramStruct) {
        Type    argType = paramStruct.argType;
        Request request = paramStruct.request;

        if (ReflectKit.isPrimitive(argType)) {
            return ReflectKit.convert(argType, request.bodyToString());
        } else {
            String json = request.bodyToString();
            return StringKit.isNotBlank(json) ? JsonKit.formJson(json, argType) : null;
        }
    }

    private static Object getQueryParam(ParamStruct paramStruct) {
        Param   param     = paramStruct.param;
        String  paramName = paramStruct.paramName;
        Type    argType   = paramStruct.argType;
        Request request   = paramStruct.request;
        String  name;
        if (null == param) {
            return null;
        }
        name = StringKit.isBlank(param.name()) ? paramName : param.name();

        if (ReflectKit.isBasicType(argType) || argType.equals(Date.class) || argType.equals(BigDecimal.class)
                || argType.equals(LocalDate.class) || argType.equals(LocalDateTime.class)) {

            String value = request.query(name).orElseGet(() -> getDefaultValue(param.defaultValue(), argType));

            return ReflectKit.convert(argType, value);
        } else {
            if (ParameterizedType.class.isInstance(argType)) {
                List<String> values = request.parameters().get(param.name());
                return getParameterizedTypeValues(values, argType);
            }
            return parseModel(ReflectKit.typeToClass(argType), request, param.name());
        }
    }

    private static String getDefaultValue(String defaultValue, Type argType) {
        if (argType.equals(String.class)) {
            if (StringKit.isNotEmpty(defaultValue)) {
                return defaultValue;
            }
            return null;
        }
        if ("".equals(defaultValue) && ReflectKit.isBasicType(argType)) {
            if (argType.equals(int.class) || argType.equals(long.class) || argType.equals(double.class) ||
                    argType.equals(float.class) || argType.equals(short.class) ||
                    argType.equals(byte.class)
                    ) {
                return "0";
            }
            if (argType.equals(boolean.class)) {
                return "false";
            }
            return "";
        }
        return defaultValue;
    }

    private static Object getCookie(ParamStruct paramStruct) throws BladeException {
        Type        argType     = paramStruct.argType;
        CookieParam cookieParam = paramStruct.cookieParam;
        String      paramName   = paramStruct.paramName;
        Request     request     = paramStruct.request;

        String cookieName = StringKit.isBlank(cookieParam.value()) ? paramName : cookieParam.value();
        String val        = request.cookie(cookieName);
        if (null == val) {
            val = cookieParam.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object getHeader(ParamStruct paramStruct) throws BladeException {
        Type        argType     = paramStruct.argType;
        HeaderParam headerParam = paramStruct.headerParam;
        String      paramName   = paramStruct.paramName;
        Request     request     = paramStruct.request;

        String key = StringKit.isBlank(headerParam.value()) ? paramName : headerParam.value();
        String val = request.header(key);
        if (StringKit.isBlank(val)) {
            val = headerParam.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object getPathParam(ParamStruct paramStruct) {
        Type      argType   = paramStruct.argType;
        PathParam pathParam = paramStruct.pathParam;
        String    paramName = paramStruct.paramName;
        Request   request   = paramStruct.request;

        String name = StringKit.isBlank(pathParam.name()) ? paramName : pathParam.name();
        String val  = request.pathString(name);
        if (StringKit.isBlank(val)) {
            val = pathParam.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    public static <T> T parseModel(Class<T> argType, Request request, String name) {

        T obj = ReflectKit.newInstance(argType);

        List<Field> fields = ReflectKit.loopFields(argType);

        for (Field field : fields) {
            if ("serialVersionUID".equals(field.getName())) {
                continue;
            }
            Object value = null;

            Optional<String> fieldValue = request.query(field.getName());

            if (StringKit.isNotBlank(name)) {
                String fieldName = name + "[" + field.getName() + "]";
                fieldValue = request.query(fieldName);
            }

            if (fieldValue.isPresent() && StringKit.isNotBlank(fieldValue.get())) {
                value = ReflectKit.convert(field.getType(), fieldValue.get());
            }
            if (null != value) {
                ReflectKit.setFieldValue(field, obj, value);
            }
        }
        return obj;
    }

    private static Object getParameterizedTypeValues(List<String> values, Type argType) {
        if (null == values || null == argType) {
            return null;
        }
        ParameterizedType parameterizedType = (ParameterizedType) argType;
        Class<?>          realType          = (Class) parameterizedType.getActualTypeArguments()[0];
        return values.stream()
                .map(s -> ReflectKit.convert(realType, s))
                .collect(Collectors.toList());
    }
}