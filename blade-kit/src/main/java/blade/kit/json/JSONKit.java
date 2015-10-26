package blade.kit.json;

import java.util.List;
import java.util.Map;

public class JSONKit {
	
	public static <V> Map<String, V> toMap(String json){
		return Json.parseToMap(json);
	}
	
	public static <V> Map<String, V> toMap(JsonObject jsonObject){
		return Json.parseToMap(jsonObject);
	}
	
	public static String toJSONString(Object bean){
		return Json.parse(bean).toString();
	}
	
	public static <K, V> String toJSONString(Map<K, V> map){
		return Json.parse(map).toString();
	}
	
	public static <T> String toJSONString(List<T> list){
		if(null != list && list.size() > 0){
			JsonArray jsonArray = new JsonArray();
			for(T oT : list){
				jsonArray.add(Json.parse(oT));
			}
			return jsonArray.toString();
		}
		return null;
	}
	
}
