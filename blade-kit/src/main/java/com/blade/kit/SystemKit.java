package com.blade.kit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SystemKit {

    private static boolean isWindows;

    static {
        isWindows = System.getProperties().getProperty("os.name").toLowerCase().contains("win");
    }

    public static boolean isWindows() {
        return isWindows;
    }

}
