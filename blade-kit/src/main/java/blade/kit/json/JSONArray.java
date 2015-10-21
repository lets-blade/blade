/*
 * $Id: JSONArray.java,v 1.1 2006/04/15 14:10:48 platform Exp $
 * Created on 2006-4-10
 */
package blade.kit.json;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * A JSON array. JSONObject supports java.util.List interface.
 * 
 * @author FangYidong<fangyidong@yahoo.com.cn>
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class JSONArray extends ArrayList implements JSONAware, JSONStreamAware {
	
	private static final long serialVersionUID = 3957988303675231981L;
	
	/**
	 * Constructs an empty JSONArray.
	 */
	public JSONArray(){
		super();
	}
	
	/**
	 * Constructs a JSONArray containing the elements of the specified
	 * collection, in the order they are returned by the collection's iterator.
	 * 
	 * @param c the collection whose elements are to be placed into this JSONArray
	 */
	public JSONArray(Collection c){
		super(c);
	}
	
	public boolean add(Object object){
		return this.add(object);
	}
	
	public void add(int index, Object object){
		this.add(index, object);
	}
	
	public boolean addAll(Collection collection){
		return this.addAll(collection);
	}
	
	public boolean addAll(int index, Collection collection){
		return this.addAll(index, collection);
	}
	
    /**
     * Encode a list into JSON text and write it to out. 
     * If this list is also a JSONStreamAware or a JSONAware, JSONStreamAware and JSONAware specific behaviours will be ignored at this top level.
     * 
     * @see blade.kit.json2.JSONValue#writeJSONString(Object, Writer)
     * 
     * @param collection
     * @param out
     */
	public static void writeJSONString(Collection collection, Writer out) throws IOException{
		if(collection == null){
			out.write("null");
			return;
		}
		
		boolean first = true;
		Iterator iter=collection.iterator();
		
        out.write('[');
		while(iter.hasNext()){
            if(first)
                first = false;
            else
                out.write(',');
            
			Object value=iter.next();
			if(value == null){
				out.write("null");
				continue;
			}
			
			JSONValue.writeJSONString(value, out);
		}
		out.write(']');
	}
	
	public void writeJSONString(Writer out) throws IOException{
		writeJSONString(this, out);
	}
	
	/**
	 * Convert a list to JSON text. The result is a JSON array. 
	 * If this list is also a JSONAware, JSONAware specific behaviours will be omitted at this top level.
	 * 
	 * @see blade.kit.json2.JSONValue#toJSONString(Object)
	 * 
	 * @param collection
	 * @return JSON text, or "null" if list is null.
	 */
	public static String toJSONString(Collection collection){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(collection, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}

	public static void writeJSONString(byte[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(byte[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(short[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(short[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(int[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(int[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(long[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(long[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(float[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(float[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(double[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(double[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(boolean[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(boolean[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(char[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[\"");
			out.write(String.valueOf(array[0]));
			
			for(int i = 1; i < array.length; i++){
				out.write("\",\"");
				out.write(String.valueOf(array[i]));
			}
			
			out.write("\"]");
		}
	}
	
	public static String toJSONString(char[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public static void writeJSONString(Object[] array, Writer out) throws IOException{
		if(array == null){
			out.write("null");
		} else if(array.length == 0) {
			out.write("[]");
		} else {
			out.write("[");
			JSONValue.writeJSONString(array[0], out);
			
			for(int i = 1; i < array.length; i++){
				out.write(",");
				JSONValue.writeJSONString(array[i], out);
			}
			
			out.write("]");
		}
	}
	
	public static String toJSONString(Object[] array){
		final StringWriter writer = new StringWriter();
		
		try {
			writeJSONString(array, writer);
			return writer.toString();
		} catch(IOException e){
			// This should never happen for a StringWriter
			throw new RuntimeException(e);
		}
	}
	
	public String toJSONString(){
		return toJSONString(this);
	}

	/**
	 * Returns a string representation of this array. This is equivalent to
	 * calling {@link JSONArray#toJSONString()}.
	 */
	public String toString() {
		return toJSONString();
	}
	
	/**
     * Get the object value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return An object value.
     * @throws JSONException
     *             If there is no value for the index.
     */
    public Object get(int index) throws JSONException {
        Object object = this.get(index);
        if (object == null) {
            throw new JSONException("JSONArray[" + index + "] not found.");
        }
        return object;
    }

    /**
     * Get the boolean value associated with an index. The string values "true"
     * and "false" are converted to boolean.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The truth.
     * @throws JSONException
     *             If there is no value for the index or if the value is not
     *             convertible to boolean.
     */
    public boolean getBoolean(int index) throws JSONException {
        Object object = this.get(index);
        if (object.equals(Boolean.FALSE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("false"))) {
            return false;
        } else if (object.equals(Boolean.TRUE)
                || (object instanceof String && ((String) object)
                        .equalsIgnoreCase("true"))) {
            return true;
        }
        throw new JSONException("JSONArray[" + index + "] is not a boolean.");
    }

    /**
     * Get the double value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public double getDouble(int index) throws JSONException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).doubleValue()
                    : Double.parseDouble((String) object);
        } catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the BigDecimal value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value cannot be converted
     *             to a BigDecimal.
     */
    public BigDecimal getBigDecimal (int index) throws JSONException {
        Object object = this.get(index);
        try {
            return new BigDecimal(object.toString());
        } catch (Exception e) {
            throw new JSONException("JSONArray[" + index +
                    "] could not convert to BigDecimal.");
        }
    }

    /**
     * Get the BigInteger value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value cannot be converted
     *             to a BigInteger.
     */
    public BigInteger getBigInteger (int index) throws JSONException {
        Object object = this.get(index);
        try {
            return new BigInteger(object.toString());
        } catch (Exception e) {
            throw new JSONException("JSONArray[" + index +
                    "] could not convert to BigInteger.");
        }
    }

    /**
     * Get the int value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value is not a number.
     */
    public int getInt(int index) throws JSONException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).intValue()
                    : Integer.parseInt((String) object);
        } catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the JSONArray associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A JSONArray value.
     * @throws JSONException
     *             If there is no value for the index. or if the value is not a
     *             JSONArray
     */
    public JSONArray getJSONArray(int index) throws JSONException {
        Object object = this.get(index);
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONArray.");
    }

    /**
     * Get the JSONObject associated with an index.
     *
     * @param index
     *            subscript
     * @return A JSONObject value.
     * @throws JSONException
     *             If there is no value for the index or if the value is not a
     *             JSONObject
     */
    public JSONObject getJSONObject(int index) throws JSONException {
        Object object = this.get(index);
        if (object instanceof JSONObject) {
            return (JSONObject) object;
        }
        throw new JSONException("JSONArray[" + index + "] is not a JSONObject.");
    }

    /**
     * Get the long value associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return The value.
     * @throws JSONException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public long getLong(int index) throws JSONException {
        Object object = this.get(index);
        try {
            return object instanceof Number ? ((Number) object).longValue()
                    : Long.parseLong((String) object);
        } catch (Exception e) {
            throw new JSONException("JSONArray[" + index + "] is not a number.");
        }
    }

    /**
     * Get the string associated with an index.
     *
     * @param index
     *            The index must be between 0 and length() - 1.
     * @return A string value.
     * @throws JSONException
     *             If there is no string value for the index.
     */
    public String getString(int index) throws JSONException {
        Object object = this.get(index);
        if (object instanceof String) {
            return (String) object;
        }
        throw new JSONException("JSONArray[" + index + "] not a string.");
    }
}
