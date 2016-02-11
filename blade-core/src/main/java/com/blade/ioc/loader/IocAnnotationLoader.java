/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.ioc.loader;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.blade.ioc.SampleIoc;
import com.blade.ioc.annotation.Component;

import blade.kit.resource.ClassPathClassReader;
import blade.kit.resource.ClassReader;

public final class IocAnnotationLoader implements IocLoader {
	
    private Collection<Class<?>> classes;
    
    private ClassReader classReader = new ClassPathClassReader();
    	
    public IocAnnotationLoader(String... packageNames) {
        List<Class<? extends Annotation>> annotations = new ArrayList<Class<? extends Annotation>>(1);
        annotations.add(Component.class);        
        this.classes = finder(Arrays.asList(packageNames), annotations, true);
    }

    private Collection<Class<?>> finder(List<String> packageNames, List<Class<? extends Annotation>> annotations, boolean recursive){
    	Collection<Class<?>> classes = new ArrayList<Class<?>>();
    	for(String packageName : packageNames){
    		for(Class<? extends Annotation> annotation : annotations){
    			classes.addAll(classReader.getClassByAnnotation(packageName, annotation, recursive));
    		}
    	}
    	return classes;
    }
    
    public IocAnnotationLoader(Collection<Class<?>> classes) {
        this.classes = classes;
    }

    @Override
    public void load(SampleIoc ioc) {
        for (Class<?> cls : classes) {
            Component anno = cls.getAnnotation(Component.class);
            if (anno != null) {
				String name = anno.value().equals("") ? cls.getName() : anno.value();
                ioc.addBean(name, cls, anno.singleton());
            }
        }
        // free
        classes = null;
    }
}
