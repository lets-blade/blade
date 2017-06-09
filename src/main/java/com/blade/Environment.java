package com.blade;

import com.blade.kit.IOKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
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
public class Environment {

    private static final Logger log = LoggerFactory.getLogger(Environment.class);

    private Properties props = new Properties();

    private Environment() {
    }

    public static Environment empty() {
        return new Environment();
    }

    /**
     * Properties to Environment
     *
     * @param props
     * @return
     */
    public static Environment of(Properties props) {
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
    public static Environment of(Map<String, String> map) {
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
    public Environment of(URL url) {
        String location = url.getPath();
        try {
            location = URLDecoder.decode(location, "utf-8");
            return of(url.openStream(), location);
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
    public static Environment of(File file) {
        try {
            return of(Files.newInputStream(Paths.get(file.getPath())), file.getName());
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
    public static Environment of(String location) {
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

    private static Environment loadClasspath(String classpath) {
        if (classpath.startsWith("/")) {
            classpath = classpath.substring(1);
        }
        InputStream is = getDefault().getResourceAsStream(classpath);
        if (null == is) {
            return new Environment();
        }
        return of(is, classpath);
    }

    private static Environment of(InputStream is, String location) {
        if (is == null) {
            log.warn("InputStream not found: " + location);
            return null;
        }
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

    public Environment set(String key, Object value) {
        props.put(key, value);
        return this;
    }

    public Environment add(String key, Object value) {
        props.put(key, value);
        return this;
    }

    public Environment addAll(Map<String, String> map) {
        map.forEach((key, value) -> this.props.setProperty(key, value));
        return this;
    }

    public Environment addAll(Properties props) {
        props.forEach((key, value) -> this.props.setProperty(key.toString(), value.toString()));
        return this;
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(props.getProperty(key));
    }

    public String get(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public Optional<Object> getObject(String key) {
        return Optional.ofNullable(props.get(key));
    }

    public Optional<Integer> getInt(String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Integer.valueOf(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Integer getInt(String key, int defaultValue) {
        if (getInt(key).isPresent()) {
            return getInt(key).get();
        }
        return defaultValue;
    }

    public Optional<Long> getLong(String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Long.valueOf(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Long getLong(String key, long defaultValue) {
        if (getLong(key).isPresent()) {
            return getLong(key).get();
        }
        return defaultValue;
    }

    public Optional<Boolean> getBoolean(String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Boolean.valueOf(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Boolean getBoolean(String key, boolean defaultValue) {
        if (getBoolean(key).isPresent()) {
            return getBoolean(key).get();
        }
        return defaultValue;
    }

    public Optional<Double> getDouble(String key) {
        if (getObject(key).isPresent()) {
            return Optional.of(Double.valueOf(getObject(key).get().toString()));
        }
        return Optional.empty();
    }

    public Double getDouble(String key, double defaultValue) {
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
