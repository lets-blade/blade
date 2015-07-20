package blade.kit.json;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class JSONKit {
	
	public static <K,V> Map<K, V> toMap(String json){
		try {
			JSONObject jsonObject = new JSONObject(json);
			return JSONMap.toMap(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <T> List<T> toList(String json){
		JSONArray jsonArray = new JSONArray(json);
		List<T> list = new ArrayList<T>();
		int len = jsonArray.length();
		for(int i=0; i<len; i++){
			T object = (T) jsonArray.get(i);
			list.add(object);
		}
		return list;
	}
	
	public static <V> String toJSONString(Map<String, V> map){
		try {
			return toJSON(map).toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <V> JSONObject toJSON(Map<String, V> map){
		try {
			JSONObject jsonObj = new JSONObject();
			for (Map.Entry<String, V> entry : map.entrySet()) {
				String key = entry.getKey();
				Object value = entry.getValue();
				try {
					jsonObj.put(key, value);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return jsonObj;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static <V> String toJSONString(List<Map<String, V>> list){
		if(null != list && list.size() > 0){
			JSONArray jsonArr = new JSONArray();
			for (Map<String, V> map : list) {
				jsonArr.put(toJSON(map));
			}
			return jsonArr.toString();
		}
		return null;
	}
	
	public static String toJSONString(Object bean){
		try {
			JSONObject jsonObject = new JSONObject(bean);
			return jsonObject.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
