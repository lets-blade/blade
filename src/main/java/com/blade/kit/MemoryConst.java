package com.blade.kit;

import lombok.NoArgsConstructor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NoArgsConstructor
public final class MemoryConst {

    /**
     * Byte与Byte的倍数
     */
    public static final int BYTE = 1;
    /**
     * KB与Byte的倍数
     */
    public static final int KB   = 1024;
    /**
     * MB与Byte的倍数
     */
    public static final int MB   = 1048576;
    /**
     * GB与Byte的倍数
     */
    public static final int GB   = 1073741824;

    @Retention(RetentionPolicy.SOURCE)
    public @interface Unit {
    }

}