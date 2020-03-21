package com.blade.kit.json;

import com.blade.kit.DateKit;
import com.blade.kit.ReflectKit;
import com.blade.kit.StringKit;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
public class BeanSerializer {

    public static Object serialize(SerializeMapping serializeMapping, Object bean) throws Exception {
        if (bean == null) {
            return null;
        }

        if (bean instanceof String) {
            return bean.toString().replaceAll("\"", "\\\\\"");
        }

        if (bean instanceof Enum) {
            return bean.toString();
        }

        if (ReflectKit.isBasicType(bean.getClass()) || bean instanceof Number || bean instanceof Date
                || bean instanceof LocalDate || bean instanceof LocalDateTime) {
            return bean;
        }

        if (bean instanceof Collection)
            return serialize(serializeMapping, ((Collection) bean).toArray());

        if (bean.getClass().isArray()) {
            int length = Array.getLength(bean);
            ArrayList<Object> array = new ArrayList<>(length);
            for (int i = 0; i < length; ++i)
                array.add(serialize(serializeMapping, Array.get(bean, i)));
            return array;
        }

        if (bean instanceof Map) {
            Map beanMap = (Map) bean;
            Map map = new HashMap(beanMap.size());
            beanMap.forEach((Object key, Object value) -> {
                try {
                    map.put(key, serialize(serializeMapping, value));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
            return map;
        }
        ArrayList<Integer> indexes = new ArrayList<>();
        ArrayList<Object> values = new ArrayList<>();
        ArrayList<String> keys = new ArrayList<>();
        int pos = 0;
        Map<String, Field> fields = declaredFields(bean.getClass());
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String key = entry.getKey();
            Field field = entry.getValue();
            Object value;
            if ("this$0".equals(key) || "serialVersionUID".equals(key)) {
                continue;
            }
            field.setAccessible(true);
            JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
            if (null != jsonIgnore) {
                continue;
            }
            JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
            JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
            SerializeMapping temp = SerializeMapping.builder()
                    .bigDecimalKeep(serializeMapping.getBigDecimalKeep())
                    .datePatten(serializeMapping.getDatePatten())
                    .build();

            if (null != jsonFormat) {
                switch (jsonFormat.type()) {
                    case DATE_PATTEN:
                        temp.setDatePatten(jsonFormat.value());
                        break;
                    case BIGDECIMAL_KEEP:
                        temp.setBigDecimalKeep(Integer.parseInt(jsonFormat.value()));
                        break;
                    default:
                        break;
                }
            }

            if (jsonProperty != null) {
                value = serialize(temp, field.get(bean));
                if (!jsonProperty.value().isEmpty())
                    key = jsonProperty.value();
            } else {
                value = serialize(temp, field.get(bean));
            }

            if (value instanceof Date) {
                value = DateKit.toString((Date) value, temp.getDatePatten());
            }
            if (value instanceof LocalDate) {
                value = DateKit.toString((LocalDate) value, temp.getDatePatten());
            }
            if (value instanceof LocalDateTime) {
                value = DateKit.toString((LocalDateTime) value, temp.getDatePatten());
            }
            if (value instanceof BigDecimal) {
                value = ((BigDecimal) value).setScale(temp.getBigDecimalKeep()).toString();
            }
            int position = indexes.size();
            indexes.add(position, pos++);
            values.add(position, value);
            keys.add(position, key);
        }
        Ason<String, Object> ason = new Ason<>(indexes.size());
        for (int i = 0; i < indexes.size(); ++i)
            ason.put(keys.get(i), values.get(i));
        return ason;
    }

    public static <T> Collection<Object> deserialize(Collection<Object> template, Class<T> genericType, Collection collection) throws Exception {
        return deserialize(template, genericType, collection.toArray());
    }

    public static <T, A> Collection<Object> deserialize(Collection<Object> template, Class<T> genericType, A[] array) throws Exception {
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
            Object value;
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
                                value = deserialize((Collection<Object>) tmp, genericType, (Collection) value);
                            else if (value.getClass().isArray())
                                value = deserialize((Collection<Object>) tmp, genericType, (Object[]) value);
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
            return (T) deserialize((Collection<Object>) template, Object.class, (Object[]) object);
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
        Map<String, Field> fields = declaredFields(klass);
        for (Map.Entry<String, Field> entry : fields.entrySet()) {
            String name = entry.getKey();
            Field field = entry.getValue();
            Object value = null;
            try {
                field.setAccessible(true);
                JsonIgnore jsonIgnore = field.getAnnotation(JsonIgnore.class);
                if (null != jsonIgnore) {
                    continue;
                }
                JsonProperty jsonProperty = field.getAnnotation(JsonProperty.class);
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
                    JsonFormat jsonFormat = field.getAnnotation(JsonFormat.class);
                    if (null != jsonFormat) {
                        switch (jsonFormat.type()) {
                            case DATE_PATTEN:
                                if (Date.class.equals(clazz)) {
                                    value = DateKit.toDateTime(value.toString(), jsonFormat.value());
                                } else if (LocalDate.class.equals(clazz)) {
                                    value = DateKit.toLocalDate(value.toString(), jsonFormat.value());
                                } else if (LocalDateTime.class.equals(clazz)) {
                                    value = DateKit.toLocalDateTime(value.toString(), jsonFormat.value());
                                }
                                break;
                            case BIGDECIMAL_KEEP:
                                BigDecimal decimal = new BigDecimal(value.toString()).setScale(Integer.parseInt(jsonFormat.value()));
                                if (BigDecimal.class.equals(clazz)) {
                                    value = decimal;
                                } else if (Double.class.equals(clazz) || double.class.equals(clazz)) {
                                    value = decimal.doubleValue();
                                } else if (Float.class.equals(clazz) || float.class.equals(clazz)) {
                                    value = decimal.floatValue();
                                }
                                break;
                            default:
                                break;
                        }
                    } else {
                        value = deserialize(clazz, value);
                    }
                }
                field.set(bean, value);
            } catch (Exception ignore) {
            }
        }
        return (T) bean;
    }

    public static <T, A> Collection<T> deserialize(Class<? extends Collection> klass, Class<T> genericType, A[] array) throws Exception {
        if (klass.equals(List.class)) {
            klass = ArrayList.class;
        }
        if (klass.equals(Set.class)) {
            klass = HashSet.class;
        }
        Collection collection = klass.newInstance();
        for (int i = 0; i < array.length; ++i)
            collection.add(deserialize(genericType, array[i]));
        return collection;
    }

    public static <T> Collection<T> deserialize(Class<? extends Collection> klass, Class<T> genericType, Collection array) throws Exception {
        return deserialize(klass, genericType, array.toArray());
    }

    public static <T> T[] deserialize(Class<T> componentType, Collection array) {
        return deserialize(componentType, array.toArray());
    }

    public static <T, A> T[] deserialize(Class<T> componentType, A[] array) {
        T[] collection = (T[]) (Array.newInstance(componentType, array.length));
        for (int i = 0; i < array.length; ++i)
            collection[i] = deserialize(componentType, array[i]);
        return collection;
    }

    public static <T> T deserialize(Class<T> klass, Object object) {
        try {
            if (null == object) {
                return null;
            }
            if (ReflectKit.isBasicType(object)) {
                return (T) ReflectKit.convert(klass, object.toString());
            } else if (klass.isEnum()) {
                return (T) Enum.valueOf((Class<? extends Enum>) klass, object.toString());
            } else if (object instanceof Map) {
                if (Map.class.isAssignableFrom(klass)) {
                    return klass.cast(object);
                } else {
                    return deserialize(klass, (Map) object);
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

    private static Map<String, Field> declaredFields(Class<?> klass) {
        Map<String, Field> fields = new HashMap<>();
        for (Class<?> cls = klass; cls != Object.class; cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                fields.putIfAbsent(field.getName(), field);
            }
        }
        return fields;
    }
}
