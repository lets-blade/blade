package blade.kit.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * http util
 * @author biezhi
 *
 */
public class HttpKit {
	
	/**
	 * 参数转换为map,a=1&b=2
	 * @param query
	 * @param decode
	 * @return Map<String, Object>
	 */
	public static Map<String, Object> parseQuery(String query, boolean decode) {
		Map<String, Object> queryMap = new HashMap<String, Object>();
		String[] kvArr = query.split("&");
		if(null != kvArr && kvArr.length > 0){
			for(String k : kvArr){
				if(k.indexOf("=") != -1){
					String[] kv = k.split("=");
					queryMap.put(kv[0], kv[1]);
				}
			}
		}
		return queryMap;
	}
	
	/**
	 * map转为url参数
	 * @param map
	 * @return String
	 */
	public static String getQuery(Map<String, Object> map){
		try {
			if(null != map && map.size() > 0){
				StringBuffer sBuffer = new StringBuffer();
				Set<String> keySet = map.keySet();
				for(String key : keySet){
					sBuffer.append(key + "=" + URLEncoder.encode(map.get(key).toString(), "UTF-8") + "&");
				}
				return sBuffer.substring(0, sBuffer.length() - 1).toString();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static HttpRequest createRequest(String url){
		return new HttpRequest(url);
	}
}

