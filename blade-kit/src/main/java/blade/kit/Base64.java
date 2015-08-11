/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package blade.kit;

import java.io.UnsupportedEncodingException;

/**
 * BASE64工具类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Base64 {
	/** The equals sign (=) as a byte. */
    private final static byte EQUALS_SIGN = (byte) '=';

    /** Preferred encoding. */
    private final static String PREFERRED_ENCODING = "US-ASCII";

    /** The 64 valid Base64 values. */
    private final static byte[] _STANDARD_ALPHABET = { (byte) 'A', (byte) 'B',
        (byte) 'C', (byte) 'D', (byte) 'E', (byte) 'F', (byte) 'G', (byte) 'H',
        (byte) 'I', (byte) 'J', (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N',
        (byte) 'O', (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
        (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y', (byte) 'Z',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e', (byte) 'f',
        (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j', (byte) 'k', (byte) 'l',
        (byte) 'm', (byte) 'n', (byte) 'o', (byte) 'p', (byte) 'q', (byte) 'r',
        (byte) 's', (byte) 't', (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x',
        (byte) 'y', (byte) 'z', (byte) '0', (byte) '1', (byte) '2', (byte) '3',
        (byte) '4', (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
        (byte) '+', (byte) '/' };
    
    private static byte[] base64DecodeChars = new byte[] { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
		-1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4,
		5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, -1, -1, 26,
		27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1,
		-1, -1, -1 };
    
    /** Defeats instantiation. */
    private Base64() {
    }

    /**
     * <p>
     * Encodes up to three bytes of the array <var>source</var> and writes the
     * resulting four Base64 bytes to <var>destination</var>. The source and
     * destination arrays can be manipulated anywhere along their length by
     * specifying <var>srcOffset</var> and <var>destOffset</var>. This method
     * does not check to make sure your arrays are large enough to accomodate
     * <var>srcOffset</var> + 3 for the <var>source</var> array or
     * <var>destOffset</var> + 4 for the <var>destination</var> array. The
     * actual number of significant bytes in your array is given by
     * <var>numSigBytes</var>.
     * </p>
     * <p>
     * This is the lowest level of the encoding methods with all possible
     * parameters.
     * </p>
     *
     * @param source
     *          the array to convert
     * @param srcOffset
     *          the index where conversion begins
     * @param numSigBytes
     *          the number of significant bytes in your array
     * @param destination
     *          the array to hold the conversion
     * @param destOffset
     *          the index where output will be put
     * @return the <var>destination</var> array
     * @since 1.3
     */
    private static byte[] encode3to4(byte[] source, int srcOffset,
        int numSigBytes, byte[] destination, int destOffset) {

      byte[] ALPHABET = _STANDARD_ALPHABET;

      int inBuff = (numSigBytes > 0 ? ((source[srcOffset] << 24) >>> 8) : 0)
          | (numSigBytes > 1 ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
          | (numSigBytes > 2 ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

      switch (numSigBytes) {
      case 3:
        destination[destOffset] = ALPHABET[(inBuff >>> 18)];
        destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
        destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];
        return destination;

      case 2:
        destination[destOffset] = ALPHABET[(inBuff >>> 18)];
        destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
        destination[destOffset + 3] = EQUALS_SIGN;
        return destination;

      case 1:
        destination[destOffset] = ALPHABET[(inBuff >>> 18)];
        destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = EQUALS_SIGN;
        destination[destOffset + 3] = EQUALS_SIGN;
        return destination;

      default:
        return destination;
      }
    }

    /**
     * Encode string as a byte array in Base64 annotation.
     *
     * @param string
     * @return The Base64-encoded data as a string
     */
    public static String encode(String string) {
      byte[] bytes;
      try {
        bytes = string.getBytes(PREFERRED_ENCODING);
      } catch (UnsupportedEncodingException e) {
        bytes = string.getBytes();
      }
      return encodeBytes(bytes);
    }

    /**
     * Encodes a byte array into Base64 notation.
     *
     * @param source
     *          The data to convert
     * @return The Base64-encoded data as a String
     * @throws NullPointerException
     *           if source array is null
     * @throws IllegalArgumentException
     *           if source array, offset, or length are invalid
     * @since 2.0
     */
    public static String encodeBytes(byte[] source) {
      return encodeBytes(source, 0, source.length);
    }

    /**
     * Encodes a byte array into Base64 notation.
     *
     * @param source
     *          The data to convert
     * @param off
     *          Offset in array where conversion should begin
     * @param len
     *          Length of data to convert
     * @return The Base64-encoded data as a String
     * @throws NullPointerException
     *           if source array is null
     * @throws IllegalArgumentException
     *           if source array, offset, or length are invalid
     * @since 2.0
     */
    public static String encodeBytes(byte[] source, int off, int len) {
      byte[] encoded = encodeBytesToBytes(source, off, len);
      try {
        return new String(encoded, PREFERRED_ENCODING);
      } catch (UnsupportedEncodingException uue) {
        return new String(encoded);
      }
    }

    /**
     * Similar to {@link #encodeBytes(byte[], int, int)} but returns a byte
     * array instead of instantiating a String. This is more efficient if you're
     * working with I/O streams and have large data sets to encode.
     *
     *
     * @param source
     *          The data to convert
     * @param off
     *          Offset in array where conversion should begin
     * @param len
     *          Length of data to convert
     * @return The Base64-encoded data as a String if there is an error
     * @throws NullPointerException
     *           if source array is null
     * @throws IllegalArgumentException
     *           if source array, offset, or length are invalid
     * @since 2.3.1
     */
    public static byte[] encodeBytesToBytes(byte[] source, int off, int len) {

      if (source == null)
        throw new NullPointerException("Cannot serialize a null array.");

      if (off < 0)
        throw new IllegalArgumentException("Cannot have negative offset: "
            + off);

      if (len < 0)
        throw new IllegalArgumentException("Cannot have length offset: " + len);

      if (off + len > source.length)
        throw new IllegalArgumentException(
            String
                .format(
                    "Cannot have offset of %d and length of %d with array of length %d",
                    off, len, source.length));

      // Bytes needed for actual encoding
      int encLen = (len / 3) * 4 + (len % 3 > 0 ? 4 : 0);

      byte[] outBuff = new byte[encLen];

      int d = 0;
      int e = 0;
      int len2 = len - 2;
      for (; d < len2; d += 3, e += 4)
        encode3to4(source, d + off, 3, outBuff, e);

      if (d < len) {
        encode3to4(source, d + off, len - d, outBuff, e);
        e += 4;
      }

      if (e <= outBuff.length - 1) {
        byte[] finalOut = new byte[e];
        System.arraycopy(outBuff, 0, finalOut, 0, e);
        return finalOut;
      } else
        return outBuff;
    }
	
  //解码
  	public static byte[] decode(String str) throws UnsupportedEncodingException {
  		StringBuffer sb = new StringBuffer();
  		byte[] data = str.getBytes("US-ASCII");
  		int len = data.length;
  		int i = 0;
  		int b1, b2, b3, b4;
  		while (i < len) {
  			/* b1 */
  			do {
  				b1 = base64DecodeChars[data[i++]];
  			} while (i < len && b1 == -1);
  			if (b1 == -1)
  				break;
  			/* b2 */
  			do {
  				b2 = base64DecodeChars[data[i++]];
  			} while (i < len && b2 == -1);
  			if (b2 == -1)
  				break;
  			sb.append((char) ((b1 << 2) | ((b2 & 0X30) >>> 4)));
  			/* b3 */
  			do {
  				b3 = data[i++];
  				if (b3 == 61)
  					return sb.toString().getBytes("iso8859-1");
  				b3 = base64DecodeChars[b3];
  			} while (i < len && b3 == -1);
  			if (b3 == -1)
  				break;
  			sb.append((char) (((b2 & 0x0f) << 4) | ((b3 & 0x3c) >>> 2)));
  			/* b4 */
  			do {
  				b4 = data[i++];
  				if (b4 == 61)
  					return sb.toString().getBytes("iso8859-1");
  				b4 = base64DecodeChars[b4];
  			} while (i < len && b4 == -1);
  			if (b4 == -1)
  				break;
  			sb.append((char) (((b3 & 0X03) << 6) | b4));
  		}
  		return sb.toString().getBytes("iso8859-1");
  	}
  	
  	public static String decoder(String str) throws UnsupportedEncodingException{
		return new String(decode(str));
	}
    
}