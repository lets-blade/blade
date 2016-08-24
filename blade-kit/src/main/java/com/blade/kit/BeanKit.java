/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.kit;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * java对象和map转换
 * @author:rex
 * @date:2014年9月17日
 * @version:1.0
 */
public class BeanKit {

	// Map --> Bean 1: 利用Introspector,PropertyDescriptor实现 Map --> Bean  
	
	public static <T> T mapToBean(Map<String, Object> map, Class<T> clazz){
		T object = null;
		try {
			object = clazz.newInstance();
			BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();

			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				if (map.containsKey(key)) {
					Object value = map.get(key);
					// 得到property对应的setter方法  

					Method setter = property.getWriteMethod();
					setter.invoke(object, value);
				}
			}
		} catch (Exception e) {
			System.out.println("map covert to bean error：" + e);
		}
		return object;

	}

	// Bean --> Map 1: 利用Introspector和PropertyDescriptor 将Bean --> Map  
	public static <T> Map<String, Object> beanToMap(T obj) {

		if (obj == null) {
			return null;
		}
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(obj.getClass());
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (PropertyDescriptor property : propertyDescriptors) {
				String key = property.getName();

				// 过滤class属性  

				if (!key.equals("class")) {
					// 得到property对应的getter方法  

					Method getter = property.getReadMethod();
					Object value = getter.invoke(obj);
					if(null != value){
						map.put(key, value);
					}
				}

			}
		} catch (Exception e) {
			System.out.println("bean covert to map error：" + e);
		}
		return map;

	}
	
	public static <T> List<Map<String, Object>> toListMap(List<T> list) {
		
		if(null == list || list.size() == 0){
			return null;
		}
		
		List<Map<String, Object>> result = CollectionKit.newArrayList(list.size());
		for(T t : list){
			Map<String, Object> map = beanToMap(t);
			result.add(map);
		}
		
		return result;
	}
	
}