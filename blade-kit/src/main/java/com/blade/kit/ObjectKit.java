package com.blade.kit;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Map;

import com.blade.kit.exception.ReflectException;
import com.blade.kit.reflect.ReflectKit;

public final class ObjectKit {

	private ObjectKit() {
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T model(String slug, Class<?> clazz, Map<String, String> params){
		try {
			Object obj = ReflectKit.newInstance(clazz);
			Field[] fields = clazz.getDeclaredFields();
			if(null == fields || fields.length == 0){
				return null;
			}
			
			for(Field field : fields){
				field.setAccessible(true);
				if(field.getName().equals("serialVersionUID")){
					continue;
				}
				String fieldName = slug + "." + field.getName();
				String fieldValue = params.get(fieldName);
				
				if(null != fieldValue){
					Object value = parse(field.getType(), fieldValue);
					field.set(obj, value);
				}
				
			}
			return (T) obj;
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			throw new ReflectException(e);
		} catch (IllegalAccessException e) {
			throw new ReflectException(e);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Object parse(Class<?> type, String value){
		if (type == Integer.class) {
			return Integer.parseInt(value);
		} else if (type == String.class) {
			return value;
		} else if (type == Date.class) {
			return DateKit.convertToDate(value);
		} else if (type == Double.class) {
			return Double.parseDouble(value);
		} else if (type == Float.class) {
			return Float.parseFloat(value);
		} else if (type == Long.class) {
			return Long.parseLong(value);
		} else if (type == Boolean.class) {
			return Boolean.parseBoolean(value);
		} else if (type == Short.class) {
			return Short.parseShort(value);
		}
		return value;
	}
	
}
