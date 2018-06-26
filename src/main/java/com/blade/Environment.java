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
package com.blade;

import com.blade.kit.IOKit;
import com.blade.kit.ReflectKit;
import com.blade.server.netty.HttpConst;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static java.util.Optional.ofNullable;

/**
 * Blade environment config
 * <p>
 * This class can help you to load the properties type of the configuration file,
 * and easy to read、write
 *
 * @author biezhi
 * 2017/6/1
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Environment {

    private static final String      PREFIX_CLASSPATH = "classpath:";
    private static final String      PREFIX_FILE      = "file:";
    private static final String      PREFIX_URL       = "url:";
    private static final Environment EMPTY_ENV        = new Environment();

    /**
     * Save the internal configuration
     */
    private Properties props = new Properties();

    /**
     * Create an empty environment
     *
     * @return return Environment instance
     */
    public static Environment empty() {
        return EMPTY_ENV;
    }

    /**
     * Properties to Environment
     *
     * @param props properties instance
     * @return return Environment instance
     */
    public static Environment of(@NonNull Properties props) {
        var environment = new Environment();
        environment.props = props;
        return environment;
    }

    /**
     * Map to Environment
     *
     * @param map config map
     * @return return Environment instance
     */
    public static Environment of(@NonNull Map<String, String> map) {
        var environment = new Environment();
        map.forEach((key, value) -> environment.props.setProperty(key, value));
        return environment;
    }

    /**
     * Load environment by URL
     *
     * @param url file url
     * @return return Environment instance
     */
    public static Environment of(@NonNull URL url) {
        try {
            return of(url.openStream());
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    /**
     * Load environment by file
     *
     * @param file environment file
     * @return return Environment instance
     */
    public static Environment of(@NonNull File file) {
        try {
            return of(Files.newInputStream(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Load environment by location
     *
     * @param location environment location
     * @return return Environment instance
     */
    public static Environment of(@NonNull String location) {
        if (location.startsWith(PREFIX_CLASSPATH)) {
            location = location.substring(PREFIX_CLASSPATH.length());
            return loadClasspath(location);
        } else if (location.startsWith(PREFIX_FILE)) {
            location = location.substring(PREFIX_FILE.length());
            return of(new File(location));
        } else if (location.startsWith(PREFIX_URL)) {
            location = location.substring(PREFIX_URL.length());
            try {
                return of(new URL(location));
            } catch (MalformedURLException e) {
                log.error("", e);
                return null;
            }
        } else {
            return loadClasspath(location);
        }
    }

    /**
     * Load classpath file to Environment
     *
     * @param classpath classpath url
     * @return return Environment instance
     */
    private static Environment loadClasspath(@NonNull String classpath) {
        var path = classpath;
        if (classpath.startsWith(HttpConst.SLASH)) {
            path = classpath.substring(1);
        }
        InputStream is = getDefault().getResourceAsStream(path);
        if (null == is) {
            return EMPTY_ENV;
        }
        return of(is);
    }

    /**
     * Load InputStream to Environment
     *
     * @param is InputStream instance
     * @return return Environment instance
     */
    private static Environment of(@NonNull InputStream is) {
        try {
            var environment = new Environment();
            environment.props.load(new InputStreamReader(is, "UTF-8"));
            return environment;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOKit.closeQuietly(is);
        }
    }

    /**
     * Get current thread context ClassLoader
     *
     * @return return ClassLoader
     */
    public static ClassLoader getDefault() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception ignored) {
        }
        if (loader == null) {
            loader = Environment.class.getClassLoader();
            if (loader == null) {
                try {
                    // getClassLoader() returning null indicates the bootstrap ClassLoader
                    loader = ClassLoader.getSystemClassLoader();
                } catch (Exception e) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }
        return loader;
    }

    /**
     * Set a value to props
     *
     * @param key   key
     * @param value value
     * @return return Environment instance
     */
    public Environment set(@NonNull String key, @NonNull Object value) {
        props.put(key, value.toString());
        return this;
    }

    /**
     * And Set the same
     *
     * @param key   key
     * @param value value
     * @return return Environment instance
     */
    public Environment add(@NonNull String key, @NonNull Object value) {
        return set(key, value);
    }

    /**
     * Add a map to props
     *
     * @param map map config instance
     * @return return Environment instance
     */
    public Environment addAll(@NonNull Map<String, String> map) {
        map.forEach((key, value) -> this.props.setProperty(key, value));
        return this;
    }

    public Environment addAll(@NonNull Properties props) {
        props.forEach((key, value) -> this.props.setProperty(key.toString(), value.toString()));
        return this;
    }

    /**
     * Properties to Environment
     *
     * @param environment environment instance
     * @return return Environment instance
     */
    public Environment load(@NonNull Environment environment) {
        this.props.putAll(environment.toMap());
        return this;
    }

    public Optional<String> get(String key) {
        if (null == key) return Optional.empty();
        return ofNullable(props.getProperty(key));
    }

    public String getOrNull(String key) {
        return get(key).orElse(null);
    }

    public String get(String key, String defaultValue) {
        return get(key).orElse(defaultValue);
    }

    public Optional<Object> getObject(String key) {
        return ofNullable(props.get(key));
    }

    public Optional<Integer> getInt(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Integer.parseInt(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Integer getIntOrNull(String key) {
        var intVal = getInt(key);
        return intVal.orElse(null);
    }

    public Integer getInt(String key, int defaultValue) {
        if (getInt(key).isPresent()) {
            return getInt(key).get();
        }
        return defaultValue;
    }

    public Optional<Long> getLong(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Long.parseLong(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Long getLongOrNull(String key) {
        var longVal = getLong(key);
        return longVal.orElse(null);
    }

    public Long getLong(String key, long defaultValue) {
        return getLong(key).orElse(defaultValue);
    }

    public Optional<Boolean> getBoolean(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Boolean.parseBoolean(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Boolean getBooleanOrNull(String key) {
        var boolVal = getBoolean(key);
        return boolVal.orElse(null);
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(key).orElse(defaultValue);
    }

    public Optional<Double> getDouble(String key) {
        if (null != key && getObject(key).isPresent()) {
            return Optional.of(Double.parseDouble(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Double getDoubleOrNull(String key) {
        var doubleVal = getDouble(key);
        return doubleVal.orElse(null);
    }

    public Double getDouble(String key, double defaultValue) {
        return getDouble(key).orElse(defaultValue);
    }

    public Optional<Date> getDate(String key) {
        if (null != key && getObject(key).isPresent()) {
            var value = getObject(key).get().toString();
            var date  = (Date) ReflectKit.convert(Date.class, value);
            return Optional.ofNullable(date);
        }
        return Optional.empty();
    }

    public Date getDateOrNull(String key) {
        var dateVal = getDate(key);
        return dateVal.orElse(null);
    }

    public Map<String, Object> getPrefix(String key) {
        var map = new HashMap<String, Object>();
        if (null != key) {
            props.forEach((key_, value) -> {
                if (key_.toString().startsWith(key)) {
                    map.put(key_.toString().substring(key.length() + 1), value);
                }
            });
        }
        return map;
    }

    public Map<String, String> toMap() {
        var map = new HashMap<String, String>(props.size());
        props.forEach((k, v) -> map.put(k.toString(), v.toString()));
        return map;
    }

    public boolean hasKey(String key) {
        if (null == key) {
            return false;
        }
        return props.containsKey(key);
    }

    public boolean hasValue(String value) {
        return props.containsValue(value);
    }

    public Properties props() {
        return props;
    }

    public int size() {
        return props.size();
    }

    public boolean isEmpty() {
        return props.isEmpty();
    }

}
