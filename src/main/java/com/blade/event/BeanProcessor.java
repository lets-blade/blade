package com.blade.event;

import com.blade.Blade;

@FunctionalInterface
public interface BeanProcessor {

    void processor(Blade blade);

    default void preHandle(Blade blade) {
    }

}