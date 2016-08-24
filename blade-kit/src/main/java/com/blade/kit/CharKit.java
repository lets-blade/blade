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
package com.blade.kit;

import java.io.UnsupportedEncodingException;

/**
 * 字符相关的工具类。
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class CharKit {

    private static final String CHAR_STRING = "\u0000\u0001\u0002\u0003\u0004\u0005\u0006\u0007"
            + "\b\t\n\u000b\f\r\u000e\u000f" + "\u0010\u0011\u0012\u0013\u0014\u0015\u0016\u0017"
            + "\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f" + "\u0020\u0021\"\u0023\u0024\u0025\u0026\u0027"
            + "\u0028\u0029\u002a\u002b\u002c\u002d\u002e\u002f" + "\u0030\u0031\u0032\u0033\u0034\u0035\u0036\u0037"
            + "\u0038\u0039\u003a\u003b\u003c\u003d\u003e\u003f" + "\u0040\u0041\u0042\u0043\u0044\u0045\u0046\u0047"
            + "\u0048\u0049\u004a\u004b\u004c\u004d\u004e\u004f" + "\u0050\u0051\u0052\u0053\u0054\u0055\u0056\u0057"
            + "\u0058\u0059\u005a\u005b\\\u005d\u005e\u005f" + "\u0060\u0061\u0062\u0063\u0064\u0065\u0066\u0067"
            + "\u0068\u0069\u006a\u006b\u006c\u006d\u006e\u006f" + "\u0070\u0071\u0072\u0073\u0074\u0075\u0076\u0077"
            + "\u0078\u0079\u007a\u007b\u007c\u007d\u007e\u007f";

    private static final String[] CHAR_STRING_ARRAY = new String[128];
    private static final Character[] CHAR_ARRAY = new Character[128];

    static {
        for (int i = 127; i >= 0; i--) {
            CHAR_STRING_ARRAY[i] = CHAR_STRING.substring(i, i + 1);
            CHAR_ARRAY[i] = new Character((char) i);
        }
    }

    /**
     * 将字符转成ASCII码
     * 
     * @param ch 字符
     * @return ASCII码
     */
    public static int toAscii(char ch) {
        if (ch <= 0xFF) {
            return ch;
        }
        return 0x3F;
    }

    /**
     * 是否为“空白字符”
     * 
     * @param ch 字符
     * @return 如果是“空白字符”则返回true
     */
    public static boolean isWhitespace(char ch) {
        return ch <= ' ';
    }

    public static Character toCharacterObject(char ch) {
        if (ch < CHAR_ARRAY.length) {
            return CHAR_ARRAY[ch];
        }
        return new Character(ch);
    }

    public static Character toCharacterObject(String str) {
        if (StringKit.isEmpty(str)) {
            return null;
        }
        return toCharacterObject(str.charAt(0));
    }

    public static char toChar(byte b) {
        return (char) (b & 0xFF);
    }

    public static char toChar(Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The Character must not be null");
        }
        return ch.charValue();
    }

    public static char toChar(Character ch, char defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return ch.charValue();
    }

    // -----------------------------------------------------------------------
    public static char toChar(String str) {
        if (StringKit.isEmpty(str)) {
            throw new IllegalArgumentException("The String must not be empty");
        }
        return str.charAt(0);
    }

    public static char toChar(String str, char defaultValue) {
        if (StringKit.isEmpty(str)) {
            return defaultValue;
        }
        return str.charAt(0);
    }

    public static int toIntValue(char ch) {
        if (!isDigit(ch)) {
            throw new IllegalArgumentException("The character " + ch + " is not in the range '0' - '9'");
        }
        return ch - 48;
    }

    public static int toIntValue(char ch, int defaultValue) {
        if (!isDigit(ch)) {
            return defaultValue;
        }
        return ch - 48;
    }

    public static int toIntValue(Character ch) {
        if (ch == null) {
            throw new IllegalArgumentException("The character must not be null");
        }
        return toIntValue(ch.charValue());
    }

    public static int toIntValue(Character ch, int defaultValue) {
        if (ch == null) {
            return defaultValue;
        }
        return toIntValue(ch.charValue(), defaultValue);
    }

    public static String toString(char ch) {
        if (ch < 128) {
            return CHAR_STRING_ARRAY[ch];
        }
        return new String(new char[] { ch });
    }

    public static String toString(Character ch) {
        if (ch == null) {
            return null;
        }
        return toString(ch.charValue());
    }

    public static String unicodeEscaped(char ch) {
        if (ch < 0x10) {
            return "\\u000" + Integer.toHexString(ch);
        } else if (ch < 0x100) {
            return "\\u00" + Integer.toHexString(ch);
        } else if (ch < 0x1000) {
            return "\\u0" + Integer.toHexString(ch);
        }
        return "\\u" + Integer.toHexString(ch);
    }

    public static String unicodeEscaped(Character ch) {
        if (ch == null) {
            return null;
        }
        return unicodeEscaped(ch.charValue());
    }

    public static byte[] toSimpleByteArray(char[] carr) {
        if (carr == null) {
            return null;
        }
        byte[] barr = new byte[carr.length];
        for (int i = 0; i < carr.length; i++) {
            barr[i] = (byte) carr[i];
        }
        return barr;
    }

    /**
     * Converts char sequence into byte array.
     * 
     * @see #toSimpleByteArray(char[])
     */
    public static byte[] toSimpleByteArray(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        byte[] barr = new byte[charSequence.length()];
        for (int i = 0; i < barr.length; i++) {
            barr[i] = (byte) charSequence.charAt(i);
        }
        return barr;
    }

    public static char[] toSimpleCharArray(byte[] barr) {
        if (barr == null) {
            return null;
        }
        char[] carr = new char[barr.length];
        for (int i = 0; i < barr.length; i++) {
            carr[i] = (char) (barr[i] & 0xFF);
        }
        return carr;
    }

    public static byte[] toAsciiByteArray(char[] carr) {
        if (carr == null) {
            return null;
        }
        byte[] barr = new byte[carr.length];
        for (int i = 0; i < carr.length; i++) {
            barr[i] = (byte) ((int) (carr[i] <= 0xFF ? carr[i] : 0x3F));
        }
        return barr;
    }

    public static byte[] toAsciiByteArray(CharSequence charSequence) {
        if (charSequence == null) {
            return null;
        }
        byte[] barr = new byte[charSequence.length()];
        for (int i = 0; i < barr.length; i++) {
            char c = charSequence.charAt(i);
            barr[i] = (byte) ((int) (c <= 0xFF ? c : 0x3F));
        }
        return barr;
    }

    public static byte[] toRawByteArray(char[] carr) {
        if (carr == null) {
            return null;
        }
        byte[] barr = new byte[carr.length << 1];
        for (int i = 0, bpos = 0; i < carr.length; i++) {
            char c = carr[i];
            barr[bpos++] = (byte) ((c & 0xFF00) >> 8);
            barr[bpos++] = (byte) (c & 0x00FF);
        }
        return barr;
    }

    public static char[] toRawCharArray(byte[] barr) {
        if (barr == null) {
            return null;
        }
        int carrLen = barr.length >> 1;
        if (carrLen << 1 < barr.length) {
            carrLen++;
        }
        char[] carr = new char[carrLen];
        int i = 0, j = 0;
        while (i < barr.length) {
            char c = (char) (barr[i] << 8);
            i++;

            if (i != barr.length) {
                c += barr[i] & 0xFF;
                i++;
            }
            carr[j++] = c;
        }
        return carr;
    }

    public static byte[] toByteArray(char[] carr) throws UnsupportedEncodingException {
        if (carr == null) {
            return null;
        }
        return new String(carr).getBytes("UTF-8");
    }

    public static byte[] toByteArray(char[] carr, String charset) throws UnsupportedEncodingException {
        if (carr == null) {
            return null;
        }
        return new String(carr).getBytes(charset);
    }

    public static char[] toCharArray(byte[] barr) throws UnsupportedEncodingException {
        if (barr == null) {
            return null;
        }
        return new String(barr, "UTF-8").toCharArray();
    }

    public static char[] toCharArray(byte[] barr, String charset) throws UnsupportedEncodingException {
        if (barr == null) {
            return null;
        }
        return new String(barr, charset).toCharArray();
    }

    public static boolean equalsOne(char c, char[] match) {
        if (match == null) {
            return false;
        }
        for (char aMatch : match) {
            if (c == aMatch) {
                return true;
            }
        }
        return false;
    }

    public static int findFirstEqual(char[] source, int index, char[] match) {
        if (source == null || match == null) {
            return -1;
        }

        for (int i = index, length = source.length; i < length; i++) {
            if (equalsOne(source[i], match)) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstEqual(char[] source, int index, char match) {
        if (source == null) {
            return -1;
        }
        for (int i = index; i < source.length; i++) {
            if (source[i] == match) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstDiff(char[] source, int index, char[] match) {
        if (source == null || match == null) {
            return -1;
        }
        for (int i = index; i < source.length; i++) {
            if (!equalsOne(source[i], match)) {
                return i;
            }
        }
        return -1;
    }

    public static int findFirstDiff(char[] source, int index, char match) {
        if (source == null) {
            return -1;
        }
        for (int i = index; i < source.length; i++) {
            if (source[i] != match) {
                return i;
            }
        }
        return -1;
    }
    
    public static boolean isLowercaseLetter(char c) {
        return (c >= 'a') && (c <= 'z');
    }

    public static boolean isUppercaseLetter(char c) {
        return (c >= 'A') && (c <= 'Z');
    }

    public static boolean isLetter(char c) {
        return ((c >= 'a') && (c <= 'z')) || ((c >= 'A') && (c <= 'Z'));
    }

    public static boolean isDigit(char c) {
        return (c >= '0') && (c <= '9');
    }

    public static boolean isLetterOrDigit(char c) {
        return isDigit(c) || isLetter(c);
    }

    public static boolean isWordChar(char c) {
        return isDigit(c) || isLetter(c) || (c == '_');
    }

    public static boolean isPropertyNameChar(char c) {
        return isDigit(c) || isLetter(c) || (c == '_') || (c == '.') || (c == '[') || (c == ']');
    }

    public static boolean isAscii(char ch) {
        return ch < 128;
    }

    public static boolean isAsciiPrintable(char ch) {
        return ch >= 32 && ch < 127;
    }

    public static boolean isAsciiControl(char ch) {
        return ch < 32 || ch == 127;
    }

    public static boolean isChinese(char c) {
        return (c >= 0x4e00 && c <= 0x9fa5);
    }

    public static char toUpperAscii(char c) {
        if (isLowercaseLetter(c)) {
            c -= (char) 0x20;
        }
        return c;
    }

    public static char toLowerAscii(char c) {
        if (isUppercaseLetter(c)) {
            c += (char) 0x20;
        }
        return c;
    }

}