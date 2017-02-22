package com.blade.kit;

import com.blade.kit.reflect.ConvertKit;
import com.blade.kit.reflect.ReflectKit;

import java.lang.reflect.Field;
import java.util.Map;

public final class ObjectKit {

	private ObjectKit() {
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T model(String slug, Class<?> clazz, Map<String, String> params) {
		try {
			Object obj = ReflectKit.newInstance(clazz);
			Field[] fields = clazz.getDeclaredFields();
			if (null == fields || fields.length == 0) {
				return null;
			}

			for (Field field : fields) {
				field.setAccessible(true);
				if (field.getName().equals("serialVersionUID")) {
					continue;
				}
				String fieldName = StringKit.isNotBlank(slug) ? slug + "." + field.getName() : field.getName();
				String fieldValue = params.get(fieldName);

				if (null != fieldValue) {
					Object value = ConvertKit.convert(field.getType(), fieldValue);
					field.set(obj, value);
				}
			}
			return (T) obj;
        } catch (NumberFormatException | IllegalAccessException | SecurityException e) {
            e.printStackTrace();
		}
		return null;
	}
	
}
