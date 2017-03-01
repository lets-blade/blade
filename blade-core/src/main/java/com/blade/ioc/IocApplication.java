/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.ioc;

import com.blade.Blade;
import com.blade.comparator.OrderComparator;
import com.blade.context.WebContextListener;
import com.blade.ioc.annotation.Component;
import com.blade.ioc.annotation.Service;
import com.blade.kit.CollectionKit;
import com.blade.kit.IocKit;
import com.blade.kit.reflect.ReflectKit;
import com.blade.kit.resource.ClassInfo;
import com.blade.kit.resource.ClassReader;
import com.blade.mvc.annotation.Controller;
import com.blade.mvc.annotation.RestController;
import com.blade.mvc.context.DynamicContext;
import com.blade.mvc.interceptor.Interceptor;
import com.blade.mvc.route.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Set;

/**
 * IOC container, used to initialize the IOC object
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.0
 */
public final class IocApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(IocApplication.class);

    /**
     * aop interceptor
     */
    private static List<Object> aopInterceptors = CollectionKit.newArrayList(8);

    private Blade blade;
    private OrderComparator orderComparator;
    private List<WebContextListener> ctxs = CollectionKit.newArrayList();

    public IocApplication() {
        this.blade = Blade.$();
        this.orderComparator = new OrderComparator();
    }

    public void initBeans() throws Exception {

        Set<String> pkgs = blade.bConfig().getPackages();
        if (null != pkgs) {
            Ioc ioc = blade.ioc();
            RouteBuilder routeBuilder = blade.routeBuilder();

            List<BeanProcessor> processors = CollectionKit.newArrayList();
            List<ClassInfo> ctxClasses = CollectionKit.newArrayList(8);
            List<ClassInfo> processoers = CollectionKit.newArrayList(8);

            pkgs.forEach(p -> {
                ClassReader classReader = DynamicContext.getClassReader(p);
                Set<ClassInfo> classInfos = classReader.getClass(p, true);
                if (null != classInfos) {
                    classInfos.forEach(c -> {
                        Class<?> clazz = c.getClazz();
                        if (!clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers())) {
                            Service service = clazz.getAnnotation(Service.class);
                            Controller controller = clazz.getAnnotation(Controller.class);
                            RestController restController = clazz.getAnnotation(RestController.class);
                            Component component = clazz.getAnnotation(Component.class);
                            if (null != service || null != component) {
                                ioc.addBean(clazz);
                            } else if (null != controller || null != restController) {
                                ioc.addBean(clazz);
                                routeBuilder.addRouter(clazz);
                            } else if (clazz.getSuperclass().getName().equals("com.blade.aop.AbstractMethodInterceptor")) {
                                aopInterceptors.add(ReflectKit.newInstance(clazz));
                            } else {
                                Class<?>[] interfaces = clazz.getInterfaces();
                                for (Class<?> in : interfaces) {
                                    if (in.equals(Interceptor.class)) {
                                        ioc.addBean(clazz);
                                        routeBuilder.addInterceptor(clazz);
                                    } else if (in.equals(WebContextListener.class)) {
                                        ctxClasses.add(c);
                                    } else if (in.equals(BeanProcessor.class)) {
                                        processoers.add(c);
                                    }
                                }
                            }
                        }
                    });
                }
            });

            ctxClasses.sort(orderComparator);
            processoers.sort(orderComparator);

            ctxClasses.forEach(c -> {
                Object bean = ioc.addBean(c.getClazz());
                ctxs.add((WebContextListener) bean);
            });

            processoers.forEach(c -> {
                Object bean = ioc.addBean(c.getClazz());
                processors.add((BeanProcessor) bean);
            });

            processors.forEach(b -> b.register(ioc));

            if (null != ioc.getBeans() && !ioc.getBeans().isEmpty()) {
                LOGGER.info("Add Object: {}", ioc.getBeans());
            }

            List<BeanDefine> beanDefines = ioc.getBeanDefines();
            if (null != beanDefines) {
                beanDefines.forEach(b -> IocKit.injection(ioc, b));
            }

        }
    }

    public void initCtx(ServletContext sec) {
        ctxs.forEach(c -> c.init(blade.bConfig(), sec));
    }

    public static List<Object> getAopInterceptors() {
        return aopInterceptors;
    }

}