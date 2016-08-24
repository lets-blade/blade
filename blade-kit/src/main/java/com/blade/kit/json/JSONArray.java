/*******************************************************************************
 * Copyright (c) 2013, 2015 EclipseSource.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.blade.kit.json;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Represents a JSON array, an ordered collection of JSON values.
 * <p>
 * Elements can be added using the <code>add(...)</code> methods which accept
 * instances of {@link JSONValue}, strings, primitive numbers, and boolean
 * values. To replace an element of an array, use the <code>set(int, ...)</code>
 * methods.
 * </p>
 * <p>
 * Elements can be accessed by their index using {@link #get(int)}. This class
 * also supports iterating over the elements in document order using an
 * {@link #iterator()} or an enhanced for loop:
 * </p>
 * 
 * <pre>
 * for (JsonValue value : jsonArray) {
 *   ...
 * }
 * </pre>
 * <p>
 * An equivalent {@link List} can be obtained from the method {@link #values()}.
 * </p>
 * <p>
 * Note that this class is <strong>not thread-safe</strong>. If multiple threads
 * access a <code>JsonArray</code> instance concurrently, while at least one of
 * these threads modifies the contents of this array, access to the instance
 * must be synchronized externally. Failure to do so may lead to an inconsistent
 * state.
 * </p>
 * <p>
 * This class is <strong>not supposed to be extended</strong> by clients.
 * </p>
 */
@SuppressWarnings("serial") // use default serial UID
public class JSONArray extends JSONValue implements Iterable<JSONValue> {

	private final List<JSONValue> values;

	/**
	 * Creates a new empty JsonArray.
	 */
	public JSONArray() {
		values = new ArrayList<JSONValue>();
	}

	/**
	 * Creates a new JsonArray with the contents of the specified JSON array.
	 *
	 * @param array
	 *            the JsonArray to get the initial contents from, must not be
	 *            <code>null</code>
	 */
	public JSONArray(JSONArray array) {
		this(array, false);
	}

	private JSONArray(JSONArray array, boolean unmodifiable) {
		if (array == null) {
			throw new NullPointerException("array is null");
		}
		if (unmodifiable) {
			values = Collections.unmodifiableList(array.values);
		} else {
			values = new ArrayList<JSONValue>(array.values);
		}
	}
	
	/**
	 * Returns an unmodifiable wrapper for the specified JsonArray. This method
	 * allows to provide read-only access to a JsonArray.
	 * <p>
	 * The returned JsonArray is backed by the given array and reflects
	 * subsequent changes. Attempts to modify the returned JsonArray result in
	 * an <code>UnsupportedOperationException</code>.
	 * </p>
	 *
	 * @param array
	 *            the JsonArray for which an unmodifiable JsonArray is to be
	 *            returned
	 * @return an unmodifiable view of the specified JsonArray
	 */
	public static JSONArray unmodifiableArray(JSONArray array) {
		return new JSONArray(array, true);
	}

	/**
	 * Appends the JSON representation of the specified <code>int</code> value
	 * to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(int value) {
		values.add(JSON.value(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>long</code> value
	 * to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(long value) {
		values.add(JSON.value(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>float</code> value
	 * to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(float value) {
		values.add(JSON.value(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>double</code>
	 * value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(double value) {
		values.add(JSON.value(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified <code>boolean</code>
	 * value to the end of this array.
	 *
	 * @param value
	 *            the value to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(boolean value) {
		values.add(JSON.value(value));
		return this;
	}

	/**
	 * Appends the JSON representation of the specified string to the end of
	 * this array.
	 *
	 * @param value
	 *            the string to add to the array
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(String value) {
		values.add(JSON.value(value));
		return this;
	}
	
	public JSONArray add(Object value) {
		values.add(JSON.value(value));
		return this;
	}
	
	/**
	 * Appends the specified JSON value to the end of this array.
	 *
	 * @param value
	 *            the JsonValue to add to the array, must not be
	 *            <code>null</code>
	 * @return the array itself, to enable method chaining
	 */
	public JSONArray add(JSONValue value) {
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		values.add(value);
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>int</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, int value) {
		values.set(index, JSON.value(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>long</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, long value) {
		values.set(index, JSON.value(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>float</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, float value) {
		values.set(index, JSON.value(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>double</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, double value) {
		values.set(index, JSON.value(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified <code>boolean</code> value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, boolean value) {
		values.set(index, JSON.value(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * JSON representation of the specified string.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the string to be stored at the specified array position
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, String value) {
		values.set(index, JSON.value(value));
		return this;
	}

	/**
	 * Replaces the element at the specified position in this array with the
	 * specified JSON value.
	 *
	 * @param index
	 *            the index of the array element to replace
	 * @param value
	 *            the value to be stored at the specified array position, must
	 *            not be <code>null</code>
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray set(int index, JSONValue value) {
		if (value == null) {
			throw new NullPointerException("value is null");
		}
		values.set(index, value);
		return this;
	}

	/**
	 * Removes the element at the specified index from this array.
	 *
	 * @param index
	 *            the index of the element to remove
	 * @return the array itself, to enable method chaining
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONArray remove(int index) {
		values.remove(index);
		return this;
	}

	/**
	 * Returns the number of elements in this array.
	 *
	 * @return the number of elements in this array
	 */
	public int size() {
		return values.size();
	}

	/**
	 * Returns <code>true</code> if this array contains no elements.
	 *
	 * @return <code>true</code> if this array contains no elements
	 */
	public boolean isEmpty() {
		return values.isEmpty();
	}

	/**
	 * Returns the value of the element at the specified position in this array.
	 *
	 * @param index
	 *            the index of the array element to return
	 * @return the value of the element at the specified position
	 * @throws IndexOutOfBoundsException
	 *             if the index is out of range, i.e. <code>index &lt; 0</code>
	 *             or <code>index &gt;= size</code>
	 */
	public JSONValue get(int index) {
		return values.get(index);
	}

	/**
	 * Returns a list of the values in this array in document order. The
	 * returned list is backed by this array and will reflect subsequent
	 * changes. It cannot be used to modify this array. Attempts to modify the
	 * returned list will result in an exception.
	 *
	 * @return a list of the values in this array
	 */
	public List<JSONValue> values() {
		return Collections.unmodifiableList(values);
	}

	/**
	 * Returns an iterator over the values of this array in document order. The
	 * returned iterator cannot be used to modify this array.
	 *
	 * @return an iterator over the values of this array
	 */
	public Iterator<JSONValue> iterator() {
		final Iterator<JSONValue> iterator = values.iterator();
		return new Iterator<JSONValue>() {

			public boolean hasNext() {
				return iterator.hasNext();
			}

			public JSONValue next() {
				return iterator.next();
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
		};
	}

	@Override
	void write(JSONWriter writer) throws IOException {
		writer.writeArrayOpen();
		Iterator<JSONValue> iterator = iterator();
		boolean first = true;
		while (iterator.hasNext()) {
			if (!first) {
				writer.writeArraySeparator();
			}
			iterator.next().write(writer);
			first = false;
		}
		writer.writeArrayClose();
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public JSONArray asArray() {
		return this;
	}

	@Override
	public int hashCode() {
		return values.hashCode();
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) {
			return true;
		}
		if (object == null) {
			return false;
		}
		if (getClass() != object.getClass()) {
			return false;
		}
		JSONArray other = (JSONArray) object;
		return values.equals(other.values);
	}

}
