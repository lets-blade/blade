package blade.kit.json;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class JSONHelper {

	public static Object jsonValueAsObject(JSONValue value) {
		if (value.isBoolean())
			return value.asBoolean();
		else if (value.isNumber())
			return value.asInt();
		else if (value.isString())
			return value.asString();
		else if (value.isArray())
			return jsonArrayAsList(value.asArray());
		else if (value.isObject())
			return parseToMap(value.asObject());
		else
			return null;
	}

	public static Set<Object> jsonArrayAsSet(JSONArray array) {
		Set<Object> set = new HashSet<Object>();
		for (JSONValue value : array)
			set.add(jsonValueAsObject(value));
		return set;
	}

	public static List<Object> jsonArrayAsList(JSONArray array) {
		List<Object> list = new ArrayList<Object>(array.size());
		for (JSONValue element : array)
			list.add(jsonValueAsObject(element));
		return list;
	}

	@SuppressWarnings("unchecked")
	public static <V> Map<String, V> parseToMap(JSONObject object) {
		Map<String, V> map = new HashMap<String, V>(object.size(), 1.f);
		for (JSONObject.Member member : object) {
			V v = (V) jsonValueAsObject(member.getValue());
			map.put(member.getName(), v);
		}
		return map;
	}
	
	public static <V> Map<String, V> parseToMap(String json) {
		try {
			JSONObject jsonObject = JSON.parse(json).asObject();
			return parseToMap(jsonObject);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static JSONValue objectAsJsonValue(Object object) {
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
		else if (object instanceof Collection)
			return collectionAsJsonArray((Collection<?>) object);
		else if (object instanceof Map)
			return mapAsJsonObject((Map<?, ?>) object);
		else
			return null;
	}

	public static JSONObject parseJSONObject(Object object) {
		Map<String, Object> map = bean2map(object);
		if (null != map) {
			return mapAsJsonObject(map);
		}
		return null;
	}

	public static JSONArray parseJSONArray(Object object) {
		JSONValue jsonValue = objectAsJsonValue(object);
		if (null != jsonValue) {
			return jsonValue.asArray();
		}
		return null;
	}

	public static JSONArray collectionAsJsonArray(Collection<?> collection) {
		JSONArray array = new JSONArray();
		for (Object element : collection)
			array.add(objectAsJsonValue(element));
		return array;
	}

	public static JSONObject mapAsJsonObject(Map<?, ?> map) {
		JSONObject object = new JSONObject();
		for (Entry<?, ?> entry : map.entrySet())
			object.put(String.valueOf(entry.getKey()), objectAsJsonValue(entry.getValue()));
		return object;
	}

	// Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map
	private static Map<String, Object> bean2map(Object obj) {
		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				// 过滤class属性
				if (!key.equals("class")) {
					// 得到property对应的getter方法
					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					map.put(key, value);
				}
			}
		} catch (Exception e) {
			System.out.println("transBean2Map Error " + e);
		}
		return map;
	}

	// Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean
	private static <T> T map2bean(Map<String, Object> map, Class<T> type) {
		T obj = null;
		try {
			obj = type.getConstructor().newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();
				if (map.containsKey(key)) {
					Object value = map.get(key);
					// 得到property对应的setter方法
					Method setter = property.getWriteMethod();
					setter.invoke(obj, value);
				}
			}
		} catch (Exception e) {
			System.out.println("transMap2Bean Error " + e);
		}
		return obj;
	}

	public static <T> T parse(JSONObject jsonObject, Class<T> type) {
		Map<String, Object> map = parseToMap(jsonObject);
		return map2bean(map, type);
	}
	
	/**
	 * 对象转换为JsonValue对象
	 * 
	 * @param bean
	 * @return
	 */
	public static JSONObject parse(Object bean) {
		JSONObject json = new JSONObject();
		Class<?> klass = bean.getClass();
		boolean includeSuperClass = klass.getClassLoader() != null;

		Method[] methods = includeSuperClass ? klass.getMethods() : klass.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {
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
					if (key.length() > 0 && Character.isUpperCase(key.charAt(0))
							&& method.getParameterTypes().length == 0) {
						if (key.length() == 1) {
							key = key.toLowerCase();
						} else if (!Character.isUpperCase(key.charAt(1))) {
							key = key.substring(0, 1).toLowerCase() + key.substring(1);
						}

						Object result = method.invoke(bean, (Object[]) null);
						if (result != null) {
							json.put(name, result);
						}
					}
				}
			} catch (Exception ignore) {
			}
		}
		return json;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> JSONObject parse(Map<K, V> map) {
		if (map != null && !map.isEmpty()) {
			JSONObject jsonObject = new JSONObject();
			Iterator<?> iterator = map.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, V> entry = (Map.Entry<String, V>) iterator.next();
				String key = entry.getKey();
				Object val = entry.getValue();
				jsonObject.put(key, val);
			}
			return jsonObject;
		}
		return null;
	}
	
}