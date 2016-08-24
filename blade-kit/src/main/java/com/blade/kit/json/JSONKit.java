package com.blade.kit.json;

import com.blade.kit.StringKit;

public final class JSONKit {

	private JSONKit() {}
	
	public static String toJSONString(Object object){
		if(null == object){
			return null;
		}
		return JSONHelper.toJSONValue(object).toString();
	}
	
	public static String toJSONString(Object object, boolean flag){
		if(!flag){
			return toJSONString(object);
		}
		if(null == object){
			return null;
		}
		return JSONHelper.toJSONValue(object).toString(WriterConfig.PRETTY_PRINT);
	}

	public static <T> T parse(String json, Class<T> clazz) {
		if(StringKit.isBlank(json) || null == clazz){
			return null;
		}
		return JSONHelper.toBean(json, clazz);
	}

	public static JSONObject parseObject(String json) {
		return JSON.parse(json).asJSONObject();
	}
	
	
}