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
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 有关字符串处理的工具类。
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class StringKit {

    private static final String FOLDER_SEPARATOR = "/";
    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";
    private static final String TOP_PATH = "..";
    private static final String CURRENT_PATH = ".";
    public static String[] NUMBER = { "零", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百", "千", "万", "亿" };
    private static final String RANDOM_CHAR = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    
    /**
     * 检查字符串是否为<code>null</code>或空字符串<code>""</code>。
     * 
     * <pre>
     * StringUtil.isEmpty(null)      = true
     * StringUtil.isEmpty(&quot;&quot;)        = true
     * StringUtil.isEmpty(&quot; &quot;)       = false
     * StringUtil.isEmpty(&quot;bob&quot;)     = false
     * StringUtil.isEmpty(&quot;  bob  &quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果为空, 则返回<code>true</code>
     */
    public static boolean isEmpty(String str) {
    	return (str == null || str.length() == 0) ? true : false;
    }
    
    public static boolean isAnyEmpty(String...strings) {
        if (strings == null) {
            return true;
        }
        for (String string : strings) {
            if (isEmpty(string)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查字符串是否不是<code>null</code>和空字符串<code>""</code>。
     * 
     * <pre>
     * StringUtil.isEmpty(null)      = false
     * StringUtil.isEmpty(&quot;&quot;)        = false
     * StringUtil.isEmpty(&quot; &quot;)       = true
     * StringUtil.isEmpty(&quot;bob&quot;)     = true
     * StringUtil.isEmpty(&quot;  bob  &quot;) = true
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果不为空, 则返回<code>true</code>
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
    
    /**
     * 检查字符串是否是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isBlank(null)      = true
     * StringUtil.isBlank(&quot;&quot;)        = true
     * StringUtil.isBlank(&quot; &quot;)       = true
     * StringUtil.isBlank(&quot;bob&quot;)     = false
     * StringUtil.isBlank(&quot;  bob  &quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isAllBlank(String...strings) {
        if (strings == null) {
            return true;
        }
        for (String string : strings) {
            if (isNotBlank(string)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isAnyBlank(String...strings) {
        if (strings == null) {
            return true;
        }
        for (String string : strings) {
            if (isBlank(string)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查字符串是否不是空白：<code>null</code>、空字符串<code>""</code>或只有空白字符。
     * 
     * <pre>
     * StringUtil.isBlank(null)      = false
     * StringUtil.isBlank(&quot;&quot;)        = false
     * StringUtil.isBlank(&quot; &quot;)       = false
     * StringUtil.isBlank(&quot;bob&quot;)     = true
     * StringUtil.isBlank(&quot;  bob  &quot;) = true
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果为空白, 则返回<code>true</code>
     */
    public static boolean isNotBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return false;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * 如果字符串是<code>null</code>，则返回指定默认字符串，否则返回字符串本身。
     * 
     * <pre>
     * StringUtil.defaultIfNull(null, &quot;default&quot;)  = &quot;default&quot;
     * StringUtil.defaultIfNull(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * StringUtil.defaultIfNull(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * StringUtil.defaultIfNull(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     * 
     * @param str 要转换的字符串
     *
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfNull(String str) {
        return (str == null) ? "" : str;
    }
    
    /**
     * 如果字符串是<code>null</code>，则返回指定默认字符串，否则返回字符串本身。
     * 
     * <pre>
     * StringUtil.defaultIfNull(null, &quot;default&quot;)  = &quot;default&quot;
     * StringUtil.defaultIfNull(&quot;&quot;, &quot;default&quot;)    = &quot;&quot;
     * StringUtil.defaultIfNull(&quot;  &quot;, &quot;default&quot;)  = &quot;  &quot;
     * StringUtil.defaultIfNull(&quot;bat&quot;, &quot;default&quot;) = &quot;bat&quot;
     * </pre>
     * 
     * @param str 要转换的字符串
     * @param defaultStr 默认字符串
     * 
     * @return 字符串本身或指定的默认字符串
     */
    public static String defaultIfNull(String str, String defaultStr) {
        return (str == null) ? defaultStr : str;
    }
    
    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trim(null)          = null
     * StringUtil.trim(&quot;&quot;)            = &quot;&quot;
     * StringUtil.trim(&quot;     &quot;)       = &quot;&quot;
     * StringUtil.trim(&quot;abc&quot;)         = &quot;abc&quot;
     * StringUtil.trim(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(String str) {
        return trim(str, null, 0);
    }

    /**
     * Trims array of strings. <code>null</code> array elements are ignored.
     */
    public static void trimAll(String[] strings) {
        if (strings == null) {
            return;
        }
        for (int i = 0; i < strings.length; i++) {
            String string = strings[i];
            if (string != null) {
                strings[i] = trim(string);
            }
        }
    }
    
    /**
     * 除去字符串头尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.trim(null, *)          = null
     * StringUtil.trim(&quot;&quot;, *)            = &quot;&quot;
     * StringUtil.trim(&quot;abc&quot;, null)      = &quot;abc&quot;
     * StringUtil.trim(&quot;  abc&quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;abc  &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot; abc &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;  abcyx&quot;, &quot;xyz&quot;) = &quot;  abc&quot;
     * </pre>
     * 
     * @param str 要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * 
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }

    /**
     * 除去字符串头部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trimStart(null)         = null
     * StringUtil.trimStart(&quot;&quot;)           = &quot;&quot;
     * StringUtil.trimStart(&quot;abc&quot;)        = &quot;abc&quot;
     * StringUtil.trimStart(&quot;  abc&quot;)      = &quot;abc&quot;
     * StringUtil.trimStart(&quot;abc  &quot;)      = &quot;abc  &quot;
     * StringUtil.trimStart(&quot; abc &quot;)      = &quot;abc &quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimStart(String str) {
        return trim(str, null, -1);
    }

    /**
     * 除去字符串头部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.trimStart(null, *)          = null
     * StringUtil.trimStart(&quot;&quot;, *)            = &quot;&quot;
     * StringUtil.trimStart(&quot;abc&quot;, &quot;&quot;)        = &quot;abc&quot;
     * StringUtil.trimStart(&quot;abc&quot;, null)      = &quot;abc&quot;
     * StringUtil.trimStart(&quot;  abc&quot;, null)    = &quot;abc&quot;
     * StringUtil.trimStart(&quot;abc  &quot;, null)    = &quot;abc  &quot;
     * StringUtil.trimStart(&quot; abc &quot;, null)    = &quot;abc &quot;
     * StringUtil.trimStart(&quot;yxabc  &quot;, &quot;xyz&quot;) = &quot;abc  &quot;
     * </pre>
     * 
     * @param str 要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * 
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimStart(String str, String stripChars) {
        return trim(str, stripChars, -1);
    }

    /**
     * 除去字符串尾部的空白，如果字符串是<code>null</code>，则返回<code>null</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trimEnd(null)       = null
     * StringUtil.trimEnd(&quot;&quot;)         = &quot;&quot;
     * StringUtil.trimEnd(&quot;abc&quot;)      = &quot;abc&quot;
     * StringUtil.trimEnd(&quot;  abc&quot;)    = &quot;  abc&quot;
     * StringUtil.trimEnd(&quot;abc  &quot;)    = &quot;abc&quot;
     * StringUtil.trimEnd(&quot; abc &quot;)    = &quot; abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimEnd(String str) {
        return trim(str, null, 1);
    }

    /**
     * 除去字符串尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.trimEnd(null, *)          = null
     * StringUtil.trimEnd(&quot;&quot;, *)            = &quot;&quot;
     * StringUtil.trimEnd(&quot;abc&quot;, &quot;&quot;)        = &quot;abc&quot;
     * StringUtil.trimEnd(&quot;abc&quot;, null)      = &quot;abc&quot;
     * StringUtil.trimEnd(&quot;  abc&quot;, null)    = &quot;  abc&quot;
     * StringUtil.trimEnd(&quot;abc  &quot;, null)    = &quot;abc&quot;
     * StringUtil.trimEnd(&quot; abc &quot;, null)    = &quot; abc&quot;
     * StringUtil.trimEnd(&quot;  abcyx&quot;, &quot;xyz&quot;) = &quot;  abc&quot;
     * </pre>
     * 
     * @param str 要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * 
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    public static String trimEnd(String str, String stripChars) {
        return trim(str, stripChars, 1);
    }

    /**
     * 除去字符串头尾部的空白，如果结果字符串是空字符串<code>""</code>，则返回<code>null</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trimToNull(null)          = null
     * StringUtil.trimToNull(&quot;&quot;)            = null
     * StringUtil.trimToNull(&quot;     &quot;)       = null
     * StringUtil.trimToNull(&quot;abc&quot;)         = &quot;abc&quot;
     * StringUtil.trimToNull(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToNull(String str) {
        return trimToNull(str, null);
    }

    /**
     * 除去字符串头尾部的空白，如果结果字符串是空字符串<code>""</code>，则返回<code>null</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trim(null, *)          = null
     * StringUtil.trim(&quot;&quot;, *)            = null
     * StringUtil.trim(&quot;abc&quot;, null)      = &quot;abc&quot;
     * StringUtil.trim(&quot;  abc&quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;abc  &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot; abc &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;  abcyx&quot;, &quot;xyz&quot;) = &quot;  abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToNull(String str, String stripChars) {
        String result = trim(str, stripChars);

        if ((result == null) || (result.length() == 0)) {
            return null;
        }

        return result;
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，则返回空字符串<code>""</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trimToEmpty(null)          = &quot;&quot;
     * StringUtil.trimToEmpty(&quot;&quot;)            = &quot;&quot;
     * StringUtil.trimToEmpty(&quot;     &quot;)       = &quot;&quot;
     * StringUtil.trimToEmpty(&quot;abc&quot;)         = &quot;abc&quot;
     * StringUtil.trimToEmpty(&quot;    abc    &quot;) = &quot;abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToEmpty(String str) {
        return trimToEmpty(str, null);
    }

    /**
     * 除去字符串头尾部的空白，如果字符串是<code>null</code>，则返回空字符串<code>""</code>。
     * 
     * <p>
     * 注意，和<code>String.trim</code>不同，此方法使用<code>Character.isWhitespace</code> 来判定空白， 因而可以除去英文字符集之外的其它空白，如中文空格。
     * 
     * <pre>
     * StringUtil.trim(null, *)          = &quot;&quot;
     * StringUtil.trim(&quot;&quot;, *)            = &quot;&quot;
     * StringUtil.trim(&quot;abc&quot;, null)      = &quot;abc&quot;
     * StringUtil.trim(&quot;  abc&quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;abc  &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot; abc &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;  abcyx&quot;, &quot;xyz&quot;) = &quot;  abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要处理的字符串
     * 
     * @return 除去空白的字符串，如果原字串为<code>null</code>或结果字符串为<code>""</code>，则返回 <code>null</code>
     */
    public static String trimToEmpty(String str, String stripChars) {
        String result = trim(str, stripChars);

        if (result == null) {
            return "";
        }
        return result;
    }

    /**
     * 除去字符串头尾部的指定字符，如果字符串是<code>null</code>，依然返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.trim(null, *)          = null
     * StringUtil.trim(&quot;&quot;, *)            = &quot;&quot;
     * StringUtil.trim(&quot;abc&quot;, null)      = &quot;abc&quot;
     * StringUtil.trim(&quot;  abc&quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;abc  &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot; abc &quot;, null)    = &quot;abc&quot;
     * StringUtil.trim(&quot;  abcyx&quot;, &quot;xyz&quot;) = &quot;  abc&quot;
     * </pre>
     * 
     * @param str 要处理的字符串
     * @param stripChars 要除去的字符，如果为<code>null</code>表示除去空白字符
     * @param mode <code>-1</code>表示trimStart，<code>0</code>表示trim全部， <code>1</code>表示trimEnd
     * 
     * @return 除去指定字符后的的字符串，如果原字串为<code>null</code>，则返回<code>null</code>
     */
    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        // 扫描字符串头部
        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (str.startsWith(stripChars) && stripChars.indexOf(str.charAt(start)) != -1)) {
                    start++;
                }
            }
        }

        // 扫描字符串尾部
        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (str.endsWith(stripChars) && stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                    end--;
                }
            }
        }

        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }

        return str;
    }

    /*
     * ========================================================================== ==
     */
    /* 比较函数。 */
    /*                                                                              */
    /* 以下方法用来比较两个字符串是否相同。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 比较两个字符串（大小写敏感）。
     * 
     * <pre>
     * StringUtil.equals(null, null)   = true
     * StringUtil.equals(null, &quot;abc&quot;)  = false
     * StringUtil.equals(&quot;abc&quot;, null)  = false
     * StringUtil.equals(&quot;abc&quot;, &quot;abc&quot;) = true
     * StringUtil.equals(&quot;abc&quot;, &quot;ABC&quot;) = false
     * </pre>
     * 
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * 
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equals(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equals(str2);
    }

    /**
     * 比较两个字符串（大小写不敏感）。
     * 
     * <pre>
     * StringUtil.equalsIgnoreCase(null, null)   = true
     * StringUtil.equalsIgnoreCase(null, &quot;abc&quot;)  = false
     * StringUtil.equalsIgnoreCase(&quot;abc&quot;, null)  = false
     * StringUtil.equalsIgnoreCase(&quot;abc&quot;, &quot;abc&quot;) = true
     * StringUtil.equalsIgnoreCase(&quot;abc&quot;, &quot;ABC&quot;) = true
     * </pre>
     * 
     * @param str1 要比较的字符串1
     * @param str2 要比较的字符串2
     * 
     * @return 如果两个字符串相同，或者都是<code>null</code>，则返回<code>true</code>
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        if (str1 == null) {
            return str2 == null;
        }

        return str1.equalsIgnoreCase(str2);
    }

    /**
     * Compares string with at least one from the provided array. If at least one equal string is found, returns its
     * index. Otherwise, <code>-1</code> is returned.
     */
    public static int equalsOne(String src, String[] dest) {
        if (src == null || dest == null) {
            return -1;
        }

        for (int i = 0; i < dest.length; i++) {
            if (src.equals(dest[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Compares string with at least one from the provided array, ignoring case. If at least one equal string is found,
     * it returns its index. Otherwise, <code>-1</code> is returned.
     */
    public static int equalsOneIgnoreCase(String src, String[] dest) {
        if (src == null || dest == null) {
            return -1;
        }

        for (int i = 0; i < dest.length; i++) {
            if (src.equalsIgnoreCase(dest[i])) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Compares two string arrays.
     * 
     * @param as first string array
     * @param as1 second string array
     * 
     * @return true if all array elements matches
     */
    public static boolean equalsIgnoreCase(String as[], String as1[]) {
        if (as == null && as1 == null) {
            return true;
        }
        if (as == null || as1 == null) {
            return false;
        }
        if (as.length != as1.length) {
            return false;
        }
        for (int i = 0; i < as.length; i++) {
            if (!as[i].equalsIgnoreCase(as1[i])) {
                return false;
            }
        }
        return true;
    }

    /*
     * ========================================================================== ==
     */
    /* 字符串类型判定函数。 */
    /*                                                                              */
    /* 判定字符串的类型是否为：字母、数字、空白等 */
    /*
     * ========================================================================== ==
     */

    /**
     * 判断字符串是否只包含unicode字母。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isAlpha(null)   = false
     * StringUtil.isAlpha(&quot;&quot;)     = true
     * StringUtil.isAlpha(&quot;  &quot;)   = false
     * StringUtil.isAlpha(&quot;abc&quot;)  = true
     * StringUtil.isAlpha(&quot;ab2c&quot;) = false
     * StringUtil.isAlpha(&quot;ab-c&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode字母组成，则返回<code>true</code>
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode字母和空格<code>' '</code>。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isAlphaSpace(null)   = false
     * StringUtil.isAlphaSpace(&quot;&quot;)     = true
     * StringUtil.isAlphaSpace(&quot;  &quot;)   = true
     * StringUtil.isAlphaSpace(&quot;abc&quot;)  = true
     * StringUtil.isAlphaSpace(&quot;ab c&quot;) = true
     * StringUtil.isAlphaSpace(&quot;ab2c&quot;) = false
     * StringUtil.isAlphaSpace(&quot;ab-c&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode字母和空格组成，则返回<code>true</code>
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetter(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode字母和数字。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isAlphanumeric(null)   = false
     * StringUtil.isAlphanumeric(&quot;&quot;)     = true
     * StringUtil.isAlphanumeric(&quot;  &quot;)   = false
     * StringUtil.isAlphanumeric(&quot;abc&quot;)  = true
     * StringUtil.isAlphanumeric(&quot;ab c&quot;) = false
     * StringUtil.isAlphanumeric(&quot;ab2c&quot;) = true
     * StringUtil.isAlphanumeric(&quot;ab-c&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode字母数字组成，则返回<code>true</code>
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode字母数字和空格<code>' '</code>。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isAlphanumericSpace(null)   = false
     * StringUtil.isAlphanumericSpace(&quot;&quot;)     = true
     * StringUtil.isAlphanumericSpace(&quot;  &quot;)   = true
     * StringUtil.isAlphanumericSpace(&quot;abc&quot;)  = true
     * StringUtil.isAlphanumericSpace(&quot;ab c&quot;) = true
     * StringUtil.isAlphanumericSpace(&quot;ab2c&quot;) = true
     * StringUtil.isAlphanumericSpace(&quot;ab-c&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode字母数字和空格组成，则返回<code>true</code>
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isLetterOrDigit(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode数字。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isNumeric(null)   = false
     * StringUtil.isNumeric(&quot;&quot;)     = true
     * StringUtil.isNumeric(&quot;  &quot;)   = false
     * StringUtil.isNumeric(&quot;123&quot;)  = true
     * StringUtil.isNumeric(&quot;12 3&quot;) = false
     * StringUtil.isNumeric(&quot;ab2c&quot;) = false
     * StringUtil.isNumeric(&quot;12-3&quot;) = false
     * StringUtil.isNumeric(&quot;12.3&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode数字组成，则返回<code>true</code>
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode数字和空格<code>' '</code>。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isNumericSpace(null)   = false
     * StringUtil.isNumericSpace(&quot;&quot;)     = true
     * StringUtil.isNumericSpace(&quot;  &quot;)   = true
     * StringUtil.isNumericSpace(&quot;123&quot;)  = true
     * StringUtil.isNumericSpace(&quot;12 3&quot;) = true
     * StringUtil.isNumericSpace(&quot;ab2c&quot;) = false
     * StringUtil.isNumericSpace(&quot;12-3&quot;) = false
     * StringUtil.isNumericSpace(&quot;12.3&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode数字和空格组成，则返回<code>true</code>
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }

        int length = str.length();

        for (int i = 0; i < length; i++) {
            if (!Character.isDigit(str.charAt(i)) && (str.charAt(i) != ' ')) {
                return false;
            }
        }

        return true;
    }

    /**
     * 判断字符串是否只包含unicode空白。
     * 
     * <p>
     * <code>null</code>将返回<code>false</code>，空字符串<code>""</code>将返回 <code>true</code>。
     * </p>
     * 
     * <pre>
     * StringUtil.isWhitespace(null)   = false
     * StringUtil.isWhitespace(&quot;&quot;)     = true
     * StringUtil.isWhitespace(&quot;  &quot;)   = true
     * StringUtil.isWhitespace(&quot;abc&quot;)  = false
     * StringUtil.isWhitespace(&quot;ab2c&quot;) = false
     * StringUtil.isWhitespace(&quot;ab-c&quot;) = false
     * </pre>
     * 
     * @param str 要检查的字符串
     * 
     * @return 如果字符串非<code>null</code>并且全由unicode空白组成，则返回<code>true</code>
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /*
     * ========================================================================== ==
     */
    /* 大小写转换。 */
    /*
     * ========================================================================== ==
     */

    /**
     * 将字符串转换成大写。
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.toUpperCasing(null)  = null
     * StringUtil.toUpperCasing(&quot;&quot;)    = &quot;&quot;
     * StringUtil.toUpperCasing(&quot;aBc&quot;) = &quot;ABC&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要转换的字符串
     * 
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toUpperCase();
    }

    /**
     * 将字符串转换成小写。
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.toLowerCasing(null)  = null
     * StringUtil.toLowerCasing(&quot;&quot;)    = &quot;&quot;
     * StringUtil.toLowerCasing(&quot;aBc&quot;) = &quot;abc&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要转换的字符串
     * 
     * @return 大写字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase();
    }

    /**
     * 将字符串的首字符转成大写（<code>Character.toTitleCase</code>），其它字符不变。
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.capitalize(null)  = null
     * StringUtil.capitalize(&quot;&quot;)    = &quot;&quot;
     * StringUtil.capitalize(&quot;cat&quot;) = &quot;Cat&quot;
     * StringUtil.capitalize(&quot;cAt&quot;) = &quot;CAt&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要转换的字符串
     * 
     * @return 首字符为大写的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String capitalize(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }

        return new StringBuilder(strLen).append(Character.toTitleCase(str.charAt(0))).append(str.substring(1))
                .toString();
    }

    /**
     * 将字符串的首字符转成小写，其它字符不变。
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.uncapitalize(null)  = null
     * StringUtil.uncapitalize(&quot;&quot;)    = &quot;&quot;
     * StringUtil.uncapitalize(&quot;Cat&quot;) = &quot;cat&quot;
     * StringUtil.uncapitalize(&quot;CAT&quot;) = &quot;cAT&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要转换的字符串
     * 
     * @return 首字符为小写的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String uncapitalize(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }

        return new StringBuilder(strLen).append(Character.toLowerCase(str.charAt(0))).append(str.substring(1))
                .toString();
    }

    /**
     * 与 {@link #uncapitalize(String)}不同，连续大写字符将不做改变
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.uncapitalize(null)  = null
     * StringUtil.uncapitalize(&quot;&quot;)    = &quot;&quot;
     * StringUtil.uncapitalize(&quot;Cat&quot;) = &quot;cat&quot;
     * StringUtil.uncapitalize(&quot;CAT&quot;) = &quot;CAT&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要转换的字符串
     * 
     * @return 首字符为小写的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String decapitalize(String str) {
        if ((str == null) || str.length() == 0) {
            return str;
        }
        if (str.length() > 1 && Character.isUpperCase(str.charAt(1)) && Character.isUpperCase(str.charAt(0))) {
            return str;
        }

        char chars[] = str.toCharArray();
        char c = chars[0];
        char modifiedChar = Character.toLowerCase(c);
        if (modifiedChar == c) {
            return str;
        }
        chars[0] = modifiedChar;
        return new String(chars);
    }

    /**
     * 反转字符串的大小写。
     * 
     * <p>
     * 如果字符串是<code>null</code>则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.swapCasing(null)                 = null
     * StringUtil.swapCasing(&quot;&quot;)                   = &quot;&quot;
     * StringUtil.swapCasing(&quot;The dog has a BONE&quot;) = &quot;tHE DOG HAS A bone&quot;
     * </pre>
     * 
     * </p>
     * 
     * @param str 要转换的字符串
     * 
     * @return 大小写被反转的字符串，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String swapCase(String str) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }

        StringBuilder builder = new StringBuilder(strLen);

        char ch = 0;

        for (int i = 0; i < strLen; i++) {
            ch = str.charAt(i);

            if (Character.isUpperCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                ch = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                ch = Character.toUpperCase(ch);
            }

            builder.append(ch);
        }

        return builder.toString();
    }

    public static String fromCamelCase(String str, char separator) {
        int strLen;

        if ((str == null) || ((strLen = str.length()) == 0)) {
            return str;
        }
        StringBuilder result = new StringBuilder(strLen * 2);
        int resultLength = 0;
        boolean prevTranslated = false;
        for (int i = 0; i < strLen; i++) {
            char c = str.charAt(i);
            if (i > 0 || c != separator) {// skip first starting separator
                if (Character.isUpperCase(c)) {
                    if (!prevTranslated && resultLength > 0 && result.charAt(resultLength - 1) != separator) {
                        result.append(separator);
                        resultLength++;
                    }
                    c = Character.toLowerCase(c);
                    prevTranslated = true;
                } else {
                    prevTranslated = false;
                }
                result.append(c);
                resultLength++;
            }
        }
        return resultLength > 0 ? result.toString() : str;
    }
    
    /**
	 * String2Short
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static short toShort(String s, short def) {
		try {
			return (isEmpty(s)) ? def : Short.parseShort(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	/**
	 * String2Int
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static int toInt(String s, int def) {
		try {
			return (isEmpty(s)) ? def : Integer.parseInt(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}
	
	/**
	 * String2Long
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static long toLong(String s, long def) {
		try {
			return (isEmpty(s)) ? def : Long.parseLong(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * String2Float
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static float toFloat(String s, float def) {
		try {
			return (isEmpty(s)) ? def : Float.parseFloat(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * String2Double
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static double toDouble(String s, double def) {
		try {
			return (isEmpty(s)) ? def : Double.parseDouble(s);
		} catch (NumberFormatException e) {
			return def;
		}
	}

	/**
	 * String2Boolean
	 * 
	 * @param s
	 * @param def
	 * @return
	 */
	public static boolean toBoolean(String s, boolean def) {
		if (isEmpty(s))
			return def;
		else {
			return "true".equalsIgnoreCase(s);
		}
	}
	
	// ----------- 字符串截取 ----------- //
	/**
	 * 截取 float/double 类型 一位小数
	 * 
	 * @param f
	 * @return
	 */
	public static String getDelFormat(Object f) {
		DecimalFormat df = new DecimalFormat("0.0");
		return df.format(f);
	}

	/**
	 * 截取 float/double 类型 两位小数
	 * 
	 * @param f
	 * @return
	 */
	public static String getDelFormat2(Object f) {
		DecimalFormat df = new DecimalFormat("0.00");
		return df.format(f);
	}

	/**
	 * 截取三位
	 * 
	 * @param f
	 * @return
	 */
	public static String getDelFormat3(Object f) {
		DecimalFormat df = new DecimalFormat("0.000");
		return df.format(f);
	}

	/**
	 * 截取字符串 先是一定的长多 多余...
	 * 
	 * @param source
	 *            元字符串
	 * @param len
	 *            显示长多
	 * @return
	 */
	public static String getStringsubstr(String source, int len) {
		if (null == source || "".equals(source)) {
			return "";
		}
		if (source.length() > len) {
			return source.substring(0, len) + "...";
		}

		return source;
	}
	
	// ----------- 随机数 ----------- //
	/**
	 * 获得0-max的随机数
	 * 
	 * @param length
	 * @return String
	 */
	public static String getRandomNumber(int length, int max) {
		Random random = new Random();
		StringBuffer buffer = new StringBuffer();

		for (int i = 0; i < length; i++) {
			buffer.append(random.nextInt(max));
		}
		return buffer.toString();
	}

	/**
	 * 获取指定长度的随机数字组成的字符串
	 * 
	 * @param size
	 * @return
	 */
	public static String getRandomNumber(int size) {
		String num = "";
		for (int i = 0; i < size; i++) {
			double a = Math.random() * 9;
			a = Math.ceil(a);
			int randomNum = new Double(a).intValue();
			num += randomNum;
		}
		return num;
	}

	/**
	 * 获取随机字符
	 * 
	 * @param size
	 * @return
	 */
	public static String getRandomChar(int size) {
		String sRand = "";
		Random random = new Random();// 创建一个随机类
		for (int i = 0; i < size; i++) {
			String ch = String.valueOf(RANDOM_CHAR.charAt(random.nextInt(RANDOM_CHAR.length())));
			sRand += ch;
		}
		return sRand;
	}
	
	// ----------- 验证 ----------- //
	/**
	 * 判断字符串是否为数字和有正确的值
	 * 
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {
		// Pattern pattern=Pattern.compile("[0-9]*");
		// return pattern.matcher(str).matches();
		if (null != str && 0 != str.trim().length() && str.matches("\\d*")) {
			return true;
		}

		return false;
	}
	
	public static boolean isBoolean(String value) {
		if(null != value){
			String val = value.toLowerCase();
			if(val.equals("true") || val.equals("false")){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 将阿拉伯数字转为中文数字
	 * 
	 * @return
	 */
	public static String toChineseNumber(int number, int depth) {
		if (depth < 0)
			depth = 0;
		if (number <= 0 && depth == 0)
			return NUMBER[0];

		String chinese = "";
		String src = number + "";
		if (src.charAt(src.length() - 1) == 'l' || src.charAt(src.length() - 1) == 'L') {
			src = src.substring(0, src.length() - 1);
		}

		if (src.length() > 4)
			chinese = toChineseNumber(Integer.parseInt(src.substring(0, src.length() - 4)), depth + 1)
					+ toChineseNumber(Integer.parseInt(src.substring(src.length() - 4, src.length())), depth - 1);
		else {
			char prv = 0;
			for (int i = 0; i < src.length(); i++) {
				switch (src.charAt(i)) {
				case '0':
					if (prv != '0')
						chinese = chinese + NUMBER[0];
					break;
				case '1':
					chinese = chinese + NUMBER[1];
					break;
				case '2':
					chinese = chinese + NUMBER[2];
					break;
				case '3':
					chinese = chinese + NUMBER[3];
					break;
				case '4':
					chinese = chinese + NUMBER[4];
					break;
				case '5':
					chinese = chinese + NUMBER[5];
					break;
				case '6':
					chinese = chinese + NUMBER[6];
					break;
				case '7':
					chinese = chinese + NUMBER[7];
					break;
				case '8':
					chinese = chinese + NUMBER[8];
					break;
				case '9':
					chinese = chinese + NUMBER[9];
					break;
				}
				prv = src.charAt(i);

				switch (src.length() - 1 - i) {
				case 1:// 十
					if (prv != '0')
						chinese = chinese + NUMBER[10];
					break;
				case 2:// 百
					if (prv != '0')
						chinese = chinese + NUMBER[11];
					break;
				case 3:// 千
					if (prv != '0')
						chinese = chinese + NUMBER[12];
					break;
				}
			}
		}
		while (chinese.length() > 0 && chinese.lastIndexOf(NUMBER[0]) == chinese.length() - 1)
			chinese = chinese.substring(0, chinese.length() - 1);
		if (depth == 1)
			chinese += NUMBER[13];
		if (depth == 2)
			chinese += NUMBER[14];
		return chinese;
	}

	/**
	 * 验证字符串是否含有特殊字符和中文
	 * 
	 * @param
	 * 
	 * @return
	 */
	public static boolean checkIsEnglish(String s) {
		String Letters = "(){}[]\",.<>\\/~!@#$%^&*;': ";
		int i;
		int c;

		if (s.charAt(0) == '-') {
			return false;
		}
		if (s.charAt(s.length() - 1) == '-') {
			return false;
		}

		for (i = 0; i < s.length(); i++) {
			c = s.charAt(i);
			if (Letters.indexOf(c) > -1) {
				return false;
			}
		}

		// 验证是否刚好匹配
		boolean yesorno = isChineseStr(s);
		if (yesorno) {
			return false;
		}
		return true;
	}

	public static boolean isChineseStr(String pValue) {
		for (int i = 0; i < pValue.length(); i++) {
			if ((int) pValue.charAt(i) > 256)
				return true;
		}
		return false;
	}

	/**
	 * 判断一个字符是Ascill字符还是其它字符（如汉，日，韩文字符）
	 * 
	 * @param c, 需要判断的字符
	 * @return boolean, 返回true,Ascill字符
	 */
	public static boolean isLetter(char c) {
		int k = 0x80;
		return c / k == 0 ? true : false;
	}
	
	
	// ----------- 格式化 ---------- //
	/**
	 * 格式化数据
	 * 
	 * @param decimal
	 *            l3453454
	 * @return 3,453,454
	 */
	public final static String FormatDecimalString(String decimal) {
		double dValue = Double.valueOf(decimal).doubleValue();
		DecimalFormat df = new DecimalFormat();
		String positivePattern = " ,000";
		String negativePattern = " ,000";
		if (dValue < 0) {
			df.applyPattern(negativePattern);
			return df.format(dValue).replace(',', ',');
		} else {
			df.applyPattern(positivePattern);
			return df.format(dValue).replace(',', ',');
		}
	}

	/**
	 * 格式化数据
	 * 
	 * @param source
	 *            3453454
	 * @return 3,453,454
	 */
	public static String getNumberFormat(long source) {
		NumberFormat usFormat = NumberFormat.getIntegerInstance(Locale.US);
		return usFormat.format(source);
	}

	// ----------- 过滤 ---------- //
	/**
	 * 过滤字符串里的的特殊字符
	 * 
	 * @param str
	 *            要过滤的字符串
	 * @return 过滤后的字符串
	 */
	public static String stringFilter(String str) {
		// 过滤通过页面表单提交的字符
		String[][] FilterChars = { { "<", "&lt;" }, { ">", "&gt;" }, { " ", "&nbsp;" }, { "\"", "&quot;" }, { "&", "&amp;" },
				{ "/", "&#47;" }, { "\\", "&#92;" }, { "'", "\\'" }, { "%", "%" } };

		String[] str_arr = stringSpilit(str, "");

		for (int i = 0; i < str_arr.length; i++) {
			for (int j = 0; j < FilterChars.length; j++) {
				if (FilterChars[j][0].equals(str_arr[i]))
					str_arr[i] = FilterChars[j][1];
			}
		}
		return (stringConnect(str_arr, "")).trim();
	}

	// 关健字过滤
	public static String stringKeyWorldFilter(String str) {
		// 过滤通过页面表单提交的字符
		String[][] FilterChars = { { "<", "" }, { ">", "" }, { "\"", "" }, { "&", "" }, { "/", "" }, { "\\", "" }, { "'", "" },
				{ "%", "" } };

		String[] str_arr = stringSpilit(str, "");

		for (int i = 0; i < str_arr.length; i++) {
			for (int j = 0; j < FilterChars.length; j++) {
				if (FilterChars[j][0].equals(str_arr[i]))
					str_arr[i] = FilterChars[j][1];
			}
		}
		return (stringConnect(str_arr, "")).trim();
	}

	public static String escapeJavaScript(String value) {
        return escapeJava(value);
    }

    public static String unescapeJavaScript(String value) {
        return unescapeJava(value);
    }
    
    public static String escapeJava(String value) {
        if (value == null || value.length() == 0) {
            return value;
        }
        int len = value.length();
        StringBuilder buf = null;
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            String rep;
            switch (ch) {
            case '\\':
                rep = "\\\\";
                break;
            case '\"':
                rep = "\\\"";
                break;
            case '\'':
                rep = "\\\'";
                break;
            case '\t':
                rep = "\\t";
                break;
            case '\n':
                rep = "\\n";
                break;
            case '\r':
                rep = "\\r";
                break;
            case '\b':
                rep = "\\b";
                break;
            case '\f':
                rep = "\\f";
                break;
            default:
                rep = null;
                break;
            }
            if (rep != null) {
                if (buf == null) {
                    buf = new StringBuilder(len * 2);
                    if (i > 0) {
                        buf.append(value.substring(0, i));
                    }
                }
                buf.append(rep);
            } else {
                if (buf != null) {
                    buf.append(ch);
                }
            }
        }
        if (buf != null) {
            return buf.toString();
        }
        return value;
    }
    
    public static String unescapeJava(String value) {
        if (value == null || value.length() == 0) {
            return value;
        }
        StringBuilder buf = null;
        int len = value.length();
        int len1 = len - 1;
        for (int i = 0; i < len; i++) {
            char ch = value.charAt(i);
            if (ch == '\\' && i < len1) {
                int j = i;
                i++;
                ch = value.charAt(i);
                switch (ch) {
                case '\\':
                    ch = '\\';
                    break;
                case '\"':
                    ch = '\"';
                    break;
                case '\'':
                    ch = '\'';
                    break;
                case 't':
                    ch = '\t';
                    break;
                case 'n':
                    ch = '\n';
                    break;
                case 'r':
                    ch = '\r';
                    break;
                case 'b':
                    ch = '\b';
                    break;
                case 'f':
                    ch = '\f';
                    break;
                case 'u':
                    ch = (char) Integer.parseInt(value.substring(i + 1, i + 5), 16);
                    i = i + 4;
                    break;
                default:
                    j--;
                }
                if (buf == null) {
                    buf = new StringBuilder(len);
                    if (j > 0) {
                        buf.append(value.substring(0, j));
                    }
                }
                buf.append(ch);
            } else if (buf != null) {
                buf.append(ch);
            }
        }
        if (buf != null) {
            return buf.toString();
        }
        return value;
    }
    
	// ----------- 切割合并 ---------- //
	/**
	 * 分割字符串
	 * 
	 * @param str
	 *            要分割的字符串
	 * @param spilit_sign
	 *            字符串的分割标志
	 * @return 分割后得到的字符串数组
	 */
	public static String[] stringSpilit(String str, String spilit_sign) {
		String[] spilit_string = str.split(spilit_sign);
		if (spilit_string[0].equals("")) {
			String[] new_string = new String[spilit_string.length - 1];
			for (int i = 1; i < spilit_string.length; i++)
				new_string[i - 1] = spilit_string[i];
			return new_string;
		} else
			return spilit_string;
	}

	/**
	 * 用特殊的字符连接字符串
	 * 
	 * @param strings
	 *            要连接的字符串数组
	 * @param spilit_sign
	 *            连接字符
	 * @return 连接字符串
	 */
	public static String stringConnect(String[] strings, String spilit_sign) {
		StringBuffer str = new StringBuffer("");
		for (int i = 0; i < strings.length; i++) {
			str.append(strings[i]).append(spilit_sign);
		}
		return str.toString();
	}

	/**
	 * 四舍五入 返回int类型
	 * 
	 * @param dSource
	 *            2342.45
	 * @return 2342
	 */
	public static int getRound(double dSource) {
		int iRound;
		// BigDecimal的构造函数参数类型是double
		BigDecimal deSource = new BigDecimal(dSource);
		// deSource.setScale(0,BigDecimal.ROUND_HALF_UP) 返回值类型 BigDecimal
		// intValue() 方法将BigDecimal转化为int
		iRound = deSource.setScale(0, BigDecimal.ROUND_HALF_UP).intValue();
		return iRound;
	}

	/**
	 * 提供小数位四舍五入处理。
	 * 
	 * @param s
	 *            需要四舍五入的数字
	 * @return 四舍五入后的结果
	 */
	public static double round(double s) {
		BigDecimal b = new BigDecimal(Double.toString(s));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供小数位四舍五入处理。
	 * 
	 * @param s
	 *            需要四舍五入的数字
	 * @return 四舍五入后的结果
	 */
	public static long roundlong(double s) {
		BigDecimal b = new BigDecimal(Double.toString(s));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, BigDecimal.ROUND_HALF_UP).longValue();
	}

	/**
	 * 得到一个字符串的长度,显示的长度,一个汉字或日韩文长度为2,英文字符长度为1
	 * 
	 * @param s ,需要得到长度的字符串
	 * @return int, 得到的字符串长度
	 */
	public static int length(String s) {
		if (s == null)
			return 0;
		char[] c = s.toCharArray();
		int len = 0;
		for (int i = 0; i < c.length; i++) {
			len++;
			if (!isLetter(c[i])) {
				len++;
			}
		}
		return len;
	}

	/**
	 * 功能：获得配置文件中指定编码字符串
	 * 
	 * @param str
	 *            解码字符串 charset 指定编码
	 * 
	 */
	public static String getStrByCharset(String str, String charset) throws UnsupportedEncodingException {
		return new String(str.getBytes("ISO-8859-1"), charset);
	}

	/**
	 * 提取字符串中的中文字符
	 * 
	 * @param str
	 * @return
	 */
	public static String getChineseByStr(String str) {

		StringBuilder resultStr = new StringBuilder("");

		Pattern pcn = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = pcn.matcher(str);
		while (m.find()) {
			resultStr.append(m.group().toString());
		}

		return resultStr.toString();
	}

	// 将两位小数的字符串*100
	public static String parseStrInt(String strDouble) {
		String fen = "";
		int dotIndex = strDouble.lastIndexOf(".");
		if (dotIndex >= 0) {// 有小数点字符串
			String zs = strDouble.substring(0, dotIndex);
			if (!isNumber(zs)) {
				return null;
			}
			fen = zs;
			if (strDouble.substring(dotIndex).length() > 1) {// 有小数部分
				String xs = strDouble.substring(dotIndex).substring(1);
				if (!isNumber(xs)) {
					return null;
				}
				if (xs.length() >= 3) {// 截取后面部分
					xs = xs.substring(0, 2);
				} else {
					if (xs.length() < 2) {
						xs = xs + "0";
					}
				}
				fen = zs + xs;
			} else {// 没有小数
				return null;
			}

		} else {// 无小数点字符串
			fen = strDouble + "00";
		}
		return fen;
	}

	/**
	 * 检查浮点数
	 * 
	 * @param num
	 * @param type
	 *            "0+":非负浮点数 "+":正浮点数 "-0":非正浮点数 "-":负浮点数 "":浮点数
	 * @return
	 */
	public static boolean checkFloat(String num, String type) {
		String eL = "";
		if (type.equals("0+"))
			eL = "^\\d+(\\.\\d+)?$";// 非负浮点数
		else if (type.equals("+"))
			eL = "^((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*))$";// 正浮点数
		else if (type.equals("-0"))
			eL = "^((-\\d+(\\.\\d+)?)|(0+(\\.0+)?))$";// 非正浮点数
		else if (type.equals("-"))
			eL = "^(-((\\d+\\.\\d*[1-9]\\d*)|(\\d*[1-9]\\d*\\.\\d+)|(\\d*[1-9]\\d*)))$";// 负浮点数
		else
			eL = "^(-?\\d+)(\\.\\d+)?$";// 浮点数
		Pattern p = Pattern.compile(eL);
		Matcher m = p.matcher(num);
		boolean b = m.matches();
		return b;
	}
	
	/**
	 * 替换字符串
	 * 
	 * @param inString
	 * @param oldPattern
	 * @param newPattern
	 * @return
	 */
    public static String replace(String inString, String oldPattern, String newPattern) {
        if (isEmpty(inString) || isEmpty(oldPattern) || newPattern == null) {
            return inString;
        }
        StringBuilder sb = new StringBuilder();
        int pos = 0; // our position in the old string
        int index = inString.indexOf(oldPattern);
        // the index of an occurrence we've found, or -1
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString.substring(pos, index));
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }
        sb.append(inString.substring(pos));
        // remember to append any characters to the right of a match
        return sb.toString();
    }

    public static String deleteAny(String inString, String charsToDelete) {
        if (isEmpty(inString) || isEmpty(charsToDelete)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inString.length(); i++) {
            char c = inString.charAt(i);
            if (charsToDelete.indexOf(c) == -1) {
                sb.append(c);
            }
        }
        return sb.toString();
    }
    
    /**
     * 获取文件名称
     * 
     * @param path
     * @return
     */
    public static String getFilename(String path) {
        if (path == null) {
            return null;
        }
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        return (separatorIndex != -1 ? path.substring(separatorIndex + 1) : path);
    }

    public static String applyRelativePath(String path, String relativePath) {
        int separatorIndex = path.lastIndexOf(FOLDER_SEPARATOR);
        if (separatorIndex != -1) {
            String newPath = path.substring(0, separatorIndex);
            if (!relativePath.startsWith(FOLDER_SEPARATOR)) {
                newPath += FOLDER_SEPARATOR;
            }
            return newPath + relativePath;
        } else {
            return relativePath;
        }
    }
    
    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        String pathToUse = replace(path, WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);

        // Strip prefix from path to analyze, to not treat it as part of the
        // first path element. This is necessary to correctly parse paths like
        // "file:core/../core/io/Resource.class", where the ".." should just
        // strip the first "core" directory while keeping the "file:" prefix.
        int prefixIndex = pathToUse.indexOf(":");
        String prefix = "";
        if (prefixIndex != -1) {
            prefix = pathToUse.substring(0, prefixIndex + 1);
            pathToUse = pathToUse.substring(prefixIndex + 1);
        }
        if (pathToUse.startsWith(FOLDER_SEPARATOR)) {
            prefix = prefix + FOLDER_SEPARATOR;
            pathToUse = pathToUse.substring(1);
        }

        String[] pathArray = delimitedListToStringArray(pathToUse, FOLDER_SEPARATOR);
        List<String> pathElements = new LinkedList<String>();
        int tops = 0;

        for (int i = pathArray.length - 1; i >= 0; i--) {
            String element = pathArray[i];
            if (CURRENT_PATH.equals(element)) {
                // Points to current directory - drop it.
            } else if (TOP_PATH.equals(element)) {
                // Registering top path found.
                tops++;
            } else {
                if (tops > 0) {
                    // Merging path element with element corresponding to top path.
                    tops--;
                } else {
                    // Normal path element found.
                    pathElements.add(0, element);
                }
            }
        }

        // Remaining top paths need to be retained.
        for (int i = 0; i < tops; i++) {
            pathElements.add(0, TOP_PATH);
        }

        return prefix + collectionToDelimitedString(pathElements, FOLDER_SEPARATOR);
    }

    public static String[] toStringArray(Collection<String> collection) {
        if (collection == null) {
            return null;
        }
        return collection.toArray(new String[collection.size()]);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter) {
        return delimitedListToStringArray(str, delimiter, null);
    }

    public static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
        if (str == null) {
            return new String[0];
        }
        if (delimiter == null) {
            return new String[] {str};
        }
        List<String> result = new ArrayList<String>();
        if ("".equals(delimiter)) {
            for (int i = 0; i < str.length(); i++) {
                result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
            }
        } else {
            int pos = 0;
            int delPos;
            while ((delPos = str.indexOf(delimiter, pos)) != -1) {
                result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
                pos = delPos + delimiter.length();
            }
            if (str.length() > 0 && pos <= str.length()) {
                // Add rest of String, but not in case of empty input.
                result.add(deleteAny(str.substring(pos), charsToDelete));
            }
        }
        return toStringArray(result);
    }

    public static String collectionToDelimitedString(Collection<?> coll, String delim, String prefix, String suffix) {
        if (CollectionKit.isEmpty(coll)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Iterator<?> it = coll.iterator();
        while (it.hasNext()) {
            sb.append(prefix).append(it.next()).append(suffix);
            if (it.hasNext()) {
                sb.append(delim);
            }
        }
        return sb.toString();
    }

    public static String collectionToDelimitedString(Collection<?> coll, String delim) {
        return collectionToDelimitedString(coll, delim, "", "");
    }

    public static String firstUpperCase(String str) {
		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
    
    public static String join(final String[] array, final String separator) {
        if (array == null) {
            return null;
        }
        final int noOfItems = array.length;
        if (noOfItems <= 0) {
            return null;
        }
        if (noOfItems == 1) {
            return array[0].toString();
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = 0; i < noOfItems; i++) {
            buf.append(separator);
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static String join(final Object[] array, final String separator) {
        if (array == null) {
            return null;
        }
        final int noOfItems = array.length;
        if (noOfItems <= 0) {
            return null;
        }
        if (noOfItems == 1) {
            return array[0].toString();
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = 0; i < noOfItems; i++) {
            buf.append(separator);
            if (array[i] != null) {
                buf.append(array[i]);
            }
        }
        return buf.toString();
    }

    public static <T> String join(final List<T> array, final String separator) {
        if (array == null) {
            return null;
        }
        final int noOfItems = array.size();
        if (noOfItems <= 0) {
            return null;
        }
        if (noOfItems == 1) {
            return array.get(0).toString();
        }
        final StringBuilder buf = new StringBuilder(noOfItems * 16);
        for (int i = 0; i < noOfItems; i++) {
            buf.append(separator);
            if (array.get(i) != null) {
                buf.append(array.get(i));
            }
        }
        return buf.toString();
    }
    
    public static String join(String... parts) {
        StringBuilder sb = new StringBuilder(parts.length);
        for (String part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }

    public static String join(Iterable<?> elements, String separator) {
        if (elements == null) {
            return "";
        }
        return join(elements.iterator(), separator);
    }

    public static String join(Iterator<?> elements, String separator) {
        if (elements == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        while (elements.hasNext()) {
            Object o = elements.next();
            if (sb.length() > 0 && separator != null) {
                sb.append(separator);
            }
            sb.append(o);
        }
        return sb.toString();
    }
    
    /**
	 * 随机获取UUID字符串(无中划线)
	 * 
	 * @return UUID字符串
	 */
	public static String getUUID() {
		String uuid = UUID.randomUUID().toString();
		return uuid.substring(0, 8) + uuid.substring(9, 13) + uuid.substring(14, 18) + uuid.substring(19, 23)
				+ uuid.substring(24);
	}

	/**
	 * 随机获取字符串
	 * 
	 * @param length	随机字符串长度
	 * @return 			随机字符串
	 */
	public static String random(int length) {
		if (length <= 0) {
			return "";
		}
		char[] randomChar = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p',
				'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', 'z', 'x', 'c', 'v', 'b', 'n', 'm' };
		Random random = new Random();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			stringBuffer.append(randomChar[Math.abs(random.nextInt()) % randomChar.length]);
		}
		return stringBuffer.toString();
	}

	/**
	 * 根据指定长度 分隔字符串
	 * 
	 * @param str		需要处理的字符串
	 * @param length	分隔长度
	 * @return 			字符串集合
	 */
	public static List<String> split(String str, int length) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < str.length(); i += length) {
			int endIndex = i + length;
			if (endIndex <= str.length()) {
				list.add(str.substring(i, i + length));
			} else {
				list.add(str.substring(i, str.length() - 1));
			}
		}
		return list;
	}

	/**
     * 将字符串按空白字符分割。
     * 
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null)       = null
     * StringUtil.split(&quot;&quot;)         = []
     * StringUtil.split(&quot;abc def&quot;)  = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot;abc  def&quot;) = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot; abc &quot;)    = [&quot;abc&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * 
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * 将String to long list
     * 
     * @param source
     * @param token
     * @return
     */
    public static List<Long> parseStringToLongList(String source, String token) {

        if (isBlank(source) || isEmpty(token)) {
            return null;
        }

        List<Long> result = new ArrayList<Long>();
        String[] units = source.split(token);
        for (String unit : units) {
            result.add(Long.valueOf(unit));
        }

        return result;
    }

    /**
     * Splits a string in several parts (tokens) that are separated by delimiter. Delimiter is <b>always</b> surrounded
     * by two strings! If there is no content between two delimiters, empty string will be returned for that token.
     * Therefore, the length of the returned array will always be: #delimiters + 1.
     * <p>
     * Method is much, much faster then regexp <code>String.split()</code>, and a bit faster then
     * <code>StringTokenizer</code>.
     * 
     * @param src string to split
     * @param delimiter split delimiter
     * 
     * @return array of split strings
     */
    public static String[] splitNoCompress(String src, String delimiter) {
        if (src == null || delimiter == null) {
            return null;
        }
        int maxparts = (src.length() / delimiter.length()) + 2; // one more for
                                                                // the last
        int[] positions = new int[maxparts];
        int dellen = delimiter.length();

        int i, j = 0;
        int count = 0;
        positions[0] = -dellen;
        while ((i = src.indexOf(delimiter, j)) != -1) {
            count++;
            positions[count] = i;
            j = i + dellen;
        }
        count++;
        positions[count] = src.length();

        String[] result = new String[count];

        for (i = 0; i < count; i++) {
            result[i] = src.substring(positions[i] + dellen, positions[i + 1]);
        }
        return result;
    }

    public static String[] splitc(String src, String d) {
        if (isAnyEmpty(src, d)) {
            return new String[] { src };
        }
        char[] delimiters = d.toCharArray();
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (CharKit.equalsOne(srcc[0], delimiters)) { // string starts with
                                                       // delimiter
            end[0] = 0;
            count++;
            s = CharKit.findFirstDiff(srcc, 1, delimiters);
            if (s == -1) { // nothing after delimiters
                return new String[] { "", "" };
            }
            start[1] = s; // new start
        }
        while (true) {
            // find new end
            e = CharKit.findFirstEqual(srcc, s, delimiters);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;
            // find new start
            count++;
            s = CharKit.findFirstDiff(srcc, e, delimiters);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    public static String[] splitc(String src, char delimiter) {
        if (isEmpty(src)) {
            return new String[] { "" };
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (srcc[0] == delimiter) { // string starts with delimiter
            end[0] = 0;
            count++;
            s = CharKit.findFirstDiff(srcc, 1, delimiter);
            if (s == -1) { // nothing after delimiters
                return new String[] { "", "" };
            }
            start[1] = s; // new start
        }
        while (true) {
            // find new end
            e = CharKit.findFirstEqual(srcc, s, delimiter);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;
            // find new start
            count++;
            s = CharKit.findFirstDiff(srcc, e, delimiter);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    public static String[] splitc(String src, char[] delimiters) {
        if (isEmpty(src) || null == delimiters || delimiters.length == 0) {
            return new String[] { src };
        }
        char[] srcc = src.toCharArray();

        int maxparts = srcc.length + 1;
        int[] start = new int[maxparts];
        int[] end = new int[maxparts];

        int count = 0;

        start[0] = 0;
        int s = 0, e;
        if (CharKit.equalsOne(srcc[0], delimiters) == true) { // string start
                                                               // with
                                                               // delimiter
            end[0] = 0;
            count++;
            s = CharKit.findFirstDiff(srcc, 1, delimiters);
            if (s == -1) { // nothing after delimiters
                return new String[] { "", "" };
            }
            start[1] = s; // new start
        }
        while (true) {
            // find new end
            e = CharKit.findFirstEqual(srcc, s, delimiters);
            if (e == -1) {
                end[count] = srcc.length;
                break;
            }
            end[count] = e;

            // find new start
            count++;
            s = CharKit.findFirstDiff(srcc, e, delimiters);
            if (s == -1) {
                start[count] = end[count] = srcc.length;
                break;
            }
            start[count] = s;
        }
        count++;
        String[] result = new String[count];
        for (int i = 0; i < count; i++) {
            result[i] = src.substring(start[i], end[i]);
        }
        return result;
    }

    /**
     * 将字符串按指定字符分割。
     * 
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null, *)         = null
     * StringUtil.split(&quot;&quot;, *)           = []
     * StringUtil.split(&quot;a.b.c&quot;, '.')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtil.split(&quot;a..b.c&quot;, '.')   = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * StringUtil.split(&quot;a:b:c&quot;, '.')    = [&quot;a:b:c&quot;]
     * StringUtil.split(&quot;a b c&quot;, ' ')    = [&quot;a&quot;, &quot;b&quot;, &quot;c&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @param separatorChar 分隔符
     * 
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, char separatorChar) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return Emptys.EMPTY_STRING_ARRAY;
        }

        List<String> list = CollectionKit.createArrayList();
        int i = 0;
        int start = 0;
        boolean match = false;

        while (i < length) {
            if (str.charAt(i) == separatorChar) {
                if (match) {
                    list.add(str.substring(start, i));
                    match = false;
                }

                start = ++i;
                continue;
            }

            match = true;
            i++;
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[list.size()]);
    }

    /**
     * 将字符串按指定字符分割。
     * 
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null, *)                = null
     * StringUtil.split(&quot;&quot;, *)                  = []
     * StringUtil.split(&quot;abc def&quot;, null)        = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot;abc def&quot;, &quot; &quot;)         = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot;abc  def&quot;, &quot; &quot;)        = [&quot;abc&quot;, &quot;def&quot;]
     * StringUtil.split(&quot; ab:  cd::ef  &quot;, &quot;:&quot;)  = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;abc.def&quot;, &quot;&quot;)          = [&quot;abc.def&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @param separatorChars 分隔符
     * 
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars) {
        return split(str, separatorChars, -1);
    }

    /**
     * 将字符串按指定字符分割。
     * 
     * <p>
     * 分隔符不会出现在目标数组中，连续的分隔符就被看作一个。如果字符串为<code>null</code>，则返回<code>null</code>。
     * 
     * <pre>
     * StringUtil.split(null, *, *)                 = null
     * StringUtil.split(&quot;&quot;, *, *)                   = []
     * StringUtil.split(&quot;ab cd ef&quot;, null, 0)        = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;  ab   cd ef  &quot;, null, 0)  = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;ab:cd::ef&quot;, &quot;:&quot;, 0)        = [&quot;ab&quot;, &quot;cd&quot;, &quot;ef&quot;]
     * StringUtil.split(&quot;ab:cd:ef&quot;, &quot;:&quot;, 2)         = [&quot;ab&quot;, &quot;cdef&quot;]
     * StringUtil.split(&quot;abc.def&quot;, &quot;&quot;, 2)           = [&quot;abc.def&quot;]
     * </pre>
     * 
     * </p>
     * 
     * @param str 要分割的字符串
     * @param separatorChars 分隔符
     * @param max 返回的数组的最大个数，如果小于等于0，则表示无限制
     * 
     * @return 分割后的字符串数组，如果原字符串为<code>null</code>，则返回<code>null</code>
     */
    public static String[] split(String str, String separatorChars, int max) {
        if (str == null) {
            return null;
        }

        int length = str.length();

        if (length == 0) {
            return Emptys.EMPTY_STRING_ARRAY;
        }

        List<String> list = CollectionKit.createArrayList();
        int sizePlus1 = 1;
        int i = 0;
        int start = 0;
        boolean match = false;

        if (separatorChars == null) {
            // null表示使用空白作为分隔符
            while (i < length) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            // 优化分隔符长度为1的情形
            char sep = separatorChars.charAt(0);

            while (i < length) {
                if (str.charAt(i) == sep) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        } else {
            // 一般情形
            while (i < length) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match) {
                        if (sizePlus1++ == max) {
                            i = length;
                        }

                        list.add(str.substring(start, i));
                        match = false;
                    }

                    start = ++i;
                    continue;
                }

                match = true;
                i++;
            }
        }

        if (match) {
            list.add(str.substring(start, i));
        }

        return list.toArray(new String[list.size()]);
    }
    
    /**
	 * Convenience method to return a String array as a CSV String.
	 * E.g. useful for {@code toString()} implementations.
	 * @param arr the array to display
	 * @return the delimited String
	 */
	public static String arrayToCommaDelimitedString(Object[] arr) {
		return arrayToDelimitedString(arr, ",");
	}
	
	/**
	 * Convenience method to return a String array as a delimited (e.g. CSV)
	 * String. E.g. useful for {@code toString()} implementations.
	 * @param arr the array to display
	 * @param delim the delimiter to use (probably a ",")
	 * @return the delimited String
	 */
	public static String arrayToDelimitedString(Object[] arr, String delim) {
		if (arr == null || arr.length == 0) {
			return "";
		}
		
		if (arr.length == 1) {
			return nullSafeToString(arr[0]);
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < arr.length; i++) {
			if (i > 0) {
				sb.append(delim);
			}
			sb.append(arr[i]);
		}
		return sb.toString();
	}
	
	/**
	 * Determine if the given objects are equal, returning {@code true}
	 * if both are {@code null} or {@code false} if only one is
	 * {@code null}.
	 * <p>Compares arrays with {@code Arrays.equals}, performing an equality
	 * check based on the array elements rather than the array reference.
	 * @param o1 first Object to compare
	 * @param o2 second Object to compare
	 * @return whether the given objects are equal
	 * @see java.util.Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}
	
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return "null";
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : "");
	}
	
	/**
	 * 将字符串List转化为字符串，以分隔符间隔.
	 * 
	 * @param list			需要处理的List.
	 * @param separator		分隔符.
	 * @return 转化后的字符串
	 */
	public static String toString(List<String> list, String separator) {
		StringBuffer stringBuffer = new StringBuffer();
		for (String str : list) {
			stringBuffer.append(separator + str);
		}
		stringBuffer.deleteCharAt(0);
		return stringBuffer.toString();
	}
	
	public static String toString(Object value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }

    public static String toString(Collection<String> collection) {
        return toString(collection, " ");
    }

    public static String toString(Collection<String> collection, String split) {
        if (collection == null || split == null) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (Object object : collection) {
            builder.append(object).append(split);
        }

        builder.setLength(builder.length() - split.length());
        return builder.toString();
    }

    /**
     * 在字符串左侧填充一定数量的特殊字符
     *
     * @param o     可被 toString 的对象
     * @param width 字符数量
     * @param c     字符
     * @return 新字符串
     */
    public static String alignRight(Object o, int width, char c) {
        if (null == o)
            return null;
        String s = o.toString();
        int len = s.length();
        if (len >= width)
            return s;
        return new StringBuilder().append(dup(c, width - len)).append(s).toString();
    }

    /**
     * 在字符串右侧填充一定数量的特殊字符
     *
     * @param o     可被 toString 的对象
     * @param width 字符数量
     * @param c     字符
     * @return 新字符串
     */
    public static String alignLeft(Object o, int width, char c) {
        if (null == o)
            return null;
        String s = o.toString();
        int length = s.length();
        if (length >= width)
            return s;
        return new StringBuilder().append(s).append(dup(c, width - length)).toString();
    }

    /**
     * 复制字符
     *
     * @param c   字符
     * @param num 数量
     * @return 新字符串
     */
    public static String dup(char c, int num) {
        if (c == 0 || num < 1)
            return "";
        StringBuilder sb = new StringBuilder(num);
        for (int i = 0; i < num; i++)
            sb.append(c);
        return sb.toString();
    }
}
