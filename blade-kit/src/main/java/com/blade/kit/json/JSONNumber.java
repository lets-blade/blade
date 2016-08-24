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

@SuppressWarnings("serial") // use default serial UID
class JSONNumber extends JSONValue {

	private final String string;

	JSONNumber(String string) {
		if (string == null) {
			throw new NullPointerException("string is null");
		}
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	void write(JSONWriter writer) throws IOException {
		writer.writeNumber(string);
	}

	@Override
	public boolean isNumber() {
		return true;
	}

	@Override
	public Integer asInt() {
		return Integer.parseInt(string, 10);
	}

	@Override
	public Long asLong() {
		return Long.parseLong(string, 10);
	}

	@Override
	public Float asFloat() {
		return Float.parseFloat(string);
	}

	@Override
	public Double asDouble() {
		return Double.parseDouble(string);
	}
	
	public Byte asByte() {
		return Byte.parseByte(string);
	}
	
	@Override
	public int hashCode() {
		return string.hashCode();
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
		JSONNumber other = (JSONNumber) object;
		return string.equals(other.string);
	}

}
