package com.blade.kit;

import com.blade.ioc.BeanDefine;
import com.blade.ioc.ClassDefine;
import com.blade.ioc.FieldInjector;
import com.blade.ioc.Ioc;
import com.blade.ioc.annotation.Inject;
import com.blade.ioc.annotation.InjectWith;
import com.blade.mvc.http.HttpMethod;
import lombok.NoArgsConstructor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Blade kit
 *
 * @author biezhi
 * 2017/5/31
 */
@NoArgsConstructor
public class BladeKit {

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

    public static void injection(Ioc ioc, BeanDefine beanDefine) {
        ClassDefine         classDefine    = ClassDefine.create(beanDefine.getType());
        List<FieldInjector> fieldInjectors = getInjectFields(ioc, classDefine);
        Object              bean           = beanDefine.getBean();
        for (FieldInjector fieldInjector : fieldInjectors) {
            fieldInjector.injection(bean);
        }
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
            return null != obj && Boolean.valueOf(obj.toString());
        } catch (Exception e) {
            return false;
        }
    }

}
