/**
 * Copyright (c) 2017, biezhi 王爵 (biezhi.me@gmail.com)
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

import com.blade.kit.json.Ason;
import com.blade.kit.json.DefaultJsonSupport;
import com.blade.kit.json.JsonSupport;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.lang.reflect.Type;

/**
 * Json kit
 *
 * @author biezhi
 * 2017/6/2
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class JsonKit {

    private static final DefaultJsonSupport defaultJsonSupport = new DefaultJsonSupport();

    private static JsonSupport jsonSupport = new DefaultJsonSupport();

    public static void jsonSupprt(JsonSupport jsonSupport) {
        JsonKit.jsonSupport = jsonSupport;
    }

    public static String toString(Object object) {
        return jsonSupport.toString(object);
    }

    public static <T> T formJson(String json, Type type) {
        return jsonSupport.formJson(json, type);
    }

    public static Ason<?, ?> toAson(String value) {
        return defaultJsonSupport.toAson(value);
    }

}