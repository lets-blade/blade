package com.blade.kit;

import com.blade.mvc.multipart.MimeType;
import lombok.NoArgsConstructor;

import java.util.Random;

/**
 * @author biezhi
 *         2017/6/1
 */
@NoArgsConstructor
public final class StringKit {

    private static final Random random = new Random();

    public static int rand(int min, int max) {
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static String rand(int size) {
        String num = "";
        for (int i = 0; i < size; i++) {
            double a = Math.random() * 9;
            a = Math.ceil(a);
            int randomNum = new Double(a).intValue();
            num += randomNum;
        }
        return num;
    }

    public static boolean isNotBlank(String str) {
        return null != str && !"".equals(str.trim());
    }

    public static boolean isBlank(String str) {
        return null == str || "".equals(str.trim());
    }

    public static boolean isNumber(String string) {
        try {
            Double.parseDouble(string);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    private static boolean notIsDigit(int c) {
        return !Character.isDigit(c);
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

    public static String fileExt(String fname) {
        if (isBlank(fname) || fname.indexOf('.') == -1) {
            return null;
        }
        return fname.substring(fname.lastIndexOf('.') + 1);
    }

    public static String mimeType(String fname) {
        String ext = fileExt(fname);
        if (null == ext) {
            return null;
        }
        return MimeType.get(ext);
    }

    public static boolean equals(String str1, String str2) {
        if (null == str1) {
            return false;
        }
        return str1.equals(str2);
    }
}
