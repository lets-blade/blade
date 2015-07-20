package blade.kit.json;

import java.util.Iterator;
import java.util.Map;

import blade.kit.CollectionKit;

@SuppressWarnings("unchecked")
public class JSONMap {
	
    public static <V> JSONObject toJSONObject(Map<String, V> map) throws JSONException {
        JSONObject jo = new JSONObject();
        if (map != null && !map.isEmpty()) {
        	Iterator<?> iterator = map.entrySet().iterator();
            while(iterator.hasNext()) {
            	Map.Entry<String, V> entry = (Map.Entry<String, V>) iterator.next();
            	String key = entry.getKey();
            	Object val = entry.getValue();
            	jo.put(key, val);
            }
        }
        return jo;
    }

	public static <K, V> Map<K,V> toMap(JSONObject jo)  throws JSONException {
        Map<K, V>  properties = CollectionKit.newHashMap();
        if (jo != null) {
            Iterator<String> keys = jo.keys();
            while (keys.hasNext()) {
                String name = keys.next();
                K k = (K) name;
                V v = (V) jo.get(name);
                properties.put(k, v);
            }
        }
        return properties;
    }
}
