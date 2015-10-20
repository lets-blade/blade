package blade.kit.json;

import java.util.List;
import java.util.Map;

import blade.kit.base.ThrowableKit;
import blade.kit.json.parse.ParseException;
import blade.kit.log.Logger;

/**
 * JSON工具类
 * @author biezhi
 *
 */
@SuppressWarnings("unchecked")
public class JSONKit {
	
	private static final Logger LOGGER = Logger.getLogger(JSONKit.class);
	
	public static <K,V> Map<K, V> toMap(String json){
		try {
			return (Map<K, V>) JSONValue.parse(json);
		} catch (ParseException e) {
			String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
		}
		return null;
	}
	
	public static <T> List<T> toList(String json){
		try {
			return (List<T>) JSONValue.parse(json);
		} catch (ParseException e) {
			String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
		}
		return null;
	}
	
	public static <T> T parse(final String json) {
		try {
			return (T) JSONValue.parse(json);
		} catch (ParseException e) {
			String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
		}
		return null;
	}
	
	public static <T> T parse(final String json, Class<T> type) {
		try {
			return (T) JSONValue.parse(json);
		} catch (ParseException e) {
			String error = ThrowableKit.getStackTraceAsString(e);
            LOGGER.error(error);
            ThrowableKit.propagate(e);
		}
		return null;
	}
	
	public static String toJSONString(Object object){
		return JSONValue.toJSONString(object);
	}
	
}
