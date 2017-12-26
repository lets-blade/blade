package com.blade.ioc.bean;

import com.blade.Environment;
import com.blade.ioc.Injector;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.util.*;
/**
 * Config annotation can be injected
 *
 * @author <a href="mailto:chenchen_839@126.com" target="_blank">ccqy66</a>
 */
@Slf4j
public class ValueInjector implements Injector {
    private Environment environment;
    private Field target;
    private String key;

    public ValueInjector(Environment environment, Field target, String key) {
        this.environment = environment;
        this.target      = target;
        this.key         = key;
    }

    @Override
    public void injection(Object bean) {
        try {
            if (!key.isEmpty()) {
                Class<?> clazz = target.getType();
                target.setAccessible(true);
                Optional<String> value = environment.get(key);
                if (!value.isPresent()) {
                    log.warn("config is absent,so can't be injected:target is {}",bean.getClass().getName());
                    return;
                }
                if (value.get().isEmpty()) {
                    log.warn("config is empty,so can't be injected:target is {}",bean.getClass().getName());
                    return;
                }
                //target field type is String
                if (clazz.isAssignableFrom(String.class)) {
                    target.set(bean, value.isPresent()? value.get() : "");
                    return;
                }

                //List and Map support,just support String element
                String split    = environment.get("value.split",",");
                String mapSplit = environment.get("value.map.split",":");
                if (clazz.isAssignableFrom(List.class)) {
                    target.set(bean,Arrays.asList(value.get().split(split)));
                    return;
                }
                Map<String,String> map = new HashMap(16);
                if (clazz.isAssignableFrom(Map.class)) {
                    Arrays.stream(value.get().split(split))
                            .filter(d -> d.indexOf(mapSplit) != -1)
                            .map(d -> d.split(mapSplit))
                            .forEach(keyValue -> map.put(keyValue[0],keyValue[1]));
                    target.set(bean,map);
                    return;
                }
            }else {
                log.warn("key is empty,so can't be injected:target is {}",bean.getClass().getName());
            }
        } catch (IllegalAccessException e) {
            log.error("inject config error! key is {},bean is {}",key,bean.getClass().getSimpleName(),e);
        }
    }
}
