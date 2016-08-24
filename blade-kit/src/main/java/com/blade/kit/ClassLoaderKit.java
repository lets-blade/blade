package com.blade.kit;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ClassLoaderKit {
    private static final Map<String, String> abbreviationMap;

    /**
     * Returns current thread's context class loader
     */
    public static ClassLoader getDefault() {
        ClassLoader loader = null;
        try {
            loader = ClassLoader.class.getClassLoader();
        } catch (Exception e) {
        }
        if (loader == null) {
            loader = ClassLoaderKit.class.getClassLoader();
            if (loader == null) {
                loader = ClassLoader.getSystemClassLoader();
            }
        }
        return loader;
    }

    /**
     * 使用默认的 ClassLoader 去载入类.
     * @return null if class not found
     */
    public static Class<?> loadClass(final String qualifiedClassName) {
        return loadClass(qualifiedClassName, null);
    }

    /**
     * 使用默认的 ClassLoader 去载入类.
     * @return null if class not found
     */
    public static Class<?> loadClass(final String qualifiedClassName, ClassLoader loader) {
        try {
            return loadClassEx(qualifiedClassName, loader);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    /**
     * 使用默认的 ClassLoader 去载入类.
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClassEx(final String qualifiedClassName) throws ClassNotFoundException {
        return loadClassEx(qualifiedClassName, null);
    }

    /**
     * 使用指定的 ClassLoader 去载入类.
     * @throws ClassNotFoundException
     */
    public static Class<?> loadClassEx(final String qualifiedClassName, final ClassLoader classLoader) throws ClassNotFoundException {
        Assert.notNull(qualifiedClassName, "qualifiedClassName must be not null");

        ClassLoader loader = (classLoader == null) ? getDefault() : classLoader;

        // 尝试基本类型
        if (abbreviationMap.containsKey(qualifiedClassName)) {
            String className = '[' + abbreviationMap.get(qualifiedClassName);
            return Class.forName(className, false, loader).getComponentType();
        }

        // 尝试用 Class.forName()
        try {
            String className = getCanonicalClassName(qualifiedClassName);
            return Class.forName(className, false, loader);
        } catch (ClassNotFoundException e) {
        }

        // 尝试当做一个内部类去识别
        if (qualifiedClassName.indexOf('$') == -1) {
            int ipos = qualifiedClassName.lastIndexOf('.');
            if (ipos > 0) {
                try {
                    String className = qualifiedClassName.substring(0, ipos) + '$' + qualifiedClassName.substring(ipos + 1);
                    className = getCanonicalClassName(className);
                    return Class.forName(className, false, loader);
                } catch (ClassNotFoundException e) {
                }
            }
        }

        throw new ClassNotFoundException(qualifiedClassName);
    }

    /**
     * 将 Java 类名转为 {@code Class.forName()} 可以载入的类名格式.
     * <pre>
     * getCanonicalClassName("int") == "int";
     * getCanonicalClassName("int[]") == "[I";
     * getCanonicalClassName("java.lang.String") == "java.lang.String";
     * getCanonicalClassName("java.lang.String[]") == "[Ljava.lang.String;";
     * </pre>
     */
    public static String getCanonicalClassName(String qualifiedClassName) {
    	Assert.notNull(qualifiedClassName, "qualifiedClassName must be not null");

        String name = StringKit.trimToEmpty(qualifiedClassName);
        if (name.endsWith("[]")) {
            StringBuilder sb = new StringBuilder();

            while (name.endsWith("[]")) {
                name = name.substring(0, name.length() - 2);
                sb.append('[');
            }

            String abbreviation = abbreviationMap.get(name);
            if (abbreviation != null) {
                sb.append(abbreviation);
            } else {
                sb.append('L').append(name).append(';');
            }

            name = sb.toString();
        }
        return name;
    }

    /**
     * Finds the resource with the given name.
     * @param name - The resource name
     * @return A URL object for reading the resource, or null if the resource could not be found
     */
    public static URL getResource(String name) {
        return getResource(name, null);
    }

    /**
     * Finds the resource with the given name.
     * @param name - The resource name
     * @return A URL object for reading the resource, or null if the resource could not be found
     */
    public static URL getResource(String name, ClassLoader classLoader) {
    	Assert.notNull(name, "resourceName must be not null");

        if (name.startsWith("/")) {
            name = name.substring(1);
        }
        if (classLoader != null) {
            URL url = classLoader.getResource(name);
            if (url != null) {
                return url;
            }
        }
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null && loader != classLoader) {
            URL url = loader.getResource(name);
            if (url != null) {
                return url;
            }
        }

        return ClassLoader.getSystemResource(name);
    }

    /**
     * Returns an input stream for reading the specified resource.
     */
    public static InputStream getResourceAsStream(String name) throws IOException {
        return getResourceAsStream(name, null);
    }

    /**
     * Returns an input stream for reading the specified resource.
     */
    public static InputStream getResourceAsStream(String name, ClassLoader classLoader) throws IOException {
        URL url = getResource(name, classLoader);
        if (url != null) {
            return url.openStream();
        }
        return null;
    }

    /**
     * Returns an input stream for reading the specified class.
     */
    public static InputStream getClassAsStream(Class<?> clazz) throws IOException {
        return getResourceAsStream(getClassFileName(clazz), clazz.getClassLoader());
    }

    /**
     * Returns an input stream for reading the specified class.
     */
    public static InputStream getClassAsStream(String qualifiedClassName) throws IOException {
        return getResourceAsStream(getClassFileName(qualifiedClassName));
    }

    /**
     * 获取一个 class 所代表的文件名
     */
    public static String getClassFileName(Class<?> clazz) {
        if (clazz.isArray()) {
            clazz = clazz.getComponentType();
        }
        return getClassFileName(clazz.getName());
    }

    /**
     * 获取一个 class 所代表的文件名
     */
    public static String getClassFileName(String qualifiedClassName) {
        return qualifiedClassName.replace('.', '/') + ".class";
    }

    static {
        abbreviationMap = new HashMap<String, String>();
        abbreviationMap.put("boolean", "Z");
        abbreviationMap.put("byte", "B");
        abbreviationMap.put("short", "S");
        abbreviationMap.put("char", "C");
        abbreviationMap.put("int", "I");
        abbreviationMap.put("long", "J");
        abbreviationMap.put("float", "F");
        abbreviationMap.put("double", "D");
    }
}