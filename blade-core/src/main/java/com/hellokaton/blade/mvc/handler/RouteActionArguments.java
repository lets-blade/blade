package com.hellokaton.blade.mvc.handler;

import com.hellokaton.blade.annotation.request.*;
import com.hellokaton.blade.exception.BladeException;
import com.hellokaton.blade.kit.JsonKit;
import com.hellokaton.blade.kit.ReflectKit;
import com.hellokaton.blade.kit.StringKit;
import com.hellokaton.blade.mvc.RouteContext;
import com.hellokaton.blade.mvc.http.HttpSession;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Response;
import com.hellokaton.blade.mvc.http.Session;
import com.hellokaton.blade.mvc.multipart.FileItem;
import com.hellokaton.blade.mvc.ui.ModelAndView;
import com.blade.reflectasm.ASMUtils;

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
public final class RouteActionArguments {

    public static Object[] getRouteActionParameters(RouteContext context) {
        Method actionMethod = context.routeAction();
        Request request = context.request();
        actionMethod.setAccessible(true);

        Parameter[] parameters = actionMethod.getParameters();
        Object[] args = new Object[parameters.length];
        String[] parameterNames = ASMUtils.findMethodParmeterNames(actionMethod);

        for (int i = 0, len = parameters.length; i < len; i++) {
            Parameter parameter = parameters[i];
            String paramName = Objects.requireNonNull(parameterNames)[i];
            Type argType = parameter.getParameterizedType();
            if (containsAnnotation(parameter)) {
                args[i] = getAnnotationParam(parameter, paramName, request);
                continue;
            }
            if (ReflectKit.isBasicType(argType)) {
                args[i] = request.query(paramName);
                continue;
            }
            args[i] = getCustomType(parameter, paramName, context);
        }
        return args;
    }

    private static boolean containsAnnotation(Parameter parameter) {
        return parameter.getAnnotation(PathParam.class) != null ||
                parameter.getAnnotation(Query.class) != null ||
                parameter.getAnnotation(Form.class) != null ||
                parameter.getAnnotation(Header.class) != null ||
                parameter.getAnnotation(Body.class) != null ||
                parameter.getAnnotation(Cookie.class) != null ||
                parameter.getAnnotation(Multipart.class) != null;
    }

    private static Object getCustomType(Parameter parameter, String paramName, RouteContext context) {
        Type argType = parameter.getParameterizedType();
        if (argType == RouteContext.class) {
            return context;
        } else if (argType == Request.class) {
            return context.request();
        } else if (argType == Response.class) {
            return context.response();
        } else if (argType == Session.class || argType == HttpSession.class) {
            return context.request().session();
        } else if (argType == FileItem.class) {
            return new ArrayList<>(context.request().fileItems().values()).get(0);
        } else if (argType == ModelAndView.class) {
            return new ModelAndView();
        } else if (argType == Map.class) {
            return context.request().formParams();
        } else if (argType == Optional.class) {
            ParameterizedType firstParam = (ParameterizedType) parameter.getParameterizedType();
            Type paramsOfFirstGeneric = firstParam.getActualTypeArguments()[0];
            Class<?> modelType = ReflectKit.form(paramsOfFirstGeneric.getTypeName());
            return Optional.ofNullable(parseModel(modelType, context.request(), null));
        } else if (ParameterizedType.class.isInstance(argType)) {
            String name = parameter.getName();
            List<String> values = context.request().formParams().get(name);
            return getParameterizedTypeValues(values, argType);
        } else if (ReflectKit.isArray(argType)) {
            List<String> values = context.request().formParams().get(paramName);
            if (null == values) {
                return null;
            }
            Class arrayCls = (Class) argType;
            Object aObject = Array.newInstance(arrayCls.getComponentType(), values.size());
            for (int i = 0; i < values.size(); i++) {
                Array.set(aObject, i, ReflectKit.convert(arrayCls.getComponentType(), values.get(i)));
            }
            return aObject;
        } else {
            return parseModel(ReflectKit.typeToClass(argType), context.request(), null);
        }
    }

