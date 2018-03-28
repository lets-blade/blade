package com.blade.kit;

import com.blade.kit.json.Ason;
import com.blade.kit.json.DefaultJsonSupport;
import com.blade.kit.json.JsonSupport;

/**
 * Json kit
 *
 * @author biezhi
 * 2017/6/2
 */
public final class JsonKit {

    private static final DefaultJsonSupport defaultJsonSupport = new DefaultJsonSupport();

    private static JsonSupport jsonSupport = new DefaultJsonSupport();

    public static void jsonSupprt(JsonSupport jsonSupport) {
        JsonKit.jsonSupport = jsonSupport;
    }

    public static String toString(Object object) {
        return jsonSupport.toString(object);
    }

    public static <T> T formJson(String json, Class<T> cls) {
        return jsonSupport.formJson(json, cls);
    }

    public static Ason<?, ?> toAson(String value) {
        return defaultJsonSupport.toAson(value);
    }

}