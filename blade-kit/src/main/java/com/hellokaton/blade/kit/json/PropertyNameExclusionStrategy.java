package com.hellokaton.blade.kit.json;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

import java.util.Arrays;

/**
 * 属性过滤性
 */
public class PropertyNameExclusionStrategy implements ExclusionStrategy {

    private final String[] ignorePropertyNames;

    public PropertyNameExclusionStrategy(String[] ignorePropertyNames) {
        this.ignorePropertyNames = ignorePropertyNames;
    }

    /**
     * @param fieldAttributes the field object that is under test
     * @return true if the field should be ignored; otherwise false
     */
    @Override
    public boolean shouldSkipField(FieldAttributes fieldAttributes) {
        return null != this.ignorePropertyNames && Arrays.stream(this.ignorePropertyNames).anyMatch(s -> fieldAttributes.getName().equals(s));
    }

    /**
     * @param clazz the class object that is under test
     * @return true if the class should be ignored; otherwise false
     */
    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }
}
