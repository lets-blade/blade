package com.blade.kit;

import com.blade.reflectasm.MethodAccess;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import static com.blade.kit.BladeKit.methodToFieldName;

/**
 * @author biezhi
 * @date 2018/4/22
 */
public class BladeCache {

    private static final Map<SerializedLambda, String> CACHE_LAMBDA_NAME       = new HashMap<>(8);
    private static final Map<Class, MethodAccess>      CLASS_METHOD_ACCESS_MAP = new HashMap<>(8);

    public static final MethodAccess getMethodAccess(Class clazz) {
        return CLASS_METHOD_ACCESS_MAP.computeIfAbsent(clazz, MethodAccess::get);
    }

    public static String getLambdaFieldName(SerializedLambda serializedLambda) {
        String name = CACHE_LAMBDA_NAME.get(serializedLambda);
        if (null != name) {
            return name;
        }
        String className  = serializedLambda.getImplClass().replace("/", ".");
        String methodName = serializedLambda.getImplMethodName();
        String fieldName  = methodToFieldName(methodName);
        try {
            Field field = Class.forName(className).getDeclaredField(fieldName);
            name = field.getName();
            CACHE_LAMBDA_NAME.put(serializedLambda, name);
            return name;
        } catch (NoSuchFieldException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

}
