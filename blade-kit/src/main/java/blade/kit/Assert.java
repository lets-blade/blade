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

import java.util.Collection;
import java.util.Map;

/**
 * 断言工具类
 * 
 * @author	biezhi
 * @since	1.0
 *
 */
public abstract class Assert {

	private static final String DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified exclusive range of %s to %s";
    private static final String DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE = "The value %s is not in the specified inclusive range of %s to %s";
    private static final String DEFAULT_IS_TRUE_EX_MESSAGE = "The validated expression is false";
    private static final String DEFAULT_IS_FALSE_EX_MESSAGE = "The validated expression is true";
    private static final String DEFAULT_NOT_BLANK_EX_MESSAGE = "The validated character sequence is blank";
    private static final String DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE = "The validated array is empty";
    private static final String DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE = "The validated character sequence is empty";
    private static final String DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE = "The validated collection is empty";
    private static final String DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE = "The validated map is empty";
    private static final String DEFAULT_IS_ASSIGNABLE_EX_MESSAGE = "Cannot assign a %s to a %s";
    private static final String DEFAULT_IS_INSTANCE_OF_EX_MESSAGE = "Expected type: %s, actual: %s";
    
    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void notNull(Object object) {
        notNull(object, "[Assertion failed] - this argument is required; it must not be null");
    }

    public static void hasLength(String text, String message) {
        if (StringKit.isEmpty(text)) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void isTrue(boolean expression) {
        if (expression == false) {
            throw new IllegalArgumentException(DEFAULT_IS_TRUE_EX_MESSAGE);
        }
    }

    public static void isTrue(boolean expression, String message, Object... values) {
        if (expression == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isFalse(boolean expression) {
        if (expression) {
            throw new IllegalArgumentException(DEFAULT_IS_FALSE_EX_MESSAGE);
        }
    }

    public static void isFalse(boolean expression, String message, Object... values) {
        if (expression) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void notNull(Object object, String message, Object... values) {
        if (object == null) {
            throw new NullPointerException(String.format(message, values));
        }
    }

    public static void notEmpty(Object[] array) {
        if (array == null) {
            throw new NullPointerException(DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(DEFAULT_NOT_EMPTY_ARRAY_EX_MESSAGE);
        }
    }

    public static void notEmpty(Object[] array, String message, Object... values) {
        if (array == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (array.length == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void notEmpty(Collection<?> collection) {
        if (collection == null) {
            throw new NullPointerException(DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
        }
        if (collection.size() == 0) {
            throw new IllegalArgumentException(DEFAULT_NOT_EMPTY_COLLECTION_EX_MESSAGE);
        }
    }

    public static void notEmpty(Collection<?> collection, String message, Object... values) {
        if (collection == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (collection.size() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void notEmpty(Map<?, ?> map) {
        if (map == null) {
            throw new NullPointerException(DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
        }
        if (map.size() == 0) {
            throw new IllegalArgumentException(DEFAULT_NOT_EMPTY_MAP_EX_MESSAGE);
        }
    }

    public static void notEmpty(Map<?, ?> map, String message, Object... values) {
        if (map == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (map.size() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void notEmpty(CharSequence str) {
        if (str == null) {
            throw new NullPointerException(DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
        }
        if (str.length() == 0) {
            throw new IllegalArgumentException(DEFAULT_NOT_EMPTY_CHAR_SEQUENCE_EX_MESSAGE);
        }
    }

    public static void notEmpty(CharSequence str, String message, Object... values) {
        if (str == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (str.length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void notBlank(CharSequence str) {
        if (str == null) {
            throw new NullPointerException(DEFAULT_NOT_BLANK_EX_MESSAGE);
        }
        if (str.toString().trim().length() == 0) {
            throw new IllegalArgumentException(DEFAULT_NOT_BLANK_EX_MESSAGE);
        }
    }

    public static void notBlank(CharSequence str, String message, Object... values) {
        if (str == null) {
            throw new NullPointerException(String.format(message, values));
        }
        if (str.toString().trim().length() == 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static <T> void inclusiveBetween(final T start, final T end, final Comparable<T> value, final String message, final Object... values) {
        if (value.compareTo(start) < 0 || value.compareTo(end) > 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void inclusiveBetween(long start, long end, long value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void inclusiveBetween(long start, long end, long value, final String message, final Object... values) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void inclusiveBetween(double start, double end, double value) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(DEFAULT_INCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void inclusiveBetween(double start, double end, double value, final String message, final Object... values) {
        if (value < start || value > end) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static <T> void exclusiveBetween(final T start, final T end, final Comparable<T> value) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static <T> void exclusiveBetween(final T start, final T end, final Comparable<T> value, final String message, final Object... values) {
        if (value.compareTo(start) <= 0 || value.compareTo(end) >= 0) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void exclusiveBetween(long start, long end, long value) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void exclusiveBetween(long start, long end, long value, final String message, final Object... values) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void exclusiveBetween(double start, double end, double value) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(String.format(DEFAULT_EXCLUSIVE_BETWEEN_EX_MESSAGE, value, start, end));
        }
    }

    public static void exclusiveBetween(double start, double end, double value, final String message, final Object... values) {
        if (value <= start || value >= end) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isInstanceOf(Class<?> type, Object obj) {
        if (type.isInstance(obj) == false) {
            throw new IllegalArgumentException(String.format(DEFAULT_IS_INSTANCE_OF_EX_MESSAGE, type.getName(), obj == null ? "null" : obj.getClass().getName()));
        }
    }

    public static void isInstanceOf(Class<?> type, Object obj, String message, Object... values) {
        if (type.isInstance(obj) == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

    public static void isAssignableFrom(Class<?> superType, Class<?> type) {
        if (superType.isAssignableFrom(type) == false) {
            throw new IllegalArgumentException(String.format(DEFAULT_IS_ASSIGNABLE_EX_MESSAGE, type == null ? "null" : type.getName(), superType.getName()));
        }
    }

    public static void isAssignableFrom(Class<?> superType, Class<?> type, String message, Object... values) {
        if (superType.isAssignableFrom(type) == false) {
            throw new IllegalArgumentException(String.format(message, values));
        }
    }

}
