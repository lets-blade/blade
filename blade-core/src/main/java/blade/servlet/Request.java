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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import blade.kit.IOKit;
import blade.kit.PathKit;
import blade.kit.StringKit;
import blade.kit.log.Logger;
import blade.route.RouteMatcher;

/**
 * HttpServletRequest请求包装类
 * <p>
 * 提供对HttpServletRequest API的简单操作
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Request {

    private static final Logger LOGGER = Logger.getLogger(Request.class);

    private static final String USER_AGENT = "user-agent";
    
    private Map<String, String> pathParams;
    private List<String> splat;
    private QueryParamsMap queryMap;

    private HttpServletRequest servletRequest;

    private Session session = null;
    
    private String body = null;
    private byte[] bodyAsBytes = null;
    
    private Set<String> headers = null;
    
    protected Request() {
    }

    /**
     * 构造一个Request对象
     * 
     * @param match		路由匹配对象，用于存储URL参数等信息
     * @param request	HttpServletRequest请求对象
     */
    public Request(RouteMatcher match, HttpServletRequest request) {
        this.servletRequest = request;
        initRequest(match);
    }

    /**
     * 初始化Request
     * 
     * @param match		路由匹配对象，用于存储URL参数等信息
     */
    public void initRequest(RouteMatcher match) {
    	
        List<String> requestList = PathKit.convertRouteToList(match.getRequestURI());
        List<String> pathList = PathKit.convertRouteToList(match.getPath());
        
        this.pathParams = getPathParams(requestList, pathList);
        this.splat = getSplat(requestList, pathList);
        
    }

    /**
     * @return	返回URL路径上的所有参数
     */
    public Map<String, String> pathParams() {
        return Collections.unmodifiableMap(this.pathParams);
    }

    /**
     * 返回在URL路径上的参数值，如：/users/:name
     * @param param		参数名称
     * @return			返回URL上对应的String参数值
     */
    public String pathParam(String param) {
        if (param == null) {
            return null;
        }
        
        if (param.startsWith(":")) {
            return this.pathParams.get(param.toLowerCase());
        } else {
            return this.pathParams.get(":" + param.toLowerCase());
        }
    }
    
    /**
     * 返回int类型的path param
     * @param param		参数名称
     * @return			返回URL上对应的Integer参数值
     */
    public Integer pathParamToInt(String param) {
        String value = pathParam(param);
        if(null != value){
        	return Integer.valueOf(value);
        }
        return null;
    }
    
    /**
     * @return	返回通配符
     */
    public String[] splat() {
        return splat.toArray(new String[splat.size()]);
    }

    /**
     * @return 返回请求method 如：GET, POST, PUT, ...
     */
    public String requestMethod() {
        return servletRequest.getMethod();
    }

    /**
     * @return 返回请求scheme
     */
    public String scheme() {
        return servletRequest.getScheme();
    }

    /**
     * @return 返回主机名称
     */
    public String host() {
        return servletRequest.getHeader("host");
    }

    /**
     * @return 返回请求UA
     */
    public String userAgent() {
        return servletRequest.getHeader(USER_AGENT);
    }

    /**
     * @return 返回服务端口
     */
    public int port() {
        return servletRequest.getServerPort();
    }


    /**
     * @return 返回pathinfo 如："/example/foo"
     */
    public String pathInfo() {
        return servletRequest.getPathInfo();
    }

    /**
     * @return 返回servletPath
     */
    public String servletPath() {
        return servletRequest.getServletPath();
    }

    /**
     * @return 返回contextpath
     */
    public String contextPath() {
        return servletRequest.getContextPath();
    }

    /**
     * @return 返回url
     */
    public String url() {
        return servletRequest.getRequestURL().toString();
    }

    /**
     * @return 返回contentType
     */
    public String contentType() {
        return servletRequest.getContentType();
    }

    /**
     * @return 返回客户端IP
     */
    public String ip() {
        return servletRequest.getRemoteAddr();
    }

    /**
     * @return 返回请求body
     */
    public String body() {
        if (body == null) {
            readBody();
        }
        return body;
    }
    
    /**
     * @return	返回转换为字节数组的请求body
     */
    public byte[] bodyAsBytes() {
        if (bodyAsBytes == null) {
            readBody();
        }
        return bodyAsBytes;
    }
    
    private void readBody() {
		try {
			bodyAsBytes = IOKit.toByteArray(servletRequest.getInputStream());
			body = new String(bodyAsBytes);
		} catch (Exception e) {
			LOGGER.warn("Exception when reading body", e);
		}
	}

    /**
     * @return 返回请求body的长度
     */
    public int contentLength() {
        return servletRequest.getContentLength();
    }

    /**
     * 获取query参数
     * 
     * @param queryParam	查询键
     * @return				返回查询到的value
     */
    public String query(String queryParam) {
        return servletRequest.getParameter(queryParam);
    }
    
    /**
     * 获取一个数组类型的query
     * 
     * @param queryParam	查询键
     * @return				返回查询到的value数组
     */
    public String[] querys(String queryParam) {
        return servletRequest.getParameterValues(queryParam);
    }
    
    /**
     * 获取query参数冰转换为Integer类型
     * @param queryParam	查询键
     * @return				返回查询到的int value
     */
    public Integer queryToInt(String queryParam) {
        String value = query(queryParam);
        if(StringKit.isNotEmpty(value)){
        	return Integer.valueOf(value);
        }
        return null;
    }
    
    /**
     * 获取query参数冰转换为Long类型
     * @param queryParam	查询键
     * @return				返回查询到的long value
     */
    public Long queryToLong(String queryParam) {
        String value = query(queryParam);
        if(StringKit.isNotEmpty(value)){
        	return Long.valueOf(value);
        }
        return null;
    }

    /**
     * 获取query参数冰转换为Boolean类型
     * @param queryParam	查询键
     * @return				返回查询到的boolean value
     */
    public Boolean queryToBoolean(String queryParam) {
        String value = query(queryParam);
        if(StringKit.isNotEmpty(value)){
        	return Boolean.valueOf(value);
        }
        return null;
    }
    
    /**
     * 获取query参数冰转换为Double类型
     * @param queryParam	查询键	
     * @return				返回查询到的double value
     */
    public Double queryToDouble(String queryParam) {
        String value = query(queryParam);
        if(StringKit.isNotEmpty(value)){
        	return Double.valueOf(value);
        }
        return null;
    }
    
    /**
     * 获取query参数并转换为Float类型
     * @param queryParam	查询键
     * @return				返回查询到的float value
     */
    public Float queryToFloat(String queryParam) {
        String value = query(queryParam);
        if(StringKit.isNotEmpty(value)){
        	return Float.valueOf(value);
        }
        return null;
    }
    
    /**
     * 获取头信息
     * 
     * @param header		要查找的请求头
     * @return				返回请求头信息
     */
    public String header(String header) {
        return servletRequest.getHeader(header);
    }

    /**
     * @return 返回查询参数集合
     */
    public Set<String> querys() {
        return servletRequest.getParameterMap().keySet();
    }

    /**
     * @return 返回所有头信息
     */
    public Set<String> headers() {
        if (headers == null) {
            headers = new TreeSet<String>();
            Enumeration<String> enumeration = servletRequest.getHeaderNames();
            while (enumeration.hasMoreElements()) {
                headers.add(enumeration.nextElement());
            }
        }
        return headers;
    }

    /**
     * @return 返回查询字符串
     */
    public String queryString() {
        return servletRequest.getQueryString();
    }

    /**
     * 设置request属性
     * 
     * @param attribute		属性名称
     * @param value			属性值
     */
    public void attribute(String attribute, Object value) {
        servletRequest.setAttribute(attribute, value);
    }

    /**
     * 获取request属性
     * 
     * @param attribute		属性名称
     * @return				返回属性值
     */
    public Object attribute(String attribute) {
        return servletRequest.getAttribute(attribute);
    }


    /**
     * @return 返回所有request属性
     */
    public Set<String> attributes() {
        Set<String> attrList = new HashSet<String>();
        Enumeration<String> attributes = (Enumeration<String>) servletRequest.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
    }

    /**
     * @return 返回原生HttpServletRequest对象
     */
    public HttpServletRequest servletRequest() {
        return servletRequest;
    }

    /**
     * @return 返回查询map
     */
    public QueryParamsMap queryMap() {
        initQueryMap();

        return queryMap;
    }

    /**
     * @param key the key
     * @return the query map
     */
    public QueryParamsMap queryMap(String key) {
        return queryMap().get(key);
    }

    private void initQueryMap() {
        if (queryMap == null) {
            queryMap = new QueryParamsMap(servletRequest());
        }
    }

    /**
     * @return	返回会话对象
     */
    public Session session() {
        if (session == null) {
            session = new Session(servletRequest.getSession());
        }
        return session;
    }

    /**
     * 返回会话对象，如果不存在则创建一个
     * @param create	true：创建，false：不创建
     * @return			返回一个会话对象
     */
    public Session session(boolean create) {
        if (session == null) {
            HttpSession httpSession = servletRequest.getSession(create);
            if (httpSession != null) {
                session = new Session(httpSession);
            }
        }
        return session;
    }

    /**
     * @return 返回cookies
     */
    public Map<String, String> cookies() {
        Map<String, String> result = new HashMap<String, String>();
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                result.put(cookie.getName(), cookie.getValue());
            }
        }
        return result;
    }

    /**
     * 根据cookie名称获取cookie
     *
     * @param name 		cookie名称
     * @return 			返回cookie值
     */
    public String cookie(String name) {
        Cookie[] cookies = servletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * @return	返回uri
     */
    public String uri() {
        return servletRequest.getRequestURI();
    }

    /**
     * @return	返回请求协议
     */
    public String protocol() {
        return servletRequest.getProtocol();
    }
    
    private static Map<String, String> getPathParams(List<String> request, List<String> matched) {
    	
        Map<String, String> params = new HashMap<String, String>();

        for (int i = 0; (i < request.size()) && (i < matched.size()); i++) {
            String matchedPart = matched.get(i);
            if (PathKit.isParam(matchedPart)) {
                LOGGER.debug("matchedPart: "
                                  + matchedPart
                                  + " = "
                                  + request.get(i));
                params.put(matchedPart.toLowerCase(), request.get(i));
            }
        }
        return Collections.unmodifiableMap(params);
    }

    private static List<String> getSplat(List<String> request, List<String> matched) {
    	
        int nbrOfRequestParts = request.size();
        int nbrOfMatchedParts = matched.size();

        boolean sameLength = (nbrOfRequestParts == nbrOfMatchedParts);

        List<String> splat = new ArrayList<String>();

        for (int i = 0; (i < nbrOfRequestParts) && (i < nbrOfMatchedParts); i++) {
            String matchedPart = matched.get(i);

            if (PathKit.isSplat(matchedPart)) {

                StringBuilder splatParam = new StringBuilder(request.get(i));
                if (!sameLength && (i == (nbrOfMatchedParts - 1))) {
                    for (int j = i + 1; j < nbrOfRequestParts; j++) {
                        splatParam.append("/");
                        splatParam.append(request.get(j));
                    }
                }
                splat.add(splatParam.toString());
            }
        }
        return Collections.unmodifiableList(splat);
    }

}
