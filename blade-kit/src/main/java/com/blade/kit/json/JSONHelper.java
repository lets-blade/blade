package com.blade.kit.json;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.StringKit;

public class JSONHelper {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(JSONHelper.class);
	
	public static Object toJSONObject(JSONValue value) {
		if(value.isBoolean())
			return value.asBoolean();
		else if(value.isNumber())
			return value.asInt();
		else if(value.isString())
			return value.asString();
		else if(value.isArray())
			return jsonArrayAsList(value.asArray());
		else if(value.isObject())
			return jsonObjectAsMap(value.asJSONObject());
		else if(value.isBean())
			return jsonObjectAsMap(value.asJSONObject());
		else return null;
	}
	
	public static <T> T toBean(String json, Class<T> clazz) {
		JSONObject jsonObject = JSON.parse(json).asJSONObject();
		if(null == jsonObject){
			return null;
		}
		
		T object = null;
		try {
			object = clazz.newInstance();
			
			/*Field[] fields = clazz.getDeclaredFields();
			for (Field field : fields) {
				if(field.getModifiers() == 2){
					String key = field.getName();
					if (jsonObject.contains(key)) {
						JSONValue value = jsonObject.get(key);
						if(null != value){
							field.setAccessible(true);
							field.set(object, value.asString());
						}
					}
				}
			}*/
			
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String name = property.getName();
				if (jsonObject.contains(name)) {
					Object value = getValue(property.getPropertyType(), jsonObject, name);
					if(null != value){
						// 得到property对应的setter方法  
						Method setter = property.getWriteMethod();
						setter.invoke(object, value);
					}
				}
			}
		} catch (Exception e) {
			LOGGER.error("jsonobject covert to bean error", e);
		}
		return object;
	}
	
	private static Object getValue(Class<?> type, JSONObject jsonObject, String name){
		if(null != type && null != jsonObject && StringKit.isNotBlank(name)){
			if(type.equals(String.class)){
				return jsonObject.getString(name);
			}
			if(type.equals(Long.class) || type.equals(long.class)){
				return jsonObject.getLong(name);
			}
			if(type.equals(Double.class) || type.equals(double.class)){
				return jsonObject.getDouble(name);
			}
			if(type.equals(Boolean.class) || type.equals(boolean.class)){
				return jsonObject.getBoolean(name);
			}
		}
		return null;
	}
	
	public static Set<Object> jsonArrayAsSet(JSONArray array) {
		Set<Object> set = new HashSet<Object>();
		for(JSONValue value:array)
			set.add(toJSONObject(value));
		return set;
	}
	
	public static List<Object> jsonArrayAsList(JSONArray array) {
		List<Object> list = new ArrayList<Object>(array.size());
		for(JSONValue element:array)
			list.add(toJSONObject(element));
		return list;
	}
	
	public static Map<String,Object> jsonObjectAsMap(JSONObject object) {
		Map<String,Object> map = new HashMap<String,Object>(object.size(), 1.f);
		for(JSONObject.Member member:object)
			map.put(member.getName(), toJSONObject(member.getValue()));
		return map;		
	}
	
	public static JSONValue toJSONValue(Object object) {
		if (object == null)
			return JSON.NULL;
		else if (object instanceof Boolean)
			return JSON.value((Boolean) object);
		else if (object instanceof Integer)
			return JSON.value((Integer) object);
		else if (object instanceof Long)
			return JSON.value((Long) object);
		else if (object instanceof Float)
			return JSON.value((Float) object);
		else if (object instanceof Double)
			return JSON.value((Double) object);
		else if (object instanceof String)
			return JSON.value((String) object);
		else if (object instanceof Byte)
			return JSON.value((Byte) object);
		else if (object instanceof Collection)
			return toJSONArray((Collection<?>) object);
		else if (object instanceof Map)
			return mapAsJsonObject((Map<?, ?>) object);
		else if (object instanceof JSONObject)
			return ((JSONObject)object);
		else if (object instanceof JSONArray)
			return ((JSONArray)object);
		else
			return toJSONObject(object);
	}
	
	public static JSONArray toJSONArray(Collection<?> collection) {
		JSONArray array = new JSONArray();
		for(Object element:collection)
			array.add(toJSONValue(element));
		return array;
	}
	
	public static JSONObject mapAsJsonObject(Map<?,?> map) {
		JSONObject object = new JSONObject();
		for(Entry<?,?> entry:map.entrySet()) {
			object.put(String.valueOf(entry.getKey()), toJSONValue(entry.getValue()));
		}
		return object;
	}
	
	public static JSONObject toJSONObject(Object bean) {
		
        Class<?> klass = bean.getClass();
        
        // If klass is a System class then set includeSuperClass to false.
        boolean includeSuperClass = klass.getClassLoader() != null;
        Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
        
        if(null == methods || methods.length == 0){
        	return null;
        }
        
        Map<String, Object> map = new HashMap<String, Object>();
        
        for (int i = 0, len = methods.length; i < len; i ++) {
            try {
                Method method = methods[i];
                if (Modifier.isPublic(method.getModifiers())) {
                    String name = method.getName();
                    String key = "";
                    if (name.startsWith("get")) {
                        if ("getClass".equals(name) || "getDeclaringClass".equals(name)) {
                            key = "";
                        } else {
                            key = name.substring(3);
                        }
                    } else if (name.startsWith("is")) {
                        key = name.substring(2);
                    }
                    if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
                        if (key.length() == 1) {
                            key = key.toLowerCase();
                        } else if (!Character.isUpperCase(key.charAt(1))) {
                            key = key.substring(0, 1).toLowerCase() + key.substring(1);
                        }
                        Object result = method.invoke(bean, (Object[]) null);
                        if(null != result){
                        	map.put(key, wrap(result));
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
		return mapAsJsonObject(map);
    }
	
	public static Object wrap(Object object) {
        try {
            if (object == null) {
                return null;
            }
            if (object instanceof JSONObject || object instanceof JSONArray
                    || JSON.NULL.equals(object) || object instanceof JSONString
                    || object instanceof Byte || object instanceof Character
                    || object instanceof Short || object instanceof Integer
                    || object instanceof Long || object instanceof Boolean
                    || object instanceof Float || object instanceof Double
                    || object instanceof String || object instanceof BigInteger
                    || object instanceof BigDecimal) {
                return object;
            }

            if (object instanceof Collection) {
                Collection<?> coll = (Collection<?>) object;
                return toJSONArray(coll);
            }
            
            if (object instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) object;
                return mapAsJsonObject(map);
            }
            
            Package objectPackage = object.getClass().getPackage();
            String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
            if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || object.getClass().getClassLoader() == null) {
                return object.toString();
            }
            return toJSONObject(object);
        } catch (Exception exception) {
            return null;
        }
    }
	
}