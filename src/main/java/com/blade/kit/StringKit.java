package com.blade.kit;

import com.blade.mvc.multipart.MimeType;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * String kit
 *
 * @author biezhi
 * 2017/6/1
 */
@UtilityClass
public class StringKit {

    private static final Random RANDOM = new Random();

    private static final char SEPARATOR = '_';

    /**
     * Randomly generate a number in the min and Max range
     *
     * @param min min value
     * @param max max value
     * @return return random int number
     */
    public static int rand(int min, int max) {
        return RANDOM.nextInt(max) % (max - min + 1) + min;
    }

    /**
     * Generate a number of numeric strings randomly
     *
     * @param size string count
     * @return return random string value
     */
    public static String rand(int size) {
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < size; i++) {
            double a = Math.random() * 9;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            num.append(randomNum);
        }
        return num.toString();
    }


    /**
     * Determine whether a list of string is not blank
     *
     * @param str a list of string value
     * @return return any one in this list of string is not blank
     */
    public static boolean isNotBlank(String... str) {
        if (str == null || str.length == 0) return false;
        for (String s : str) {
            if (isBlank(s)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Execute consumer when the string is not empty
     *
     * @param str      string value
     * @param consumer consumer
     */
    public static void isNotBlankThen(String str, Consumer<String> consumer) {
        notBankAccept(str, Function.identity(), consumer);
    }

    /**
     * Execute consumer when the string is empty
     *
     * @param str      string value
     * @param consumer consumer
     */
    public static void isBlankThen(String str, Consumer<String> consumer) {
        if (isBlank(str)) {
            consumer.accept(str);
        }
    }


    /**
     *
     * we can replace
     * `
     * if（StringUtils.isNotBlank(str)） {
     *      Integer i = Integer.parseInt (str)
     *     demo.setAge(i)
     * }
     * `
     * with
     * `
     *  notBankAccept ("2", Integer::parseInt, route::setSort)
     * `
     * @param str string value
     * @param consumer consumer
     * @param function format
     */

    public static <R> void notBankAccept(String str, Function<String, R> function, Consumer<R> consumer) {
        if (isNotBlank(str)) {
            R r = function.apply(str);
            consumer.accept(r);
        }
    }

    /**
     * Execute action when String is not empty
     * @param str String value
     * @param action action
     */
    public static void notBankThen(String str, Runnable action) {
        if (isNotBlank(str)) {
            action.run();
        }
    }

    /**
     * we can replace
     * if（doMethod1()!= null） {
     *     return doMethod1()
     * } else {
     *      return doMethod2()
     * }
     * with
     * return notBlankElse(bar::getName, bar::getNickName)
     * @param s1 Supplier
     * @param s2 Supplier
     */

    public static <T> T noNullElseGet(@NonNull Supplier<T> s1, @NonNull Supplier<T> s2) {
        T t1 = s1.get();
        if (t1 != null) {
            return t1;
        }
        return s2.get();
    }

    /**
     * Determine whether a string is blank
     *
     * @param str string value
     * @return return string is blank
     */
    public static boolean isBlank(String str) {
        return null == str || "".equals(str.trim());
    }

    public static boolean isEmpty(String str) {
        return null == str || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }


    /**
     * There is at least one null in the array of strings
     *
     * @param values string array
     * @return return whether or not there is an empty
     */
    public static boolean isAnyBlank(String... values) {
        if (CollectionKit.isEmpty(values)) {
            return true;
        }
        return Stream.of(values).filter(StringKit::isBlank).count() == values.length;
    }

    /**
     * determines whether the string is a numeric format
     *
     * @param value string value
     * @return return value is number
     */
    public static boolean isNumber(String value) {
        try {
            Double.parseDouble(value);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    /**
     * Fill a certain number of special characters on the right side of the string
     *
     * @param o     objects that can be to String
     * @param width number of characters
     * @param c     characters
     * @return new characters
     */
    public static String alignLeft(Object o, int width, char c) {
        if (null == o)
            return null;
        String s      = o.toString();
        int    length = s.length();
        if (length >= width)
            return s;
        return s + dup(c, width - length);
    }



    /**
     * Fill a certain number of special characters on the left side of the string
     *
     * @param o     objects that can be to String
     * @param width number of characters
     * @param c     characters
     * @return new characters
     */
    public static String alignRight(Object o, int width, char c) {
        if (null == o)
            return null;
        String s   = o.toString();
        int    len = s.length();
        if (len >= width)
            return s;
        return new StringBuilder().append(dup(c, width - len)).append(s).toString();
    }

    /**
     * Copy characters
     *
     * @param c   characters
     * @param num character number
     * @return new characters
     */
    public static String dup(char c, int num) {
        if (c == 0 || num < 1)
            return "";
        StringBuilder sb = new StringBuilder(num);
        for (int i = 0; i < num; i++)
            sb.append(c);
        return sb.toString();
    }

    public static String fileExt(String fname) {
        if (isBlank(fname) || fname.indexOf('.') == -1) {
            return null;
        }
        return fname.substring(fname.lastIndexOf('.') + 1);
    }

    public static String mimeType(String fileName) {
        String ext = fileExt(fileName);
        if (null == ext) {
            return null;
        }
        return MimeType.get(ext);
    }

    public static String padRight(String s, int n) {
        return String.format("%1$-" + n + "s", s);
    }

    public static String padLeft(String s, int n) {
        return String.format("%1$" + n + "s", s);
    }

    public static boolean equals(String str1, String str2) {
        if (null == str1) {
            return false;
        }
        return str1.equals(str2);
    }

    public static String toUnderlineName(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb        = new StringBuilder();
        boolean       upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if (Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    if (i > 0) sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }

    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb        = new StringBuilder(s.length());
        boolean       upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

}
