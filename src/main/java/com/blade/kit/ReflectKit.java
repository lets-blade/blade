package com.blade.kit;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author biezhi
 * 2017/5/31
 */
public class ReflectKit {

    private static final List EMPTY_LIST = new ArrayList(0);

    public static <T> T newInstance(Class<T> cls) {
        try {
            return cls.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    public static Object convert(Class<?> type, String value) {

        if (StringKit.isBlank(value)) {
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
     * @param bean   类实例
     * @param method 方法名称
     * @param args   方法参数
     * @return
     * @throws Exception
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

    public static <T> T cast(Object value, Class<T> type) {
        if (value != null && !type.isAssignableFrom(value.getClass())) {
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
     * 对象是否其中一个
     */
    public static boolean is(Object obj, Object... mybe) {
        if (obj != null && mybe != null) {
            for (Object mb : mybe)
                if (obj.equals(mb))
                    return true;
        }
        return false;
    }

    public static boolean hasInterface(Class<?> cls, Class<?> inter) {
        return Stream.of(cls.getInterfaces()).filter(c -> c.equals(inter)).count() > 0;
    }

    public static boolean isNormalClass(Class<?> cls) {
        return !cls.isInterface() && !Modifier.isAbstract(cls.getModifiers());
    }

    public static Method getMethod(Class<?> cls, String methodName, Class<?>... types) {
        try {
            return cls.getMethod(methodName, types);
        } catch (Exception e) {
            return null;
        }
    }

    public static boolean isPrimitive(Class<?> cls) {
        return cls == boolean.class
                || cls == Boolean.class
                || cls == double.class
                || cls == Double.class
                || cls == float.class
                || cls == Float.class
                || cls == short.class
                || cls == Short.class
                || cls == int.class
                || cls == Integer.class
                || cls == long.class
                || cls == Long.class
                || cls == String.class
                || cls == byte.class
                || cls == Byte.class
                || cls == char.class
                || cls == Character.class;
    }

    public static boolean isPrimitive(Object cls) {
        return cls instanceof Boolean
                || cls instanceof Double
                || cls instanceof Float
                || cls instanceof Short
                || cls instanceof Integer
                || cls instanceof Long
                || cls instanceof String
                || cls instanceof Byte
                || cls instanceof Character;
    }

    public static Object defaultPrimitiveValue(Class<?> primitiveCls) {
        if (primitiveCls == boolean.class) {
            return false;
        } else if (primitiveCls == double.class) {
            return 0d;
        } else if (primitiveCls == float.class) {
            return 0f;
        } else if (primitiveCls == short.class) {
            return (short) 0;
        } else if (primitiveCls == int.class) {
            return 0;
        } else if (primitiveCls == long.class) {
            return 0L;
        } else if (primitiveCls == byte.class) {
            return (byte) 0;
        } else if (primitiveCls == char.class) {
            return '\0';
        } else {
            return null;
        }
    }

    public static Class<?> form(String typeName) {
        try {
            return Class.forName(typeName);
        } catch (Exception e) {
            return null;
        }
    }
}
