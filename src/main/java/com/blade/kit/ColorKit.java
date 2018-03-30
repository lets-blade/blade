package com.blade.kit;

import static com.blade.kit.BladeKit.isWindows;

/**
 * Color kit
 *
 * @author biezhi
 * @date 2018/3/28
 */
public class ColorKit {

    private static final String ANSI_RESET             = "\u001B[0m";
    private static final String ANSI_BLACK             = "\u001B[30m";
    private static final String ANSI_RED               = "\u001B[31m";
    private static final String ANSI_GREEN             = "\u001B[32m";
    private static final String ANSI_YELLOW            = "\u001B[33m";
    private static final String ANSI_BLUE              = "\u001B[34m";
    private static final String ANSI_PURPLE            = "\u001B[35m";
    private static final String ANSI_CYAN              = "\u001B[36m";
    private static final String ANSI_WHITE             = "\u001B[37m";
    private static final String ANSI_GRAY              = "\u001B[90m";
    private static final String ANSI_BLACK_BACKGROUND  = "\u001B[40m";
    private static final String ANSI_RED_BACKGROUND    = "\u001B[41m";
    private static final String ANSI_GREEN_BACKGROUND  = "\u001B[42m";
    private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
    private static final String ANSI_BLUE_BACKGROUND   = "\u001B[44m";
    private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
    private static final String ANSI_CYAN_BACKGROUND   = "\u001B[46m";
    private static final String ANSI_WHITE_BACKGROUND  = "\u001B[47m";

    public static String green(String str) {
        if (isWindows()) return str;
        return ANSI_GREEN + str + ANSI_RESET;
    }

    public static String yellow(String str) {
        if (isWindows()) return str;
        return ANSI_YELLOW + str + ANSI_RESET;
    }

    public static String blue(String str) {
        if (isWindows()) return str;
        return ANSI_BLUE + str + ANSI_RESET;
    }

    public static String cyan(String str) {
        if (isWindows()) return str;
        return ANSI_CYAN + str + ANSI_RESET;
    }

    public static String gray(String str) {
        if (isWindows()) return str;
        return ANSI_GRAY + str + ANSI_RESET;
    }

    public static String red(String str) {
        if (isWindows()) return str;
        return ANSI_RED + str + ANSI_RESET;
    }

    public static String magenta(String str) {
        if (isWindows()) return str;
        return ANSI_PURPLE + str + ANSI_RESET;
    }

    public static String redAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_RED_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String yellowAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_YELLOW_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String greenAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_GREEN_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String blueAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_BLUE_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String cyanAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_CYAN_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String purpleAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_PURPLE_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String blankAndWhite(String str) {
        if (isWindows()) return str;
        return ANSI_BLACK_BACKGROUND + ANSI_WHITE + " " + str + " " + ANSI_RESET;
    }

    public static String whiteAndBlank(String str) {
        if (isWindows()) return str;
        return ANSI_WHITE_BACKGROUND + ANSI_BLACK + " " + str + " " + ANSI_RESET;
    }

}
