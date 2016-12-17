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
import java.util.concurrent.locks.Condition;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.IOKit;
import com.blade.kit.StringKit;

public class Config {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Config.class);
	
    private final Map<String, String> config;
    
    public Config() {
    	config = new HashMap<String, String>(32);
	}
    
	public Config load(Properties props) {
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            config.put(key, value);
        }
        return this;
    }

	public Config load(Map<String, String> map) {
        config.putAll(map);
        return this;
    }
	
	/**
     * 从文件路径或者classpath路径中载入配置.
     * @param location － 配置文件路径
     * @return this
     */
    public static Config load(String location) {
        return new Config().loadLoaction(location);
    }
    
    private Config loadLoaction(String location){
    	if (location.startsWith("classpath:")) {
            location = location.substring("classpath:".length());
            return loadClasspath(location);
        } else if (location.startsWith("file:")) {
            location = location.substring("file:".length());
            return load(new File(location));
        } else {
        	return loadClasspath(location);
        }
    }
    
    public void add(String location){
    	Config config = loadLoaction(location);
    	if(null != config){
    		this.config.putAll(config.asMap());
    	}
    }

    // 从 URL 载入
    public Config load(URL url) {
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
    private Config loadClasspath(String classpath) {
        if (classpath.startsWith("/")) {
            classpath = classpath.substring(1);
        }
        InputStream is = getDefault().getResourceAsStream(classpath);
        LOGGER.info("Load config [classpath:" + classpath + "]");
        return loadInputStream(is, classpath);
    }
    
    // 从 File 载入
    public Config load(File file) {
        try {
        	LOGGER.info("Load config [file:" + file.getPath() + "]");
            return loadInputStream(new FileInputStream(file), file.getName());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
	
    // 载入 web 资源文件
    public Config load(String location, ServletContext sc) {
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

    private Config loadInputStream(InputStream is, String location) {
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

    public Config loadSystemProperties() {
        return load(System.getProperties());
    }

    public Config loadSystemEnvs() {
        return load(System.getenv());
    }
    
    public Map<String, String> asMap(){
    	return this.config;
    }
    
    /**
     * Returns current thread's context class loader
     */
    private static ClassLoader getDefault() {
        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
        }
        if (loader == null) {
            loader = Config.class.getClassLoader();
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
    
    public String get(String key){
    	return config.get(key);
    }
    
    public String get(String key, String defaultValue) {
		return null != config.get(key) ? config.get(key) : defaultValue;
	}

	public Integer getInt(String key) {
		String value = get(key);
		if (StringKit.isNotBlank(value)) {
			return Integer.valueOf(value);
		}
		return null;
	}

	public Integer getInt(String key, Integer defaultValue) {
		return null != getInt(key) ? getInt(key) : defaultValue;
	}
	
	public Long getLong(String key) {
		String value = get(key);
		if (StringKit.isNotBlank(value)) {
			return Long.valueOf(value);
		}
		return null;
	}
	
	public Long getLong(String key, Long defaultValue) {
		return null != getLong(key) ? getLong(key) : defaultValue;
	}

	public Double getDouble(String key) {
		String value = get(key);
		if (StringKit.isNotBlank(value)) {
			return Double.valueOf(value);
		}
		return null;
	}
	
	public double getDouble(String key, double defaultValue) {
		return null != getDouble(key) ? getDouble(key) : defaultValue;
	}
	
	public Boolean getBoolean(String key) {
		String value = get(key);
		if (StringKit.isNotBlank(value)) {
			return Boolean.valueOf(value);
		}
		return null;
	}
	
	public Boolean getBoolean(String key, boolean defaultValue) {
		return null != getBoolean(key) ? getBoolean(key) : defaultValue;
	}

    public Config put(String key, Object value){
        config.put(key, value.toString());
        return this;
    }
	
}
