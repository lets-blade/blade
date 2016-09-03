package com.blade.kit.base;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import com.blade.kit.IOKit;

public class ConfigLoader {
	
    private final Map<String, String> config;
    
    public ConfigLoader() {
    	config = new HashMap<String, String>(32);
	}
    
	public ConfigLoader load(Properties props) {
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            config.put(key, value);
        }
        return this;
    }
	
	public ConfigLoader load(Map<String, String> map) {
        config.putAll(map);
        return this;
    }
	
	/**
     * 从文件路径或者classpath路径中载入配置.
     * @param location － 配置文件路径
     * @return this
     */
    public ConfigLoader load(String location) {
        if (location.startsWith("classpath:")) {
            location = location.substring("classpath:".length());
            return loadClasspath(location);
        } else if (location.startsWith("file:")) {
            location = location.substring("file:".length());
            return load(new File(location));
        } else {
            return load(new File(location));
        }
    }

    // 从 URL 载入
    public ConfigLoader load(URL url) {
        String location = url.getPath();
        try {
            location = URLDecoder.decode(location, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }

        try {
            return loadInputStream(url.openStream(), location);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // 从 classpath 下面载入
    private ConfigLoader loadClasspath(String classpath) {
        if (classpath.startsWith("/")) {
            classpath = classpath.substring(1);
        }
        InputStream is = getDefault().getResourceAsStream(classpath);
        return loadInputStream(is, classpath);
    }

    // 从 File 载入
    public ConfigLoader load(File file) {
        try {
            return loadInputStream(new FileInputStream(file), file.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
 // 载入 web 资源文件
    public ConfigLoader load(String location, ServletContext sc) {
        if (location.startsWith("classpath:") || location.startsWith("file:")) {
            return load(location);
        } else {
            if (location.startsWith("webroot:")) {
                location = location.substring("webroot:".length());
            }
            if (!location.startsWith("/")) {
                location = "/" + location;
            }
            InputStream is = sc.getResourceAsStream(location);
            return loadInputStream(is, location);
        }
    }

    private ConfigLoader loadInputStream(InputStream is, String location) {
        if (is == null) {
            throw new IllegalStateException("InputStream not found: " + location);
        }
        
        location = location.toLowerCase();
        try {
            Properties config = new Properties();
            config.load(is);
            load(config);
            return this;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            IOKit.closeQuietly(is);
        }
    }

    public ConfigLoader loadSystemProperties() {
        return load(System.getProperties());
    }

    public ConfigLoader loadSystemEnvs() {
        return load(System.getenv());
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
            loader = ConfigLoader.class.getClassLoader();
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
    
}
