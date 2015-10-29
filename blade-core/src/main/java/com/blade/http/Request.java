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
import com.blade.servlet.Session;
import com.blade.servlet.multipart.FileItem;

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
	
	HttpServletRequest raw();
	
	void initPathParams(String routePath);
	
	String host();

	String url();

	String path();
	
	String userAgent();
	
	String pathInfo();
	
	String protocol();
	
	String servletPath();
	
	String contextPath();
	
	ServletContext context();
	
	Map<String,String> pathParams();

	String param(String name);
	
	Integer paramAsInt(String name);
	
	Long paramAsLong(String name);
	
	Boolean paramAsBoolean(String name);

	String queryString();
	
	Map<String,String> querys();

	String query(String name);
	
	Integer queryAsInt(String name);
	
	Long queryAsLong(String name);
	
	Boolean queryAsBoolean(String name);
	
	Float queryAsFloat(String name);
	
	Double queryAsDouble(String name);

	String method();
	
	HttpMethod httpMethod();

	String address();
	
	Session session();
	
	Session session(boolean create);
	
	String contentType();

	int port();

	boolean isSecure();

	boolean isAjax();

	Map<String, Cookie> cookies();
	
	String cookie(String name);
	
	Cookie cookieRaw(String name);

	Map<String,String> headers();

	String header(String name);

	void encoding(String encoding);
	
	void attribute(String name, Object value);
	
	<T> T attribute(String name);
	
	Set<String> attributes();
	
	FileItem[] files();

	BodyParser body();
	
	void setRoute(Route route);
	
	interface BodyParser {
		String asString();
		InputStream asInputStream();
		byte[] asByte();
	}
	
}
