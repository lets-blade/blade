package com.blade.kit;

/**
 * @author biezhi
 * @date 2018/3/28
 */
public class ColorKit {

    public static String green(String str) {
        return "\033[32m" + str + "\033[0m";
    }

    public static String blue(String str) {
        return "\033[34m" + str + "\033[0m";
    }

    public static String red(String str) {
        return "\033[31m" + str + "\033[0m";
    }

    public static String yellow(String str) {
        return "\033[33m" + str + "\033[0m";
    }

    public static String gray(String str) {
        return "\033[37m" + str + "\033[0m";
    }

    public static String purple(String str) {
        return "\033[35m" + str + "\033[0m";
    }

    public static void blue(StringBuilder buf, String str) {
        buf.append("\033[34m").append(str).append("\033[0m");
    }

    public static void red(StringBuilder buf, String str) {
        buf.append("\033[31m").append(str).append("\033[0m");
    }

    public static void yellow(StringBuilder buf, String str) {
        buf.append("\033[33m").append(str).append("\033[0m");
    }

    public static void gray(StringBuilder buf, String str) {
        buf.append("\033[37m").append(str).append("\033[0m");
    }

    public static void green(StringBuilder buf, String str) {
        buf.append("\033[32m").append(str).append("\033[0m");
    }

    public static void purple(StringBuilder buf, String str) {
        buf.append("\033[35m").append(str).append("\033[0m");
    }

    public static String redAndWhite(String str) {
        return "\033[41;37m " + str + " \033[0m";
    }

    public static String yelloAndWhite(String str) {
        return "\033[43;37m " + str + " \033[0m";
    }

    public static String greenAndWhite(String str) {
        return "\033[42;37m " + str + " \033[0m";
    }

}
