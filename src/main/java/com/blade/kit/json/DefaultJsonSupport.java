package com.blade.kit.json;

import com.blade.kit.ReflectKit;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;

/**
 * @author biezhi
 * @date 2017/12/15
 */
@Slf4j
public class DefaultJsonSupport implements JsonSupport {

    @Override
    public String toString(Object object) {
        return toString(object, SerializeMapping.defaultMapping());
    }

    @Override
    public <T> T formJson(String json, Type type) {
        Object jsonObj = SampleJsonSerializer.deserialize(json);
        Class<T> cls = (Class<T>) ReflectKit.typeToClass(type);
        return BeanSerializer.deserialize(cls, jsonObj);
    }

    public String toString(Object object, SerializeMapping serializeMapping) {
        try {
            Object jsonObj = BeanSerializer.serialize(serializeMapping, object);
            return SampleJsonSerializer.serialize(jsonObj);
        } catch (Exception e) {
            log.error("object to json string error", e);
            return null;
        }
    }

    public Ason toAson(String json) {
        Object jsonObj = SampleJsonSerializer.deserialize(json);
        return (Ason) jsonObj;
    }

}
