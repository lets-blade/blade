/*
 * $Id: JSONObject.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package blade.kit.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
@SuppressWarnings("rawtypes")
public class JSONObject extends HashMap implements Map, JSONAware, JSONStreamAware{
	
	private static final long serialVersionUID = -503443796854799292L;
	
	
	public JSONObject() {
		super();
	}

	/**
	 * Allows creation of a JSONObject from a Map. After that, both the
	 * generated JSONObject and the Map can be modified independently.
	 * 
	 * @param map
	 */
	@SuppressWarnings("unchecked")
	public JSONObject(Map map) {
		super(map);
	}
	
	public boolean put(String key, Object value){
		return this.put(key, value);
	}
	
	public Object put(Object key, Object value){
		return this.put(key, value);
	}
	
	public Object get(String key) throws JSONException {
        if (key == null) {
            throw new JSONException("Null key.");
        }
        Object object = this.get(key);
        if (object == null) {
            throw new JSONException("JSONObject[" + quote(key) + "] not found.");
        }
        return object;
    }
	
	public String getString(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof String) {
            return (String) object;
        }
        throw new JSONException("JSONObject[" + quote(key) + "] not a string.");
    }
	
	public int getInt(String key) throws JSONException {
		Object object = this.get(key);
		try {
			return object instanceof Number ? ((Number) object).intValue() : Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new JSONException("JSONObject[" + quote(key) + "] is not an int.");
		}
	}
	
	/**
     * Get the boolean value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The truth.
     * @throws JSONException
     *             if the value is not a Boolean or the String "true" or
     *             "false".
     */
    public boolean getBoolean(String key) throws JSONException {
        Object object = this.get(key);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONObject[" + quote(key)
                + "] is not a Boolean.");
    }

    /**
     * Get the BigInteger value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value cannot 
     *             be converted to BigInteger.
     */
    public BigInteger getBigInteger(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return new BigInteger(object.toString());
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key)
                    + "] could not be converted to BigInteger.");
        }
    }

    /**
     * Get the BigDecimal value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value
     *             cannot be converted to BigDecimal.
     */
    public BigDecimal getBigDecimal(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return new BigDecimal(object.toString());
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key)
                    + "] could not be converted to BigDecimal.");
        }
    }

    /**
     * Get the double value associated with a key.
     *
     * @param key
     *            A key string.
     * @return The numeric value.
     * @throws JSONException
     *             if the key is not found or if the value is not a Number
     *             object and cannot be converted to a number.
     */
    public double getDouble(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key)
                    + "] is not a number.");
        }
    }
    
    public JSONArray getJSONArray(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        throw new JSONException("JSONObject[" + quote(key)
                + "] is not a JSONArray.");
    }
    
    public JSONObject getJSONObject(String key) throws JSONException {
        Object object = this.get(key);
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        throw new JSONException("JSONObject[" + quote(key)
                + "] is not a JSONObject.");
    }
    
    public long getLong(String key) throws JSONException {
        Object object = this.get(key);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (Exception e) {
            throw new JSONException("JSONObject[" + quote(key)
                    + "] is not a long.");
        }
    }
    
	public static String quote(String string) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(string, sw).toString();
            } catch (IOException ignored) {
                // will never happen - we are writing to a string writer
                return "";
            }
        }
    }
	
	public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.length() == 0) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }

    /**
     * Encode a map into JSON text and write it to out.
     * If this map is also a JSONAware or JSONStreamAware, JSONAware or JSONStreamAware specific behaviours will be ignored at this top level.
     * 
     * @see blade.kit.json2.JSONValue#writeJSONString(Object, Writer)
     * 
     * @param map
     * @param out
     */
	public static void writeJSONString(Map map, Writer out) throws IOException {
		if(map == null){
			out.write("null");
			return;
		}
		
		boolean first = true;
		Iterator iter=map.entrySet().iterator();
		
        out.write('{');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                out.write(',');
			Map.Entry entry=(Map.Entry)iter.next();
            out.write('\"');
            out.write(escape(String.valueOf(entry.getKey())));
            out.write('\"');
            out.write(':');
			JSONValue.writeJSONString(entry.getValue(), out);
		}
		out.write('}');
	}

	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}
	
	/**
	 * Convert a map to JSON text. The result is a JSON object. 
	 * If this map is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
	 * 
	 * @see blade.kit.json2.JSONValue#toJSONString(Object)
	 * 
	 * @param map
	 * @return JSON text, or "null" if map is null.
	 */
	public static String toJSONString(Map map){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(map, writer);
			return writer.toString();
		} catch (IOException e) {
			// This should never happen with a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public String toJSONString(){
		return toJSONString(this);
	}
	
	public String toString(){
		return toJSONString();
	}

	public static String toString(String key,Object value){
        StringBuffer sb = new StringBuffer();
        sb.append('\"');
        if(key == null)
            sb.append("null");
        else
            JSONValue.escape(key, sb);
		sb.append('\"').append(':');
		
		sb.append(JSONValue.toJSONString(value));
		
		return sb.toString();
	}
	
	/**
	 * Escape quotes, \, /, \r, \n, \b, \f, \t and other control characters (U+0000 through U+001F).
	 * It's the same as JSONValue.escape() only for compatibility here.
	 * 
	 * @see blade.kit.json2.JSONValue#escape(String)
	 * 
	 * @param s
	 * @return
	 */
	public static String escape(String s){
		return JSONValue.escape(s);
	}
}
