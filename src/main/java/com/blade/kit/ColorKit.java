package com.blade.kit;

import static com.blade.kit.BladeKit.isWindows;

/**
 * @author biezhi
 * @date 2018/3/28
 */
public class ColorKit {

    public static String magenta(String str) {
        if (isWindows()) return str;
        return "\033[35m" + str + "\033[0m";
    }

    public static String redAndWhite(String str) {
        if (isWindows()) return str;
        return "\033[41;37m " + str + " \033[0m";
    }

    public static String yellowAndWhite(String str) {
        if (isWindows()) return str;
        return "\033[43;37m " + str + " \033[0m";
    }

    public static String greenAndWhite(String str) {
        if (isWindows()) return str;
        return "\033[42;37m " + str + " \033[0m";
    }

    public static String blueAndWhite(String str) {
        if (isWindows()) return str;
        return "\033[44;37m " + str + " \033[0m";
    }

    public static String cyanAndWhite(String str) {
        if (isWindows()) return str;
        return "\033[46;37m " + str + " \033[0m";
    }

}
