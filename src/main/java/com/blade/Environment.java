package com.blade;

import com.blade.kit.IOKit;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;

/**
 * properties config env
 *
 * @author biezhi
 *         2017/6/1
 */
@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Environment {

    private Properties props = new Properties();

    public static Environment empty() {
        return new Environment();
    }

    /**
     * Properties to Environment
     *
     * @param props
     * @return
     */
    public static Environment of(@NonNull Properties props) {
        Environment environment = new Environment();
        environment.props = props;
        return environment;
    }

    /**
     * Map to Environment
     *
     * @param map
     * @return
     */
    public static Environment of(@NonNull Map<String, String> map) {
        Environment environment = new Environment();
        map.forEach((key, value) -> environment.props.setProperty(key, value));
        return environment;
    }

    /**
     * load Environment by URL
     *
     * @param url
     * @return
     */
    public Environment of(@NonNull URL url) {
        try {
            return of(url.openStream());
        } catch (UnsupportedEncodingException e) {
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return null;
    }

    /**
     * load Environment by file
     *
     * @param file
     * @return
     */
    public static Environment of(@NonNull File file) {
        try {
            return of(Files.newInputStream(Paths.get(file.getPath())));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * load Environment by location
     *
     * @param location
     * @return
     */
    public static Environment of(@NonNull String location) {
        if (location.startsWith("classpath:")) {
            location = location.substring("classpath:".length());
            return loadClasspath(location);
        } else if (location.startsWith("file:")) {
            location = location.substring("file:".length());
            return new Environment().of(new File(location));
        } else if (location.startsWith("url:")) {
            location = location.substring("url:".length());
            try {
                return new Environment().of(new URL(location));
            } catch (MalformedURLException e) {
                log.error("", e);
                return null;
            }
        } else {
            return new Environment().loadClasspath(location);
        }
    }

    private static Environment loadClasspath(@NonNull String classpath) {
        if (classpath.startsWith("/")) {
            classpath = classpath.substring(1);
        }
        InputStream is = getDefault().getResourceAsStream(classpath);
        if (null == is) {
            return new Environment();
        }
        return of(is);
    }

    private static Environment of(@NonNull InputStream is) {
        try {
            Environment environment = new Environment();
            environment.props.load(is);
            return environment;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOKit.closeQuietly(is);
        }
    }

    /**
     * Returns current thread's context class loader
     */
    public static ClassLoader getDefault() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
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

    public Environment set(@NonNull String key, @NonNull Object value) {
        props.put(key, value);
        return this;
    }

    public Environment add(@NonNull String key, @NonNull Object value) {
        props.put(key, value);
        return this;
    }

    public Environment addAll(@NonNull Map<String, String> map) {
        map.forEach((key, value) -> this.props.setProperty(key, value));
        return this;
    }

    public Environment addAll(@NonNull Properties props) {
        props.forEach((key, value) -> this.props.setProperty(key.toString(), value.toString()));
        return this;
    }

    public Optional<String> get(@NonNull String key) {
        return Optional.ofNullable(props.getProperty(key));
    }

    public String get(@NonNull String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public Optional<Object> getObject(@NonNull String key) {
        return Optional.ofNullable(props.get(key));
    }

    public Optional<Integer> getInt(@NonNull String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Integer.parseInt(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Integer getInt(@NonNull String key, int defaultValue) {
        if (getInt(key).isPresent()) {
            return getInt(key).get();
        }
        return defaultValue;
    }

    public Optional<Long> getLong(@NonNull String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Long.parseLong(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Long getLong(@NonNull String key, long defaultValue) {
        if (getLong(key).isPresent()) {
            return getLong(key).get();
        }
        return defaultValue;
    }

    public Optional<Boolean> getBoolean(@NonNull String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Boolean.parseBoolean(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Boolean getBoolean(@NonNull String key, boolean defaultValue) {
        if (getBoolean(key).isPresent()) {
            return getBoolean(key).get();
        }
        return defaultValue;
    }

    public Optional<Double> getDouble(@NonNull String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Double.parseDouble(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Double getDouble(@NonNull String key, double defaultValue) {
        if (getDouble(key).isPresent()) {
            return getDouble(key).get();
        }
        return defaultValue;
    }

    public Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>(props.size());
        props.forEach((k, v) -> map.put(k.toString(), v.toString()));
        return map;
    }

    public Properties props() {
        return props;
    }

}
