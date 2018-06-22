package com.blade.loader;

import com.blade.Blade;

/**
 * Blade Loader
 *
 * @author biezhi
 * @date 2018/6/22
 */
public interface BladeLoader {

    default void preLoad(Blade blade) {

    }

    void load(Blade blade);

}
