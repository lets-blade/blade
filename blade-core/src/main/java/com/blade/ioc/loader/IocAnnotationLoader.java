/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.ioc.loader;

import com.blade.ioc.SimpleIoc;
import com.blade.ioc.annotation.Component;
import com.blade.kit.CollectionKit;
import com.blade.kit.resource.ClassInfo;
import com.blade.kit.resource.ClassReader;
import com.blade.mvc.context.DynamicContext;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Ioc annotation loader
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public final class IocAnnotationLoader implements IocLoader {

    private Collection<ClassInfo> classes;

    public IocAnnotationLoader(String... packageNames) {
        List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>(1);
        annotations.add(Component.class);
        this.classes = finder(Arrays.asList(packageNames), annotations, true);
    }

    private Collection<ClassInfo> finder(List<String> packageNames, List<Class<? extends Annotation>> annotations, boolean recursive) {
        Collection<ClassInfo> classes = CollectionKit.newArrayList();
        for (String packageName : packageNames) {
            ClassReader classReader = DynamicContext.getClassReader(packageName);
            for (Class<? extends Annotation> annotation : annotations) {
                classes.addAll(classReader.getClassByAnnotation(packageName, annotation, recursive));
            }
        }
        return classes;
    }

    public IocAnnotationLoader(Collection<ClassInfo> classes) {
        this.classes = classes;
    }

    @Override
    public void load(SimpleIoc ioc) {
        for (ClassInfo classInfo : classes) {
            Class<?> cls = classInfo.getClazz();
            Component anno = cls.getAnnotation(Component.class);
            if (anno != null) {
                String name = anno.value().equals("") ? cls.getName() : anno.value();
                ioc.addBean(name, cls, true);
            }
        }
        // free
        classes = null;
    }
}
