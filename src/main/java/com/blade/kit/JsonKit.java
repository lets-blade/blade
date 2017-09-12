package com.blade.kit;

import com.blade.kit.json.Ason;
import com.blade.kit.json.BeanSerializer;
import com.blade.kit.json.JsonSerializer;
import com.blade.kit.json.SerializeMapping;
import lombok.NoArgsConstructor;

/**
 * Json kit
 *
 * @author biezhi
 * 2017/6/2
 */
@NoArgsConstructor
public final class JsonKit {

    public static String toString(Object object) {
        return toString(object, SerializeMapping.defaultMapping());
    }

    public static String toString(Object object, SerializeMapping serializeMapping) {
        Object jsonObj = BeanSerializer.serialize(serializeMapping, object);
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