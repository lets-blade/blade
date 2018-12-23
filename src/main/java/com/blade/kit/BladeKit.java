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
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
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

    public static boolean runtimeIsJAR() {
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

    public static void log200(Logger log, String method, String uri) {
        if (!log.isInfoEnabled()) {
            return;
        }
        String pad    = StringKit.padLeft("", 6);
        String msg200 = Ansi.BgGreen.and(Ansi.Black).format(" 200 ");
        log.info("{} {}  {} {}", msg200, pad, method, uri);
    }

    public static long log200AndCost(Logger log, Instant start, String method, String uri) {
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

    public static void log405(Logger log, String method, String uri) {
        if (!log.isWarnEnabled()) {
            return;
        }
        String pad    = StringKit.padLeft("", 6);
        String msg404 = Ansi.BgRed.and(Ansi.Black).format(" 405 ");
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

    // --app.env=dev --server.port=9001
    public static Map<String, String> parseArgs(String[] args) {
        Map<String, String> argsMap = new HashMap<>();
        if (null == args || args.length == 0) {
            return argsMap;
        }
        for (String arg : args) {
            if (arg.startsWith("--") && arg.contains("=")) {
                String[] param = arg.substring(2).split("=");
                argsMap.put(param[0], param[1]);
            }
        }
        return argsMap;
    }

    /**
     * @return Get the process id of the current JVM process
     */
    public static Integer getPID() {
        String mbean = ManagementFactory.getRuntimeMXBean().getName();
        if (mbean.contains("@")) {
            return Integer.valueOf(mbean.substring(0, mbean.indexOf("@")));
        }
        return -1;
    }

}
