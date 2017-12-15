package com.blade.kit.json;

/**
 * @author biezhi
 * @date 2017/12/15
 */
public interface JsonSupport {

    String toString(Object data);

    <T> T formJson(String json, Class<T> cls);

}
