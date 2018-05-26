package com.blade.kit.json;

import java.lang.reflect.Type;

/**
 * @author biezhi
 * @date 2017/12/15
 */
public interface JsonSupport {

    String toString(Object data);

    <T> T formJson(String json, Type cls);

}
