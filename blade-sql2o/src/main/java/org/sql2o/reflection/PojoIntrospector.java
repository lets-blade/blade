package org.sql2o.reflection;

import org.sql2o.tools.AbstractCache;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.*;

import static java.beans.Introspector.decapitalize;
import static java.lang.reflect.Modifier.isPrivate;
import static java.lang.reflect.Modifier.isStatic;

/**
 * User: dimzon
 * Date: 4/9/14
 * Time: 1:10 AM
 */

// TODO: move introspection code from PojoMetadata to PojoIntrospector

@SuppressWarnings("UnusedDeclaration")
public class PojoIntrospector {
    private static final AbstractCache<Class<?>, Map<String, ReadableProperty>, Void> rpCache =
            new AbstractCache<Class<?>, Map<String, ReadableProperty>, Void>() {
                @Override
                protected Map<String, ReadableProperty> evaluate(Class<?> key, Void param) {
                    return collectReadableProperties(key);
                }
            };

    private static Map<String, ReadableProperty> collectReadableProperties(Class<?> cls) {
        Map<String, ReadableProperty> map = new HashMap<String, ReadableProperty>();
        List<Class<?>> classList = classInheritanceHierarhy(cls, Object.class);
        for (Class<?> aClass : classList) {
            collectPropertyGetters(map, aClass);
        }
        for (Class<?> aClass : classList) {
            collectReadableFields(map, aClass);
        }
        return Collections.unmodifiableMap(map);
    }

    public static Map<String, ReadableProperty> readableProperties(Class<?> ofClass) {
        return rpCache.get(ofClass, null);
    }

    private static void collectReadableFields(Map<String, ReadableProperty> map, Class<?> cls) {
        for (final Field m : cls.getDeclaredFields()) {
            if (isStaticOrPrivate(m)) continue;
            String propName = m.getName();
            if (map.containsKey(propName)) continue;
            Class<?> returnType = m.getType();
            m.setAccessible(true);
            ReadableProperty rp = new ReadableProperty(propName, returnType) {
                @Override
                public Object get(Object instance) throws InvocationTargetException, IllegalAccessException {
                    return m.get(instance);
                }
            };
            map.put(propName, rp);
        }
    }

    private static boolean isStaticOrPrivate(Member m) {
        final int modifiers = m.getModifiers();
        return isStatic(modifiers) || isPrivate(modifiers);
    }

    private static void collectPropertyGetters(Map<String, ReadableProperty> map, Class<?> cls) {
        for (final Method m : cls.getDeclaredMethods()) {
            if (isStatic(m.getModifiers())) continue;
            if (isPrivate(m.getModifiers())) continue;
            if (0 != m.getParameterTypes().length) continue;
            Class<?> returnType = m.getReturnType();
            if (returnType == Void.TYPE || returnType == Void.class) continue;
            String name = m.getName();
            String propName = null;
            if (name.startsWith("get") && name.length() > 3) {
                propName = decapitalize(name.substring(3));
            } else if (name.startsWith("is") && name.length() > 2 && returnType == Boolean.TYPE) {
                propName = decapitalize(name.substring(2));
            }
            if (propName == null) continue;
            if (map.containsKey(propName)) continue;
            m.setAccessible(true);
            ReadableProperty rp = new ReadableProperty(propName, returnType) {
                @Override
                public Object get(Object instance) throws InvocationTargetException, IllegalAccessException {
                    return m.invoke(instance, (Object[]) null);
                }
            };
            map.put(propName, rp);
        }
    }

    private static List<Class<?>> classInheritanceHierarhy(Class<?> cls, Class<Object> stopAt) {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        while (cls != null && cls != stopAt) {
            list.add(cls);
            cls = cls.getSuperclass();
        }
        return list;
    }

    public abstract static class ReadableProperty {
        public final String name;
        public final Class<?> type;

        private ReadableProperty(String name, Class<?> type) {
            this.name = name;
            this.type = type;
        }

        public abstract Object get(Object instance) throws InvocationTargetException, IllegalAccessException;
    }
}
