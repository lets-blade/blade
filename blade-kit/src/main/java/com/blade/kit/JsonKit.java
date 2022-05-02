/**
 * Copyright (c) 2022, katon (hellokaton@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import com.blade.kit.json.MapDeserializerDoubleAsIntFix;
import com.blade.kit.json.PropertyNameExclusionStrategy;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Json kit
 *
 * @author hellokaton
 * 2022/5/2
 */
@UtilityClass
public class JsonKit {

    private static final Gson gson;

    static {
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .registerTypeAdapter(LocalDate.class, new JsonKit.LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new JsonKit.LocalDateTimeAdapter())
                .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
                }.getType(), new MapDeserializerDoubleAsIntFix())
                .create();
    }

    public String toString(Object object) {
        return toJson(object);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        if (StringKit.isEmpty(json)) return null;
        if (clazz == null) {
            return gson.fromJson(json, new TypeToken<Map<String, Object>>() {
            }.getType());
        }
        return gson.fromJson(json, clazz);
    }

    public static <T> T fromJson(String json, Type typeOfT) {
        if (StringKit.isEmpty(json)) return null;
        return gson.fromJson(json, typeOfT);
    }

    public static <T> T fromJson(String json, TypeToken<T> typeRef) {
        if (StringKit.isEmpty(json)) return null;
        return gson.fromJson(json, typeRef.getType());
    }

    public static <T> String toJson(T value) {
        if (value == null) return null;
        if (value instanceof String) return (String) value;
        return gson.toJson(value);
    }

    public static <T> String toJson(T value, boolean pretty) {
        if (!pretty) {
            return toJson(value);
        }
        if (value == null) return null;
        if (value instanceof String) return (String) value;
        return gson.newBuilder().setPrettyPrinting().create().toJson(value);
    }

    public static <T> String toJson(T value, final String... ignorePropertyNames) {
        if (null == ignorePropertyNames) return toJson(value);
        return new GsonBuilder()
                .excludeFieldsWithModifiers(Modifier.STATIC)
                .excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .addSerializationExclusionStrategy(new PropertyNameExclusionStrategy(ignorePropertyNames))
                .create()
                .toJson(value);
    }

    public static GsonBuilder newBuilder() {
        return gson.newBuilder();
    }

    final static class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {

        @Override
        public JsonElement serialize(LocalDate date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        }

        @Override
        public LocalDate deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            String timestamp = element.getAsJsonPrimitive().getAsString();
            return LocalDate.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE);
        }
    }

    final static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        @Override
        public JsonElement serialize(LocalDateTime date, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(date.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }

        @Override
        public LocalDateTime deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException {
            String timestamp = element.getAsJsonPrimitive().getAsString();
            return LocalDateTime.parse(timestamp, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        }
    }

//    public Ason<?, ?> toAson(String value) {
//        return defaultJsonSupport.toAson(value);
//    }

}