package com.blade.kit.json;

import com.blade.kit.ReflectKit;
import com.blade.kit.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class BeanSerializer {

    private static final Logger log = LoggerFactory.getLogger(BeanSerializer.class);

    public static Object serialize(Object bean) throws NullPointerException {
        if (bean == null) {
            return null;
        }

        if (ReflectKit.isPrimitive(bean) || bean instanceof Number) {
            return bean;
        }

        if (bean instanceof Collection)
            return serialize(((Collection) bean).toArray());

        if (bean.getClass().isArray()) {
            int length = Array.getLength(bean);
            ArrayList<Object> array = new ArrayList<>(length);
            for (int i = 0; i < length; ++i)
                array.add(serialize(Array.get(bean, i)));
            return array;
        }

        if (bean instanceof Map) {
            Map map = (Map) bean;
            map.forEach((key, value) -> map.put(key, serialize(value)));
            return map;
        }
        ArrayList<Integer> indexs = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        int pos = 0;
        for (Field field : bean.getClass().getDeclaredFields()) {
            Object value = null;
            try {
                String key = field.getName();
                if ("this$0".equals(key) || "serialVersionUID".equals(key)) {
                    continue;
                }
                field.setAccessible(true);
                JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
                if (null != jsonIgnore) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null) {
                    value = serialize(field.get(bean));
                    if (!jsonProperty.value().isEmpty())
                        key = jsonProperty.value();
                } else {
                    value = serialize(field.get(bean));
                }
                int position = indexs.size();
                indexs.add(position, pos++);
                values.add(position, value);
                keys.add(position, key);
            } catch (Exception ignore) {
            }
        }
        Ason<String, Object> ason = new Ason<>(indexs.size());
        for (int i = 0; i < indexs.size(); ++i)
            ason.put(keys.get(i), values.get(i));
        return ason;
    }

    public static <T> Collection deserialize(Collection template, Class<T> genericType, Collection collection) throws Exception {
        return deserialize(template, genericType, collection.toArray());
    }

    public static <T, A> Collection deserialize(Collection template, Class<T> genericType, A[] array) throws Exception {
        Object[] list = template.toArray();
        template.clear();
        for (int i = 0; i < array.length; ++i)
            if (i < list.length) {
                template.add(deserialize(list[i], array[i]));
            } else {
                template.add(deserialize(genericType, array[i]));
            }
        return template;
    }

    public static <T> T deserialize(T template, Map map) {
        for (Field field : template.getClass().getDeclaredFields()) {
            Object value = null;
            try {
                field.setAccessible(true);
                JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
                if (null != jsonIgnore) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                if (jsonProperty != null) {
                    String name = jsonProperty.value();
                    if (name.isEmpty())
                        name = field.getName();
                    value = map.get(name);
                    Object tmp = field.get(template);
                    Class clazz = field.getType();

                    if (Collection.class.isAssignableFrom(clazz)) {
                        Class genericType = Object.class;
                        try {
                            genericType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                        } catch (Exception ignore) {
                        }
                        if (tmp == null || ((Collection) tmp).size() == 0) {
                            if (value instanceof Collection)
                                value = deserialize(clazz, genericType, (Collection) value);
                            else if (value.getClass().isArray())
                                value = deserialize(clazz, genericType, (Object[]) value);
                            else return null;
                        } else {
                            if (value instanceof Collection)
                                value = deserialize((Collection) tmp, genericType, (Collection) value);
                            else if (value.getClass().isArray())
                                value = deserialize((Collection) tmp, genericType, (Object[]) value);
                            else return null;
                        }
                    } else {
                        if (tmp == null || (tmp.getClass().isArray() && Array.getLength(tmp) == 0))
                            value = deserialize(clazz, value);
                        else
                            value = deserialize(tmp, value);
                    }
                    field.set(template, value);
                }
            } catch (Exception ignore) {
            }
        }
        return template;
    }


    public static <T> T deserialize(T template, Object object) throws Exception {
        if (object instanceof Number || object instanceof String || object instanceof Boolean)
            return (T) object;
        if (object instanceof Collection)
            return deserialize(template, ((Collection) object).toArray());
        if (template instanceof Collection) {
            if (!object.getClass().isArray())
                return null;
            return (T) deserialize((Collection) template, Object.class, (Object[]) object);
        }
        if (template.getClass().isArray()) {
            if (!object.getClass().isArray())
                return null;
            int desLength = Array.getLength(template);
            int srcLength = Array.getLength(object);
            boolean isAppend = desLength == 0;
            Class componentType = template.getClass().getComponentType();
            int length = desLength > srcLength ? srcLength : desLength;
            Object array = Array.newInstance(componentType, length);
            for (int i = 0; i < length; ++i)
                if (isAppend)
                    Array.set(array, i, deserialize(componentType, Array.get(object, i)));
                else if (i < Array.getLength(template))
                    Array.set(array, i, deserialize(Array.get(template, i), Array.get(object, i)));
            return (T) array;
        }

        if (object instanceof Map) {
            if (template instanceof Map) {
                Map des = (Map) template;
                Map src = (Map) object;
                boolean isAppend = des.isEmpty();
                for (Object key : src.keySet()) {
                    if (isAppend) {
                        des.put(key, src.get(key));
                    } else if (des.containsKey(key)) {
                        des.replace(key, deserialize(des.get(key), src.get(key)));
                    }
                }
            } else {
                return deserialize(template, (Map) object);
            }
        }
        return null;
    }


    public static <T> T deserialize(Class<T> klass, Map map) throws Exception {
        Object bean = klass.newInstance();
        for (Field field : klass.getDeclaredFields()) {
            Object value = null;
            try {
                field.setAccessible(true);
                JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
                if (null != jsonIgnore) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
                String name = field.getName();
                if (jsonProperty != null) {
                    if (StringKit.isNotBlank(jsonProperty.value())) {
                        name = jsonProperty.value();
                    }
                }
                value = map.get(name);
                Class clazz = field.getType();
                if (Collection.class.isAssignableFrom(clazz)) {
                    Class genericType = Object.class;
                    try {
                        genericType = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                    } catch (Exception ignore) {
                    }
                    if (value instanceof Collection) {
                        value = deserialize(clazz, genericType, (Collection) value);
                    } else if (value.getClass().isArray()) {
                        value = deserialize(clazz, genericType, (Object[]) value);
                    } else {
                        value = null;
                    }
                } else {
                    value = deserialize(clazz, value);
                }
                field.set(bean, value);
            } catch (Exception ignore) {
            }
        }
        return (T) bean;
    }

    public static <T, A> Collection<T> deserialize(Class<? extends Collection> klass, Class<T> genericType, A[] array) throws Exception {
        Collection collection = klass.newInstance();
        for (int i = 0; i < array.length; ++i)
            collection.add(deserialize(genericType, array[i]));
        return collection;
    }

    public static <T> Collection<T> deserialize(Class<? extends Collection> klass, Class<T> genericType, Collection array) throws Exception {
        return deserialize(klass, genericType, array.toArray());
    }

    public static <T> T[] deserialize(Class<T> componentType, Collection array) throws Exception {
        return deserialize(componentType, array.toArray());
    }

    public static <T, A> T[] deserialize(Class<T> componentType, A[] array) throws Exception {
        T[] collection = (T[]) (Array.newInstance(componentType, array.length));
        for (int i = 0; i < array.length; ++i)
            collection[i] = deserialize(componentType, array[i]);
        return collection;
    }

    public static <T> T deserialize(Class<T> klass, Object object) {
        try {
            if (ReflectKit.isPrimitive(object)) {
                return (T) object;
            } else if (object instanceof Map) {
                if (Map.class.isAssignableFrom(klass)) {
                    return klass.cast(object);
                } else {
                    return (T) deserialize(klass, (Map) object);
                }
            } else if (Collection.class.isAssignableFrom(klass)) {
                if (object instanceof Collection) {
                    return (T) deserialize((Class<? extends Collection>) klass, Object.class, (Collection) object);
                } else if (object.getClass().isArray()) {
                    return (T) deserialize((Class<? extends Collection>) klass, Object.class, (Object[]) object);
                }
            } else if (klass.isArray()) {
                if (object instanceof Collection) {
                    return (T) deserialize(klass.getComponentType(), (Collection) object);
                } else if (object.getClass().isArray()) {
                    return (T) deserialize(klass.getComponentType(), (Object[]) object);
                }
            }
            return null;
        } catch (Exception e) {
            log.error("json deserialize error", e);
            return null;
        }
    }
}
