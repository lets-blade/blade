package com.blade.kit;

import com.blade.kit.json.Ason;
import com.blade.kit.json.BeanSerializer;
import com.blade.kit.json.JsonSerializer;

/**
 * @author biezhi
 *         2017/6/2
 */
public final class JsonKit {

    private JsonKit() {
    }

    public static String toString(Object object) {
        Object jsonObj = BeanSerializer.serialize(object);
        return JsonSerializer.serialize(jsonObj);
    }

    public static Ason toAson(String json) {
        Object jsonObj = JsonSerializer.deserialize(json);
        return (Ason) jsonObj;
    }

    public static <T> T formJson(String json, Class<T> cls) {
        Object jsonObj = JsonSerializer.deserialize(json);
        return BeanSerializer.deserialize(cls, jsonObj);
    }

}
