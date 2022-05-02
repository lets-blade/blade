package com.blade.websocket;

import com.blade.Blade;
import com.blade.kit.ReflectKit;
import com.blade.websocket.annotaion.OnClose;
import com.blade.websocket.annotaion.OnMessage;
import com.blade.websocket.annotaion.OnOpen;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author darren
 * @description
 * @date 2018/12/17 18:41
 */
@Slf4j
public final class WebSocketHandlerWrapper implements WebSocketHandler {

    private final Map<String,Class<?>> handlers = new HashMap<>(4);
    private final Map<String, Map<Class<? extends Annotation>, Method>> methodCache = new HashMap<>(4);
    private final FastThreadLocal<String> path = new FastThreadLocal<>();
    private final Blade blade;

    public static WebSocketHandlerWrapper init(Blade blade) {
        return new WebSocketHandlerWrapper(blade);
    }

    private WebSocketHandlerWrapper(Blade blade) {
        this.blade = blade;
    }

    public void setPath(String path) {
        this.path.set(path);
    }

    /**
     * add @WebSocket handler mapper
     *
     * @param path
     * @param handler
     */
    public void wrapHandler(String path, Class<?> handler) {
        Method[] methods = handler.getMethods();
        Map<Class<? extends Annotation>, Method> cache = new HashMap<>(3);
        cacheMethod(cache, methods, OnOpen.class);
        cacheMethod(cache, methods, OnMessage.class);
        cacheMethod(cache, methods, OnClose.class);
        if (cache.size() > 0) {
            methodCache.put(path, cache);
            handlers.put(path,handler);
        } else {
            throw new RuntimeException("Do not found any annotation of [@OnOpen / @OnMessage / @OnClose] in class: " + handler.getName());
        }
    }

    private static void cacheMethod(Map<Class<? extends Annotation>, Method> cache, Method[] methods, Class<? extends Annotation> filter) {
        List<Method> methodList = Stream.of(methods)
                .filter(method -> method.isAnnotationPresent(filter))
                .collect(Collectors.toList());
        if (methodList.size() == 1) {
            cache.put(filter, methodList.get(0));
        } else if (methodList.size() > 1) {
            throw new RuntimeException("Duplicate annotation @" + filter.getSimpleName() + " in class: " + methodList.get(0).getDeclaringClass().getName());
        }
    }

    @Override
    public void onConnect(WebSocketContext ctx) {
        invoke(ctx, OnOpen.class);
    }

    @Override
    public void onText(WebSocketContext ctx) {
        invoke(ctx, OnMessage.class);
    }

    @Override
    public void onDisConnect(WebSocketContext ctx) {
        invoke(ctx, OnClose.class);
    }

    /**
     * invoke target handler methods
     *
     * @param ctx   WebSocket context
     * @param event WebSocket event type
     */
    private void invoke(WebSocketContext ctx, Class<? extends Annotation> event) {
        Map<Class<? extends Annotation>, Method> methodCache = this.methodCache.get(path.get());
        if (methodCache != null) {
            Method method = methodCache.get(event);
            if (method != null) {
                Class<?>[] paramTypes = method.getParameterTypes();
                Object[] param = new Object[paramTypes.length];
                try {
                    for (int i = 0; i < paramTypes.length; i++) {
                        Class<?> paramType = paramTypes[i];
                        if (paramType == WebSocketContext.class) {
                            param[i] = ctx;
                        } else {
                            Object bean = this.blade.getBean(paramType);
                            if (bean != null) {
                                param[i] = bean;
                            } else {
                                param[i] = ReflectKit.newInstance(paramType);
                            }
                        }
                    }
                    method.invoke(blade.getBean(handlers.get(path.get())), param);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
