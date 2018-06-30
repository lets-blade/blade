package com.blade.kit;

import com.blade.exception.BladeException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reflect Kit
 *
 * @author biezhi
 * 2017/5/31
 */
@Slf4j
@UtilityClass
public class ReflectKit {

    private static final String TYPE_NAME_PREFIX = "class ";

    /**
     * Create an instance of the none constructor.
     *
     * @param type target type
     * @param <T>  target type
     * @return object instance
     */
    public static <T> T newInstance(Type type) {
        try {
            Class<T> clazz = (Class<T>) getClass(type);
            if (clazz == null) {
                return null;
            }
            return clazz.newInstance();
        } catch (Exception e) {
            log.warn("new instance fail", e.getMessage());
            return null;
        }
    }

    public static Class<?> getClass(Type type)
            throws ClassNotFoundException {
        String className = getClassName(type);
        if (className == null || className.isEmpty()) {
            return null;
        }
        return Class.forName(className);
    }

    public static String getClassName(Type type) {
        if (type == null) {
            return "";
        }
        String className = type.toString();
        if (className.startsWith(TYPE_NAME_PREFIX)) {
            className = className.substring(TYPE_NAME_PREFIX.length());
        }
        return className;
    }

    public static Class<?> typeToClass(Type type) {
        if (type instanceof Class) {
            @SuppressWarnings("unchecked")
            Class<?> clazz = (Class<?>) type;
            return clazz;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;
            return (Class<?>) parameterizedType.getRawType();
        }
        return null;
    }

