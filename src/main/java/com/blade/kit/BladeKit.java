package com.blade.kit;

import com.blade.Environment;
import com.blade.ioc.Ioc;
import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.InjectWith;
import com.blade.ioc.annotation.Value;
import com.blade.ioc.bean.BeanDefine;
import com.blade.ioc.bean.ClassDefine;
import com.blade.ioc.bean.FieldInjector;
import com.blade.ioc.bean.ValueInjector;
import com.blade.mvc.Const;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.route.Route;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Blade kit
 *
 * @author biezhi
 * 2017/5/31
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BladeKit {

    private static boolean isWindows;

    static {
        isWindows = System.getProperties().getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Get @Inject Annotated field
     *
     * @param ioc         ioc container
     * @param classDefine classDefine
     * @return return FieldInjector
     */
    private static List<FieldInjector> getInjectFields(Ioc ioc, ClassDefine classDefine) {
        List<FieldInjector> injectors = new ArrayList<>(8);
        for (Field field : classDefine.getDeclaredFields()) {
            if (null != field.getAnnotation(InjectWith.class) || null != field.getAnnotation(Inject.class)) {
                injectors.add(new FieldInjector(ioc, field));
            }
        }
        if (injectors.size() == 0) {
            return new ArrayList<>();
        }
        return injectors;
    }

    /**
     * Get @Value Annotated field
     *
     * @param environment
     * @param classDefine
     * @return
     */
    private static List<ValueInjector> getValueInjectFields(Environment environment, ClassDefine classDefine) {
        List<ValueInjector> valueInjectors = new ArrayList<>(8);
        //handle class annotation
        if (null != classDefine.getType().getAnnotation(Value.class)) {
            String suffix = classDefine.getType().getAnnotation(Value.class).name();
            Arrays.stream(classDefine.getDeclaredFields()).forEach(field -> valueInjectors.add(
                    new ValueInjector(environment, field, suffix + "." + field.getName())
            ));
        } else {
            Arrays.stream(classDefine.getDeclaredFields()).
                    filter(field -> null != field.getAnnotation(Value.class)).
                    map(field -> new ValueInjector(
                            environment, field, field.getAnnotation(Value.class).name())
                    ).forEach(valueInjectors::add);
        }
        return valueInjectors;
    }

    public static void injection(Ioc ioc, BeanDefine beanDefine) {
        ClassDefine         classDefine    = ClassDefine.create(beanDefine.getType());
        List<FieldInjector> fieldInjectors = getInjectFields(ioc, classDefine);
        Object              bean           = beanDefine.getBean();
        for (FieldInjector fieldInjector : fieldInjectors) {
            fieldInjector.injection(bean);
        }
    }

    public static void injectionValue(Environment environment, BeanDefine beanDefine) {
        ClassDefine         classDefine = ClassDefine.create(beanDefine.getType());
        List<ValueInjector> valueFileds = getValueInjectFields(environment, classDefine);
        Object              bean        = beanDefine.getBean();
        valueFileds.stream().forEach(fieldInjector -> fieldInjector.injection(bean));
    }

    public static boolean isEmpty(Collection<?> c) {
        return null == c || c.isEmpty();
    }

    public static <T> boolean isEmpty(T[] arr) {
        return null == arr || arr.length == 0;
    }

    public static boolean isNotEmpty(Collection<?> c) {
        return null != c && !c.isEmpty();
    }

    public static boolean isWebHook(HttpMethod httpMethod) {
        return httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER;
    }

    public static boolean notIsWebHook(HttpMethod httpMethod) {
        return !isWebHook(httpMethod);
    }

    public static boolean epollIsAvailable() {
        try {
            Object obj = Class.forName("io.netty.channel.epoll.Epoll").getMethod("isAvailable").invoke(null);
            return null != obj && Boolean.valueOf(obj.toString()) && System.getProperty("os.name").toLowerCase().contains("linux");
        } catch (Exception e) {
            return false;
        }
    }

    public static <T> void okThen(T value, Predicate<T> predicate, Consumer<T> consumer) {
        if (predicate.test(value)) {
            consumer.accept(value);
        }
    }

    public static <T> void equalThen(T a, T b, BiConsumer<T, T> consumer) {
        if (a.equals(b)) {
            consumer.accept(a, b);
        }
    }

    public static <T> void notNullThen(T value, Consumer<T> consumer) {
        if (null != value) {
            consumer.accept(value);
        }
    }

    public static boolean isInJar() {
        return Const.CLASSPATH.endsWith(".jar");
    }

    public static String getCurrentClassPath() {
        URL    url = BladeKit.class.getResource("/");
        String path;
        if (null == url) {
            File f = new File(BladeKit.class.getProtectionDomain().getCodeSource().getLocation().getPath());
            path = f.getPath();
        } else {
            path = url.getPath();
        }
        if (isWindows()) {
            return path.replaceFirst("^/(.:/)", "$1");
        }
        return path;
    }

    public static long getCostMS(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }

    public static void log500(Logger log, String method, String uri) {
        String pad = StringKit.padLeft("", 6);
        log.error("{} {}  {} {}", ColorKit.redAndWhite("500"), pad, method, uri);
    }

    public static void log304(Logger log, String method, String uri) {
        String pad = StringKit.padLeft("", 6);
        log.warn("{} {}  {} {}", ColorKit.greenAndWhite("304"), pad, method, uri);
    }

    public static long log200(Logger log, Instant start, String method, String uri) {
        long   cost = getCostMS(start);
        String pad  = StringKit.padLeft(String.valueOf(cost) + "ms", 6);
        log.info("{} {}  {} {}", ColorKit.greenAndWhite("200"), pad, method, uri);
        return cost;
    }

    public static void log403(Logger log, String method, String uri) {
        String pad = StringKit.padLeft("", 6);
        log.warn("{} {}  {} {}", ColorKit.yellowAndWhite("403"), pad, method, uri);
    }

    public static void log404(Logger log, String method, String uri) {
        String pad = StringKit.padLeft("", 6);
        log.warn("{} {}  {} {}", ColorKit.yellowAndWhite("404"), pad, method, uri);
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static String getStartedSymbol() {
        return isWindows() ? "" : "» ";
    }

    public static String getPrefixSymbol() {
        return isWindows() ? "=> " : "» ";
    }

    public static void logAddRoute(Logger log, Route route) {
        String method = StringKit.padRight(route.getHttpMethod().name(), 6);
        switch (route.getHttpMethod()) {
            case GET:
                method = ColorKit.greenAndWhite(method);
                break;
            case POST:
                method = ColorKit.blueAndWhite(method);
                break;
            case DELETE:
                method = ColorKit.redAndWhite(method);
                break;
            case PUT:
                method = ColorKit.yellowAndWhite(method);
                break;
            case OPTIONS:
                method = ColorKit.cyanAndWhite(method);
                break;
            case BEFORE:
                method = ColorKit.purpleAndWhite(method);
                break;
            case AFTER:
                method = ColorKit.whiteAndBlank(method);
                break;
        }
        String msg = (route.getHttpMethod().equals(HttpMethod.BEFORE) || route.getHttpMethod().equals(HttpMethod.AFTER)) ? " hook" : "route";
        log.info("{}Add {} {} {}", getStartedSymbol(), msg, method, route.getPath());
    }

}
