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
package blade.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

/**
 * 查询参数封装
 * <p>
 *	封装了URL上的查询参数和Path参数 
 * </p>
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class QueryParamsMap {

	/**
	 * 一个空的参数查询map
	 */
    private static final QueryParamsMap NULL = new QueryParamsMap();

    /**
     * 查询参数字典，存放url上传输的参数
     */
    private Map<String, QueryParamsMap> queryMap = new HashMap<String, QueryParamsMap>();

    /**
     * 所有的值数组
     */
    private String[] values;
    
    private Pattern p = Pattern.compile("\\A[\\[\\]]*([^\\[\\]]+)\\]*");

    /**
     * 根据request创建一个QueryParamsMap对象
     * 从request.getParameterMap()方法解析参数
     * 
     * @param request	HttpServletRequest请求对象
     */
    public QueryParamsMap(HttpServletRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("HttpServletRequest cannot be null.");
        }
        loadQueryString(request.getParameterMap());
    }

    /**
     * 并没有什么卵用 _(:з」∠)_
     */
    private QueryParamsMap() {
    }

    
    /**
     * 解析和创建键和值，一键多值
     * 
     * @param key		形如: user[info][name]
     * @param values	所有值
     */
    protected QueryParamsMap(String key, String... values) {
        loadKeys(key, values);
    }

    /**
     * 构造一个查询参数对象
     * 
     * @param params	要加载进的所有参数
     */
    protected QueryParamsMap(Map<String, String[]> params) {
        loadQueryString(params);
    }

    /**
     * 加载查询参数
     * 
     * @param params	要加载进的所有参数
     */
    protected final void loadQueryString(Map<String, String[]> params) {
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            loadKeys(param.getKey(), param.getValue());
        }
    }

    /**
     * 加载所有key
     * 
     * @param key		要加载的键
     * @param value		家加载的值
     */
    protected final void loadKeys(String key, String[] value) {
        String[] parsed = parseKey(key);

        if (parsed == null) {
            return;
        }

        if (!queryMap.containsKey(parsed[0])) {
            queryMap.put(parsed[0], new QueryParamsMap());
        }
        if (!parsed[1].isEmpty()) {
            queryMap.get(parsed[0]).loadKeys(parsed[1], value);
        } else {
            queryMap.get(parsed[0]).values = value.clone();
        }
    }

    protected final String[] parseKey(String key) {
        Matcher m = p.matcher(key);

        if (m.find()) {
            return new String[] {cleanKey(m.group()), key.substring(m.end())};
        } else {
            return null;
        }
    }
    
    protected static final String cleanKey(String group) {
        if (group.startsWith("[")) {
            return group.substring(1, group.length() - 1);
        } else {
            return group;
        }
    }

    /**
     * 根据key返回QueryParamsMap
     * 注入：user[name]=fede
     * 获取：get("user").get("name").value() || get("user","name").value()
     * 
     * @param keys		键列表
     * @return			返回一个查询参数map
     */
    public QueryParamsMap get(String... keys) {
        QueryParamsMap ret = this;
        for (String key : keys) {
            if (ret.queryMap.containsKey(key)) {
                ret = ret.queryMap.get(key);
            } else {
                ret = NULL;
            }
        }
        return ret;
    }

    /**
     * @return	返回键的值
     */
    public String value() {
        if (hasValue()) {
            return values[0];
        } else {
            return null;
        }
    }

    /**
     * 根据传入的keys获取值
     * 
     * @param keys	keys
     * @return		返回键的值
     */
    public String value(String... keys) {
        return get(keys).value();
    }
    
    /**
     * @return 是否包含该key
     */
    public boolean hasKeys() {
        return !this.queryMap.isEmpty();
    }

    /**
     * @return 是否包含value
     */
    public boolean hasValue() {
        return this.values != null && this.values.length > 0;
    }

    /**
     * @return 返回Boolean类型值
     */
    public Boolean booleanValue() {
        return hasValue() ? Boolean.valueOf(value()) : null;
    }

    /**
     * @return Integer类型的值
     */
    public Integer integerValue() {
        return hasValue() ? Integer.valueOf(value()) : null;
    }

    /**
     * @return 返回Long类型值
     */
    public Long longValue() {
        return hasValue() ? Long.valueOf(value()) : null;
    }

    /**
     * @return 返回Float类型值
     */
    public Float floatValue() {
        return hasValue() ? Float.valueOf(value()) : null;
    }

    /**
     * @return 返回Double类型值
     */
    public Double doubleValue() {
        return hasValue() ? Double.valueOf(value()) : null;
    }

    /**
     * @return 返回values
     */
    public String[] values() {
        return this.values.clone();
    }

    /**
     * @return 返回queryMap
     */
    Map<String, QueryParamsMap> getQueryMap() {
        return queryMap;
    }

    /**
     * @return 返回values
     */
    String[] getValues() {
        return values;
    }
    
    /**
     * @return 将queryMap转换为map
     */
    public Map<String, String[]> toMap() {
        Map<String, String[]> map = new HashMap<String, String[]>();

        for (Entry<String, QueryParamsMap> key : this.queryMap.entrySet()) {
            map.put(key.getKey(), key.getValue().values);
        }

        return map;
    }
}
