package com.blade.kit;

import lombok.NoArgsConstructor;

/**
 * Collection kit
 *
 * @author biezhi
 * @date 2017/12/15
 */
@NoArgsConstructor
public final class CollectionKit {

    public static <T> boolean isEmpty(T[] array) {
        return null == array || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return null != array && array.length > 0;
    }

}
