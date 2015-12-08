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
package com.blade.http;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.blade.route.Route;
import com.blade.servlet.multipart.FileItem;
import com.blade.servlet.wrapper.Session;

/**
 * 
 * <p>
 * HTTP请求对象
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface Request {
	
	/**
	 * @return	返回HttpServletRequest原生对象
	 */
	HttpServletRequest raw();
	
	/**
	 * 初始化路由上的URL参数，如：/user/23
	 * @param routePath	路由URL
	 */
	void initPathParams(String routePath);
	
	/**
	 * @return	返回客户端请求主机
	 */
	String host();

	/**
	 * @return	返回请求URL
	 */
	String url();

	/**
	 * @return	返回请求uri
	 */
	String path();
	
	/**
	 * @return	返回UA
	 */
	String userAgent();
	
	/**
	 * @return	返回PathInfo
	 */
	String pathInfo();
	
	/**
	 * @return	返回协议
	 */
	String protocol();
	
	/**
	 * @return	返回servletPath
	 */
	String servletPath();
	
	/**
	 * @return	返回contextPath
	 */
	String contextPath();
	
	/**
	 * @return	返回上下文对象
	 */
	ServletContext context();
	
	/**
	 * @return	路径上的参数Map
	 */
	Map<String,String> pathParams();

	/**
	 * 获取一个URL上的参数
	 * @param name	参数名
	 * @return		返回参数值
	 */
	String param(String name);
	
	/**
	 * 获取一个URL上的参数，如果为NULL则返回defaultValue
	 * @param name			参数名
	 * @param defaultValue	默认值
	 * @return				返回参数值
	 */
	String param(String name, String defaultValue);
	
	/**
	 * 返回一个Int类型的URL参数
	 * @param name	参数名
	 * @return		返回Int参数值
	 */
	Integer paramAsInt(String name);
	
	/**
	 * 返回一个Long类型的URL参数
	 * @param name	参数名
	 * @return		返回Long参数值
	 */
	Long paramAsLong(String name);
	
	/**
	 * 返回一个Boolean类型的URL参数
	 * @param name	参数名
	 * @return		返回Boolean参数值
	 */
	Boolean paramAsBool(String name);

	/**
	 * @return	返回请求字符串
	 */
	String queryString();
	
	/**
	 * @return	返回请求参数Map
	 */
	Map<String,String> querys();

	/**
	 * 获取一个请求参数
	 * @param name	参数名
	 * @return		返回请求参数值
	 */
	String query(String name);
	
	/**
	 * 获取一个请求参数，如果为NULL则返回defaultValue
	 * @param name			参数名
	 * @param defaultValue	默认返回值
	 * @return				返回请求参数值
	 */
	String query(String name, String defaultValue);
	
	/**
	 * 返回一个Int类型的请求参数
	 * @param name	参数名
	 * @return		返回Int参数值
	 */
	Integer queryAsInt(String name);
	
	/**
	 * 返回一个Long类型的请求参数
	 * @param name	参数名
	 * @return		返回Long参数值
	 */
	Long queryAsLong(String name);
	
	/**
	 * 返回一个Boolean类型的请求参数
	 * @param name	参数名
	 * @return		返回Boolean参数值
	 */
	Boolean queryAsBool(String name);
	
	/**
	 * 返回一个Float类型的请求参数
	 * @param name	参数名
	 * @return		返回Float参数值
	 */
	Float queryAsFloat(String name);
	
	/**
	 * 返回一个Double类型的请求参数
	 * @param name	参数名
	 * @return		返回Double参数值
	 */
	Double queryAsDouble(String name);

	/**
	 * @return	返回请求方法
	 */
	String method();
	
	/**
	 * @return	返回枚举类型的HttpMethod
	 */
	HttpMethod httpMethod();

	/**
	 * @return	返回服务器远程地址
	 */
	String address();
	
	/**
	 * @return	返回当前会话
	 */
	Session session();
	
	/**
	 * 返回当前或创建一个会话
	 * @param create	是否创建会话
	 * @return			返回会话对象
	 */
	Session session(boolean create);
	
	/**
	 * @return	返回contentType
	 */
	String contentType();

	/**
	 * @return	返回服务器端口
	 */
	int port();

	/**
	 * @return	返回是否使用SSL连接
	 */
	boolean isSecure();

	/**
	 * @return	放回当前请求是否是AJAX请求
	 */
	boolean isAjax();

	/**
	 * @return	返回Cookie Map
	 */
	Map<String, Cookie> cookies();
	
	/**
	 * 获取String类型Cookie
	 * @param name	cookie name
	 * @return		返回cookie值
	 */
	String cookie(String name);
	
	/**
	 * 获取Cookie
	 * @param name	cookie name
	 * @return		返回cookie值
	 */
	Cookie cookieRaw(String name);

	/**
	 * @return	返回头信息Map
	 */
	Map<String,String> headers();

	/**
	 * 获取头信息
	 * @param name	参数名
	 * @return		返回头信息
	 */
	String header(String name);
	
	/**
	 * 设置请求编码
	 * @param encoding	编码字符串
	 */
	void encoding(String encoding);
	
	/**
	 * 设置一个RequestAttribute
	 * @param name	参数名
	 * @param value	参数值
	 */
	void attribute(String name, Object value);
	
	/**
	 * 获取一个RequestAttribute
	 * @param name	参数名
	 * @return		返回参数值
	 */
	<T> T attribute(String name);
	
	/**
	 * @return	返回Request中所有Attribute
	 */
	Set<String> attributes();
	
	/**
	 * @return	返回请求中文件列表
	 */
	FileItem[] files();
	
	/**
	 * @return	返回请求体
	 */
	BodyParser body();
	
	/**
	 * 设置路由，执行请求用
	 * @param route	路由对象
	 */
	void setRoute(Route route);
	
	/**
	 * 请求体接口
	 * @author biezhi
	 *
	 */
	interface BodyParser {
		String asString();
		InputStream asInputStream();
		byte[] asByte();
	}
	
}
