package com.hellokaton.blade.ioc;

import com.hellokaton.blade.ioc.bean.ClassInfo;
import com.hellokaton.blade.ioc.bean.Scanner;
import com.hellokaton.blade.ioc.reader.ClassPathClassReader;
import com.hellokaton.blade.ioc.reader.JarReaderImpl;
import com.hellokaton.blade.kit.StringKit;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Get ClassReader by JAR or folder
 *
 * @author <a href="mailto:hellokaton@gmail.com" target="_blank">hellokaton</a>
 * @since 1.6.6
 */
@Slf4j
@NoArgsConstructor
public final class DynamicContext {

    private static final ClassReader classpathReader = new ClassPathClassReader();
    private static final ClassReader jarReader       = new JarReaderImpl();
    private static final String      SUFFIX_JAR      = ".jar";

    private static boolean isJarContext = false;

    public static void init(Class<?> clazz) {
        String rs = clazz.getResource("").toString();
        if (rs.contains(SUFFIX_JAR)) {
            isJarContext = true;
        }
    }

    public static Stream<ClassInfo> recursionFindClasses(String packageName) {
        Scanner        scanner    = Scanner.builder().packageName(packageName).recursive(true).build();
        Set<ClassInfo> classInfos = getClassReader(packageName).readClasses(scanner);
        return classInfos.stream();
    }

    public static ClassReader getClassReader(String packageName) {
        if (isJarPackage(packageName)) {
            return jarReader;
        }
        return classpathReader;
    }

    public static boolean isJarPackage(String packageName) {
        if (StringKit.isBlank(packageName)) {
            return false;
        }
        try {
            packageName = packageName.replace(".", "/");
            Enumeration<URL> dirs = DynamicContext.class.getClassLoader().getResources(packageName);
            if (dirs.hasMoreElements()) {
                String url = dirs.nextElement().toString();
                return url.indexOf(".jar!") != -1 || url.indexOf(".zip!") != -1;
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return false;
    }

    public static boolean isJarContext() {
        return isJarContext;
    }

}