package com.blade.kit;

import com.blade.kit.json.Ason;
import com.blade.kit.json.BeanSerializer;
import com.blade.kit.json.JsonSerializer;
import com.blade.kit.json.SerializeMapping;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Json kit
 *
 * @author biezhi
 * 2017/6/2
 */
@Slf4j
@NoArgsConstructor
public final class JsonKit {

    public static String toString(Object object) {
        return toString(object, SerializeMapping.defaultMapping());
    }

    public static String toString(Object object, SerializeMapping serializeMapping) {
        try {
            Object jsonObj = BeanSerializer.serialize(serializeMapping, object);
            return JsonSerializer.serialize(jsonObj);
        } catch (Exception e) {
            log.error("object to json string error", e);
            return null;
        }
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