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

import java.io.Serializable;

/**
 * 定义各种代表“空”的常量
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class Emptys {

    // =============================================================
    // 数组常量
    // =============================================================

    // primitive arrays

    /** 空的byte数组。 */
    public static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    public static final Byte[] EMPTY_BYTE_OBJECT_ARRAY = new Byte[0];

    /** 空的short数组。 */
    public static final short[] EMPTY_SHORT_ARRAY = new short[0];
    public static final Short[] EMPTY_SHORT_OBJECT_ARRAY = new Short[0];

    /** 空的int数组。 */
    public static final int[] EMPTY_INT_ARRAY = new int[0];
    public static final Integer[] EMPTY_INTEGER_OBJECT_ARRAY = new Integer[0];

    /** 空的long数组。 */
    public static final long[] EMPTY_LONG_ARRAY = new long[0];
    public static final Long[] EMPTY_LONG_OBJECT_ARRAY = new Long[0];

    /** 空的float数组。 */
    public static final float[] EMPTY_FLOAT_ARRAY = new float[0];
    public static final Float[] EMPTY_FLOAT_OBJECT_ARRAY = new Float[0];

    /** 空的double数组。 */
    public static final double[] EMPTY_DOUBLE_ARRAY = new double[0];
    public static final Double[] EMPTY_DOUBLE_OBJECT_ARRAY = new Double[0];

    /** 空的char数组。 */
    public static final char[] EMPTY_CHAR_ARRAY = new char[0];
    public static final Character[] EMPTY_CHARACTER_OBJECT_ARRAY = new Character[0];

    /** 空的boolean数组。 */
    public static final boolean[] EMPTY_BOOLEAN_ARRAY = new boolean[0];
    public static final Boolean[] EMPTY_BOOLEAN_OBJECT_ARRAY = new Boolean[0];

    // object arrays

    /** 空的Object数组。 */
    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];

    /** 空的Class数组。 */
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class[0];

    /** 空的String数组。 */
    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    // =============================================================
    // 对象常量
    // =============================================================

    // 0-valued primitive wrappers
    public static final Byte BYTE_ZERO = new Byte((byte) 0);
    public static final Short SHORT_ZERO = new Short((short) 0);
    public static final Integer INT_ZERO = new Integer(0);
    public static final Long LONG_ZERO = new Long(0L);
    public static final Float FLOAT_ZERO = new Float(0);
    public static final Double DOUBLE_ZERO = new Double(0);
    public static final Character CHAR_NULL = new Character('\0');
    public static final Boolean BOOL_FALSE = Boolean.FALSE;

    /** 代表null值的占位对象。 */
    public static final Object NULL_PLACEHOLDER = new NullPlaceholder();

    private final static class NullPlaceholder implements Serializable {

        /**
		 * 
		 */
        private static final long serialVersionUID = 5893921848518437319L;

        @Override
        public String toString() {
            return "null";
        }

        private Object readResolve() {
            return NULL_PLACEHOLDER;
        }
    }

    /** 空字符串。 */
    public static final String EMPTY_STRING = "";

}