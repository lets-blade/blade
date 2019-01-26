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
import lombok.experimental.UtilityClass;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Ioc kit
 *
 * @author biezhi
 * 2017/5/31
 */
@UtilityClass
public class IocKit {

    /**
     * Get @Inject Annotated field
     *
     * @param ioc         ioc container
     * @param classDefine classDefine
     * @return return FieldInjector
     */
    private static List<FieldInjector> getInjectFields(Ioc ioc, ClassDefine classDefine) {
        List<FieldInjector> injectors = new ArrayList<>();
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

        Object bean = beanDefine.getBean();
        fieldInjectors.forEach(fieldInjector -> {
            Object fieldInstance = ReflectKit.newInstance(fieldInjector.getType());
            if (fieldInjector.hasInjectFields()) {
                injection(ioc, new BeanDefine(fieldInstance));
            }
            fieldInjector.injection(bean, fieldInstance);
        });
    }

    public static void initInjection(Ioc ioc, BeanDefine beanDefine) {
        ClassDefine         classDefine    = ClassDefine.create(beanDefine.getType());
        List<FieldInjector> fieldInjectors = getInjectFields(ioc, classDefine);

        Object bean = beanDefine.getBean();

        AtomicBoolean hasPrototypeField = new AtomicBoolean(false);

        fieldInjectors.forEach(fieldInjector -> {
            if (fieldInjector.isSingleton()) {
                fieldInjector.injection(bean);
            } else {
                hasPrototypeField.set(true);
            }
        });

        beanDefine.setFieldHasPrototype(hasPrototypeField.get());
    }

    public static void injectionValue(Environment environment, BeanDefine beanDefine) {
        ClassDefine         classDefine = ClassDefine.create(beanDefine.getType());
        List<ValueInjector> valueFields = getValueInjectFields(environment, classDefine);

        Object bean = beanDefine.getBean();

        valueFields.forEach(fieldInjector -> fieldInjector.injection(bean));
    }

    public static boolean isSingleton(Class<?> type) {
        return true;
    }

}
