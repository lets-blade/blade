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
import com.blade.task.TaskStruct;
import com.blade.task.annotation.Schedule;
import lombok.experimental.UtilityClass;
import org.slf4j.Logger;

import java.io.File;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
import java.util.stream.Collectors;

/**
 * Blade kit
 *
 * @author biezhi
 * 2017/5/31
 */
@UtilityClass
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
        List<ValueInjector> valueFields = getValueInjectFields(environment, classDefine);
        Object              bean        = beanDefine.getBean();
        valueFields.stream().forEach(fieldInjector -> fieldInjector.injection(bean));
    }

    public static String getLambdaFieldName(Serializable lambda) {
        for (Class<?> cl = lambda.getClass(); cl != null; cl = cl.getSuperclass()) {
            try {
                Method m = cl.getDeclaredMethod("writeReplace");
                m.setAccessible(true);
                Object replacement = m.invoke(lambda);
                if (!(replacement instanceof SerializedLambda)) {
                    break; // custom interface implementation
                }
                SerializedLambda serializedLambda = (SerializedLambda) replacement;
                return BladeCache.getLambdaFieldName(serializedLambda);
            } catch (NoSuchMethodException e) {
                // do nothing
            } catch (IllegalAccessException | InvocationTargetException e) {
                break;
            }
        }
        return null;
    }

    public static String methodToFieldName(String methodName) {
        return capitalize(methodName.replace("get", ""));
    }

    public static String capitalize(String input) {
        return input.substring(0, 1).toLowerCase() + input.substring(1, input.length());
    }

    public static List<TaskStruct> getTasks(Class<?> type) {
        return Arrays.stream(type.getMethods())
                .filter(m -> null != m.getAnnotation(Schedule.class))
                .map(m -> {
                    TaskStruct taskStruct = new TaskStruct();
                    taskStruct.setSchedule(m.getAnnotation(Schedule.class));
                    taskStruct.setMethod(m);
                    taskStruct.setType(type);
                    return taskStruct;
                })
                .collect(Collectors.toList());
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
            return decode(path.replaceFirst("^/(.:/)", "$1"));
        }
        return decode(path);
    }

    private static String decode(String path) {
        try {
            return java.net.URLDecoder.decode(path, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return path;
        }
    }

    public static long getCostMS(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }

    public static void log500(Logger log, String method, String uri) {
        String pad    = StringKit.padLeft("", 6);
        String msg500 = Ansi.BgRed.and(Ansi.Black).format(" 500 ");
        log.error("{} {}  {} {}", msg500, pad, method, uri);
    }

    public static void log304(Logger log, String method, String uri) {
        if (!log.isWarnEnabled()) {
            return;
        }
        String pad    = StringKit.padLeft("", 6);
        String msg304 = Ansi.BgGreen.and(Ansi.Black).format(" 304 ");
        log.warn("{} {}  {} {}", msg304, pad, method, uri);
    }

    public static long log200(Logger log, Instant start, String method, String uri) {
        long cost = getCostMS(start);
        if (!log.isInfoEnabled()) {
            return cost;
        }
        String pad    = StringKit.padLeft(String.valueOf(cost) + "ms", 6);
        String msg200 = Ansi.BgGreen.and(Ansi.Black).format(" 200 ");
        log.info("{} {}  {} {}", msg200, pad, method, uri);

        return cost;
    }

    public static void log403(Logger log, String method, String uri) {
        if (!log.isWarnEnabled()) {
            return;
        }
        String pad    = StringKit.padLeft("", 6);
        String msg403 = Ansi.BgYellow.and(Ansi.Black).format(" 403 ");
        log.warn("{} {}  {} {}", msg403, pad, method, uri);
    }

    public static void log404(Logger log, String method, String uri) {
        if (!log.isWarnEnabled()) {
            return;
        }
        String pad    = StringKit.padLeft("", 6);
        String msg404 = Ansi.BgRed.and(Ansi.Black).format(" 404 ");
        log.warn("{} {}  {} {}", msg404, pad, method, uri);
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
            case ALL:
                method = Ansi.BgBlack.and(Ansi.White).format(" %s ", method);
                break;
            case GET:
                method = Ansi.BgGreen.and(Ansi.Black).format(" %s ", method);
                break;
            case POST:
                method = Ansi.BgBlue.and(Ansi.Black).format(" %s ", method);
                break;
            case DELETE:
                method = Ansi.BgRed.and(Ansi.Black).format(" %s ", method);
                break;
            case PUT:
                method = Ansi.BgYellow.and(Ansi.Black).format(" %s ", method);
                break;
            case OPTIONS:
                method = Ansi.BgCyan.and(Ansi.Black).format(" %s ", method);
                break;
            case BEFORE:
                method = Ansi.BgMagenta.and(Ansi.Black).format(" %s ", method);
                break;
            case AFTER:
                method = Ansi.BgWhite.and(Ansi.Black).format(" %s ", method);
                break;
        }
        String msg = (route.getHttpMethod().equals(HttpMethod.BEFORE) || route.getHttpMethod().equals(HttpMethod.AFTER)) ? " hook" : "route";
        log.info("{}Add {} {} {}", getStartedSymbol(), msg, method, route.getPath());
    }

    public static void logWebSocket(Logger log, String path) {
        log.info("{}Add WebSocket {}", getStartedSymbol(), path);
    }

}
