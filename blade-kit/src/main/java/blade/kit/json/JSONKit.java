package blade.kit.json;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class JSONKit {
	
	public static <V> Map<String, V> toMap(String json){
		return JSONHelper.parseToMap(json);
	}
	
	public static <V> Map<String, V> toMap(JSONObject jsonObject){
		return JSONHelper.parseToMap(jsonObject);
	}
	
	public static <T> T parse(JSONObject jsonObject, Class<T> type){
		return JSONHelper.parse(jsonObject, type);
	}
	
	public static String toJSONString(Object bean){
		return JSONHelper.parse(bean).toString();
	}
	
	public static <K, V> String toJSONString(Map<K, V> map){
		return JSONHelper.mapAsJsonObject(map).toString();
	}
	
	public static <T> String toJSONString(List<T> list){
		if(null != list && list.size() > 0){
			JSONArray jsonArray = new JSONArray();
			for(T oT : list){
				jsonArray.add(JSONHelper.parse(oT));
			}
			return jsonArray.toString();
		}
		return null;
	}

	public static JSONValue parse(String json) {
		try {
			return new JSONParser(json).parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
