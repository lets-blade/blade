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
package blade.kit;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import blade.kit.log.Logger;

/**
 * properties文件工具类
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class PropertyKit {

	private static final Logger logger = Logger.getLogger(PropertyKit.class);
	
	/**
	 * 根据文件名读取properties文件
	 */
	public static Properties getProperty(String resourceName) {
		InputStream in = null;
		Properties props = new Properties();
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
			if (in != null) {
				props.load(in);
			}
		} catch (IOException e) {
			logger.error("加载属性文件出错！", e);
		} finally {
			IOKit.closeQuietly(in);
		}
		return props;
	}

	/**
	 * //TODO 根据文件名读取map数据
	 */
	public static Map<String, String> getPropertyMap(String resourceName) {
		InputStream in = null;
		Map<String, String> map = CollectionKit.newHashMap();
		try {
			in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
			if (null == in) {
				throw new RuntimeException("没有找到资源文件 [" + resourceName + "]");
			}
			Properties prop = new Properties();
			prop.load(in);
			Set<Entry<Object, Object>> set = prop.entrySet();
			Iterator<Map.Entry<Object, Object>> it = set.iterator();
			while (it.hasNext()) {
				Entry<Object, Object> entry = it.next();
				String key = entry.getKey().toString();
				String value = entry.getValue().toString();
				String fuKey = getWildcard(value);
				if(null != fuKey && null != prop.get(fuKey)){
					String fuValue = prop.get(fuKey).toString();
					value = value.replaceAll("\\$\\{" + fuKey + "\\}", fuValue);
				}
				map.put(key, value);
			}
			logger.info("加载properties文件[" + resourceName + "]");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			IOKit.closeQuietly(in);
		}
		return map;
	}
	
	private static String getWildcard(String str){
		if(null != str && str.indexOf("${") != -1){
			int start = str.indexOf("${");
			int end = str.indexOf("}");
			if(start != -1 && end != -1){
				return str.substring(start + 2, end);
			}
		}
		return null;
	}

	/**
	 * //TODO 根据property对象获取map格式数据
	 */
	public static Map<String, String> getPropertyMap(Properties prop) {
		Map<String, String> map = CollectionKit.newHashMap();
		Set<Entry<Object, Object>> set = prop.entrySet();
		Iterator<Map.Entry<Object, Object>> it = set.iterator();
		while (it.hasNext()) {
			Entry<Object, Object> entry = it.next();
			map.put(entry.getKey().toString(), entry.getValue().toString());
		}
		return map;
	}
}