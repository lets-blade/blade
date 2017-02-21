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
package com.blade.ioc;

import com.blade.ioc.loader.IocLoader;

import java.util.List;
import java.util.Set;

/**
 * IOC container, it provides an interface for registration and bean.
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
 */
public interface Ioc {

	void load(IocLoader iocLoader);

	void addBean(Object bean);

	Object addBean(Class<?> type);

	void addBean(String name, Object bean);

	void setBean(Class<?> type, Object proxyBean);

	Object getBean(String name);

    <T> T getBean(Class<T> type);

    List<BeanDefine> getBeanDefines();

	List<Object> getBeans();

	Set<String> getBeanNames();

	void clearAll();

}
