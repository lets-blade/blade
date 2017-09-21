package com.blade.mvc.handler;

import com.blade.exception.BladeException;
import com.blade.kit.AsmKit;
import com.blade.kit.JsonKit;
import com.blade.kit.ReflectKit;
import com.blade.kit.StringKit;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

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
            String   paramName = parameterNames[i];
            Class<?> argType   = parameter.getType();
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
        Class<?> argType = parameter.getType();
        Param    param   = parameter.getAnnotation(Param.class);
        if (null != param) {
            return getQueryParam(ParamStrut.builder().argType(argType).param(param).paramName(paramName).request(request).build());
        }
        BodyParam bodyParam = parameter.getAnnotation(BodyParam.class);
        if (null != bodyParam) {
            return getBodyParam(ParamStrut.builder().argType(argType).request(request).build());
        }
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        if (null != pathParam) {
            return getPathParam(ParamStrut.builder().argType(argType).pathParam(pathParam).paramName(paramName).request(request).build());
        }
        HeaderParam headerParam = parameter.getAnnotation(HeaderParam.class);
        if (null != headerParam) {
            return getHeader(ParamStrut.builder().argType(argType).headerParam(headerParam).paramName(paramName).request(request).build());
        }
        // cookie param
        CookieParam cookieParam = parameter.getAnnotation(CookieParam.class);
        if (null != cookieParam) {
            return getCookie(ParamStrut.builder().argType(argType).cookieParam(cookieParam).paramName(paramName).request(request).build());
        }
        // form multipart
        MultipartParam multipartParam = parameter.getAnnotation(MultipartParam.class);
        if (null != multipartParam && argType == FileItem.class) {
            String name = StringKit.isBlank(multipartParam.value()) ? paramName : multipartParam.value();
            return request.fileItem(name).orElse(null);
        }
        QueryParam queryParam = parameter.getAnnotation(QueryParam.class);
        if (null != queryParam) {
            return getQueryParam(ParamStrut.builder().argType(argType).queryParam(queryParam).paramName(paramName).request(request).build());
        }
        return null;
    }

    private static Object getBodyParam(ParamStrut paramStrut) throws Exception {
        Class<?> argType = paramStrut.argType;
        Request  request = paramStrut.request;

        if (ReflectKit.isPrimitive(argType)) {
            return ReflectKit.convert(argType, request.bodyToString());
        } else {
            String json = request.bodyToString();
            return StringKit.isNotBlank(json) ? JsonKit.formJson(request.bodyToString(), argType) : null;
        }
    }

    private static Object getQueryParam(ParamStrut paramStrut) throws Exception {
        QueryParam queryParam = paramStrut.queryParam;
        Param      param      = paramStrut.param;

        String   paramName = paramStrut.paramName;
        Class<?> argType   = paramStrut.argType;
        Request  request   = paramStrut.request;
        String   name      = null;
        if (null != param) {
            name = StringKit.isBlank(param.name()) ? paramName : param.name();
        }
        if (null != queryParam) {
            name = StringKit.isBlank(queryParam.name()) ? paramName : queryParam.name();
        }
        if (ReflectKit.isPrimitive(argType) || argType.equals(Date.class) || argType.equals(BigDecimal.class)
                || argType.equals(LocalDate.class) || argType.equals(LocalDateTime.class)) {
            Optional<String> val = request.query(name);
            if (!val.isPresent()) {
                val = Optional.of(null != param ? param.defaultValue() : queryParam.defaultValue());
            }
            return ReflectKit.convert(argType, val.get());
        } else {
            name = null != param ? param.name() : queryParam.name();
            return parseModel(argType, request, name);
        }
    }

    private static Object getCookie(ParamStrut paramStrut) throws BladeException {
        Class<?>    argType     = paramStrut.argType;
        CookieParam cookieParam = paramStrut.cookieParam;
        String      paramName   = paramStrut.paramName;
        Request     request     = paramStrut.request;

        String           cookieName = StringKit.isBlank(cookieParam.value()) ? paramName : cookieParam.value();
        Optional<String> val        = request.cookie(cookieName);
        if (!val.isPresent()) {
            val = Optional.of(cookieParam.defaultValue());
        }
        return ReflectKit.convert(argType, val.get());
    }

    private static Object getHeader(ParamStrut paramStrut) throws BladeException {
        Class<?>    argType     = paramStrut.argType;
        HeaderParam headerParam = paramStrut.headerParam;
        String      paramName   = paramStrut.paramName;
        Request     request     = paramStrut.request;

        String key = StringKit.isBlank(headerParam.value()) ? paramName : headerParam.value();
        String val = request.header(key);
        if (StringKit.isBlank(val)) {
            val = headerParam.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object getPathParam(ParamStrut paramStrut) {
        Class<?>  argType   = paramStrut.argType;
        PathParam pathParam = paramStrut.pathParam;
        String    paramName = paramStrut.paramName;
        Request   request   = paramStrut.request;

        String name = StringKit.isBlank(pathParam.name()) ? paramName : pathParam.name();
        String val  = request.pathString(name);
        if (StringKit.isBlank(val)) {
            val = pathParam.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object parseModel(Class<?> argType, Request request, String name) throws Exception {
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
    }

}