    /**
     * Converts a string type to a target type
     *
     * @param type  target type
     * @param value string value
     * @return return target value
     */
    public static Object convert(Type type, String value) {
        if (null == value) {
            return value;
        }

        if ("".equals(value)) {

            if (type.equals(String.class)) {
                return value;
            }
            if (type.equals(int.class) || type.equals(double.class) ||
                    type.equals(short.class) || type.equals(long.class) ||
                    type.equals(byte.class) || type.equals(float.class)) {
                return 0;
            }
            if (type.equals(boolean.class)) {
                return false;
            }
            return null;
        }

        if (type.equals(int.class) || type.equals(Integer.class)) {
            return Integer.parseInt(value);
        } else if (type.equals(String.class)) {
            return value;
        } else if (type.equals(Double.class) || type.equals(double.class)) {
            return Double.parseDouble(value);
        } else if (type.equals(Float.class) || type.equals(float.class)) {
            return Float.parseFloat(value);
        } else if (type.equals(Long.class) || type.equals(long.class)) {
            return Long.parseLong(value);
        } else if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            return Boolean.parseBoolean(value);
        } else if (type.equals(Short.class) || type.equals(short.class)) {
            return Short.parseShort(value);
        } else if (type.equals(Byte.class) || type.equals(byte.class)) {
            return Byte.parseByte(value);
        } else if (type.equals(BigDecimal.class)) {
            return new BigDecimal(value);
        } else if (type.equals(Date.class)) {
            if (value.length() == 10) return DateKit.toDate(value, "yyyy-MM-dd");
            return DateKit.toDateTime(value, "yyyy-MM-dd HH:mm:ss");
        } else if (type.equals(LocalDate.class)) {
            return DateKit.toLocalDate(value, "yyyy-MM-dd");
        } else if (type.equals(LocalDateTime.class)) {
            return DateKit.toLocalDateTime(value, "yyyy-MM-dd HH:mm:ss");
        }
        return value;
    }

    /**
     * invoke method
     *
     * @param bean   bean instance
     * @param method method instance
     * @param args   method arguments
     * @return return method returned value
     * @throws Exception throws Exception
     */
    public static Object invokeMethod(Object bean, Method method, Object... args) throws Exception {
        Class<?>[] types    = method.getParameterTypes();
        int        argCount = args == null ? 0 : args.length;
        // 参数个数对不上
        if (argCount != types.length) {
            throw new IllegalStateException(String.format("%s in %s", method.getName(), bean));
        }

        // 转参数类型
        for (int i = 0; i < argCount; i++) {
            args[i] = cast(args[i], types[i]);
        }
        return method.invoke(bean, args);
    }

    /**
     * Converts value to a specified type
     *
     * @param value instance value
     * @param type  target type
     * @param <T>   target type
     * @return converted value
     */
    public static <T> T cast(Object value, Class<T> type) {
        if (null == value) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            if (is(type, int.class, Integer.class)) {
                value = Integer.parseInt(value.toString());
            } else if (is(type, long.class, Long.class)) {
                value = Long.parseLong(value.toString());
            } else if (is(type, float.class, Float.class)) {
                value = Float.parseFloat(value.toString());
            } else if (is(type, double.class, Double.class)) {
                value = Double.parseDouble(value.toString());
            } else if (is(type, boolean.class, Boolean.class)) {
                value = Boolean.parseBoolean(value.toString());
            } else if (is(type, String.class)) {
                value = value.toString();
            }
        }
        return (T) value;
    }

    /**
     * Whether the object is one of them.
     *
     * @param instance    instance
     * @param maybeValues
     * @return
     */
    public static boolean is(Object instance, Object... maybeValues) {
        if (instance != null && maybeValues != null) {
            for (Object mb : maybeValues)
                if (instance.equals(mb)) return true;
        }
        return false;
    }

    /**
     * Determine whether CLS is an implementation of an inteface Type.
     *
     * @param cls
     * @param inter
     * @return
     */
    public static boolean hasInterface(Class<?> cls, Class<?> inter) {
        return Stream.of(cls.getInterfaces()).anyMatch(c -> c.equals(inter));
    }

    /**
     * Determine whether cls belong to a normal type
     *
     * @param cls
     * @return
     */
    public static boolean isNormalClass(Class<?> cls) {
        return !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers());
    }

    /**
     * Get cls method name by name and parameter types
     *
     * @param cls
     * @param methodName
     * @param types
     * @return
     */
    public static Method getMethod(Class<?> cls, String methodName, Class<?>... types) {
        try {
            return cls.getMethod(methodName, types);
        } catch (Exception e) {
            return null;
        }
    }

    public static void setFieldValue(Field field, Object target, Object value) {
        field.setAccessible(true);
        try {
            field.set(target, value);
        } catch (IllegalAccessException e) {
            throw new BladeException(500, "", e.getMessage());
        }
    }

    public static boolean isPrimitive(Object bean) {
        return isPrimitive(bean.getClass());
    }

    /**
     * Is cls a basic type
     *
     * @param type Class type
     * @return true or false
     */
    public static boolean isPrimitive(Type type) {
        return type.equals(boolean.class)
                || type.equals(double.class)
                || type.equals(float.class)
                || type.equals(short.class)
                || type.equals(int.class)
                || type.equals(long.class)
                || type.equals(byte.class)
                || type.equals(char.class);
    }

    public static boolean isBasicType(Object bean) {
        return isBasicType(bean.getClass());
    }

    public static boolean isBasicType(Type type) {
        return type.equals(String.class) || type.equals(Integer.class) ||
                type.equals(Long.class) || type.equals(Double.class) ||
                type.equals(Float.class) || type.equals(Short.class) ||
                type.equals(Boolean.class) || type.equals(Byte.class) ||
                type.equals(Character.class) || type.equals(int.class) ||
                type.equals(long.class) || type.equals(double.class) ||
                type.equals(float.class) || type.equals(short.class) ||
                type.equals(boolean.class) || type.equals(byte.class) ||
                type.equals(char.class);
    }

    public static boolean isArray(Type type) {
        return type == String[].class || type == int[].class ||
                type == Integer[].class || type == Long[].class ||
                type == Double[].class || type == double[].class ||
                type == Float[].class || type == float[].class ||
                type == Boolean[].class || type == boolean[].class ||
                type == Short[].class || type == short[].class ||
                type == Byte[].class || type == byte[].class;
    }

    /**
     * Load a class according to the class name.
     *
     * @param typeName
     * @return
     */
    public static Class<?> form(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (Exception e) {
            log.warn("Class.forName fail", e.getMessage());
            return null;
        }
    }

    public static List<Field> loopFields(Class<?> type) {
        Class<?> superCls = type.getSuperclass();
        if (superCls.equals(Object.class)) {
            if (null != type.getDeclaredFields() && type.getDeclaredFields().length > 0) {
                return Arrays.asList(type.getDeclaredFields());
            }
            return new ArrayList<>();
        }

        List<Field> fields = new ArrayList<>();
        if (null != type.getDeclaredFields() && type.getDeclaredFields().length > 0) {
            fields.addAll(Arrays.asList(type.getDeclaredFields()));
        }

        while (!superCls.equals(Object.class)) {
            if (null != superCls.getDeclaredFields() && superCls.getDeclaredFields().length > 0) {
                fields.addAll(Arrays.asList(superCls.getDeclaredFields()));
            }
            superCls = superCls.getSuperclass();
        }
        return fields;
    }
}