    private static Object getAnnotationParam(Parameter parameter, String paramName, Request request) {
        Type argType = parameter.getParameterizedType();
        Query query = parameter.getAnnotation(Query.class);

        ParamStruct.ParamStructBuilder structBuilder = ParamStruct.builder().argType(argType).request(request);

        if (null != query) {
            return getQueryParam(structBuilder.query(query).paramName(paramName).build());
        }

        Form form = parameter.getAnnotation(Form.class);
        if (null != form) {
            return getFormParam(structBuilder.form(form).paramName(paramName).build());
        }

        Body body = parameter.getAnnotation(Body.class);
        if (null != body) {
            return getBodyParam(structBuilder.build());
        }
        PathParam pathParam = parameter.getAnnotation(PathParam.class);
        if (null != pathParam) {
            return getPathParam(structBuilder.pathParam(pathParam).paramName(paramName).build());
        }
        Header header = parameter.getAnnotation(Header.class);
        if (null != header) {
            return getHeader(structBuilder.header(header).paramName(paramName).build());
        }
        // cookie param
        Cookie cookie = parameter.getAnnotation(Cookie.class);
        if (null != cookie) {
            return getCookie(structBuilder.cookie(cookie).paramName(paramName).build());
        }
        // form multipart
        Multipart multipart = parameter.getAnnotation(Multipart.class);
        if (null != multipart && argType == FileItem.class) {
            String name = StringKit.isBlank(multipart.value()) ? paramName : multipart.value();
            return request.fileItem(name).orElse(null);
        }
        return null;
    }

    private static Object getBodyParam(ParamStruct paramStruct) {
        Type argType = paramStruct.argType;
        Request request = paramStruct.request;

        if (ReflectKit.isPrimitive(argType)) {
            return ReflectKit.convert(argType, request.bodyToString());
        } else {
            String json = request.bodyToString();
            return StringKit.isNotEmpty(json) ? JsonKit.fromJson(json, argType) : null;
        }
    }

    private static Object getQueryParam(ParamStruct paramStruct) {
        Query query = paramStruct.query;
        Type argType = paramStruct.argType;
        Request request = paramStruct.request;
        if (null == query) {
            return null;
        }

        String name = StringKit.isBlank(query.name()) ? paramStruct.paramName : query.name();

        if (ReflectKit.isBasicType(argType) || argType.equals(Date.class)
                || argType.equals(BigDecimal.class) || argType.equals(LocalDate.class)
                || argType.equals(LocalDateTime.class) || (argType instanceof Class && ((Class) argType).isEnum())) {

            String value = request.query(name).orElseGet(() -> getDefaultValue(query.defaultValue(), argType));

            return ReflectKit.convert(argType, value);
        } else {
            if (ParameterizedType.class.isInstance(argType)) {

                List<String> values = request.queries().get(query.name());
                return getParameterizedTypeValues(values, argType);
            }
            return parseModel(ReflectKit.typeToClass(argType), request, query.name());
        }
    }

    private static Object getFormParam(ParamStruct paramStruct) {
        Form form = paramStruct.form;
        Type argType = paramStruct.argType;
        Request request = paramStruct.request;
        if (null == form) {
            return null;
        }

        String name = StringKit.isBlank(form.name()) ? paramStruct.paramName : form.name();

        if (ReflectKit.isBasicType(argType) || argType.equals(Date.class)
                || argType.equals(BigDecimal.class) || argType.equals(LocalDate.class)
                || argType.equals(LocalDateTime.class) || (argType instanceof Class && ((Class) argType).isEnum())) {

            String value = request.form(name).orElseGet(() -> getDefaultValue(form.defaultValue(), argType));

            return ReflectKit.convert(argType, value);
        } else {
            if (ParameterizedType.class.isInstance(argType)) {
                List<String> values = request.formParams().get(form.name());
                return getParameterizedTypeValues(values, argType);
            }
            return parseModel(ReflectKit.typeToClass(argType), request, form.name());
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

            if (argType.equals(int.class) || argType.equals(long.class)
                    || argType.equals(double.class) || argType.equals(float.class)
                    || argType.equals(short.class) || argType.equals(byte.class)) {

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
        Type argType = paramStruct.argType;
        Cookie cookie = paramStruct.cookie;
        String paramName = paramStruct.paramName;
        Request request = paramStruct.request;

        String cookieName = StringKit.isEmpty(cookie.value()) ? paramName : cookie.value();
        String val = request.cookie(cookieName);
        if (null == val) {
            val = cookie.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object getHeader(ParamStruct paramStruct) throws BladeException {
        Type argType = paramStruct.argType;
        Header header = paramStruct.header;
        String paramName = paramStruct.paramName;
        Request request = paramStruct.request;

        String key = StringKit.isEmpty(header.value()) ? paramName : header.value();
        String val = request.header(key);
        if (StringKit.isBlank(val)) {
            val = header.defaultValue();
        }
        return ReflectKit.convert(argType, val);
    }

    private static Object getPathParam(ParamStruct paramStruct) {
        Type argType = paramStruct.argType;
        PathParam pathParam = paramStruct.pathParam;
        String paramName = paramStruct.paramName;
        Request request = paramStruct.request;

        String name = StringKit.isEmpty(pathParam.name()) ? paramName : pathParam.name();
        String val = request.pathString(name);
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
        Class<?> realType = (Class) parameterizedType.getActualTypeArguments()[0];
        return values.stream()
                .map(s -> ReflectKit.convert(realType, s))
                .collect(Collectors.toList());
    }
}