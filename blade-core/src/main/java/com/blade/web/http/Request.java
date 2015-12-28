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
package com.blade.web.http;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import com.blade.route.Route;
import com.blade.web.http.wrapper.Session;
import com.blade.web.multipart.FileItem;

/**
 * HTTP Request
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public interface Request {
	
	/**
	 * @return	Return HttpServletRequest
	 */
	HttpServletRequest raw();
	
	/**
	 * URL parameters on the initial route, e.g:/user/23
	 * @param routePath	Route URL
	 */
	void initPathParams(String routePath);
	
	/**
	 * @return	Return client request host
	 */
	String host();

	/**
	 * @return	Return request URL
	 */
	String url();

	/**
	 * @return	Return request URI
	 */
	String path();
	
	/**
	 * @return	Return UA
	 */
	String userAgent();
	
	/**
	 * @return	Return PathInfo
	 */
	String pathInfo();
	
	/**
	 * @return	Return protocol
	 */
	String protocol();
	
	/**
	 * @return	Return servletPath
	 */
	String servletPath();
	
	/**
	 * @return	Return contextPath
	 */
	String contextPath();
	
	/**
	 * @return	Return ServletContext
	 */
	ServletContext context();
	
	/**
	 * @return	Return parameters on the path Map
	 */
	Map<String,String> pathParams();

	/**
	 * Get a URL parameter
	 * @param name	Parameter name  
	 * @return		Return parameter value
	 */
	String param(String name);
	
	/**
	 * Get a URL parameter, and returns defaultValue if it is NULL
	 * @param name			Parameter name  
	 * @param defaultValue	Default Value
	 * @return				Return parameter value
	 */
	String param(String name, String defaultValue);
	
	/**
	 * Return a URL parameter for a Int type
	 * @param name	Parameter name  
	 * @return		Return Int parameter value
	 */
	Integer paramAsInt(String name);
	
	/**
	 * Return a URL parameter for a Long type
	 * @param name	Parameter name  
	 * @return		Return Long parameter value
	 */
	Long paramAsLong(String name);
	
	/**
	 * Return a URL parameter for a Boolean type
	 * @param name	Parameter name  
	 * @return		Return Boolean parameter value
	 */
	Boolean paramAsBool(String name);

	/**
	 * @return	Return query string
	 */
	String queryString();
	
	/**
	 * @return	Return request query Map
	 */
	Map<String,String> querys();

	/**
	 * Get a request parameter
	 * 
	 * @param name	Parameter name
	 * @return		Return request parameter value
	 */
	String query(String name);
	
	/**
	 * Get a request parameter, if NULL is returned to defaultValue
	 * 
	 * @param name			Parameter name
	 * @param defaultValue	Default value
	 * @return				Return request parameter values
	 */
	String query(String name, String defaultValue);
	
	/**
	 * Returns a request parameter for a Int type
	 * 
	 * @param name	Parameter name
	 * @return		Return Int parameter values
	 */
	Integer queryAsInt(String name);
	
	/**
	 * Returns a request parameter for a Long type
	 * 
	 * @param name	Parameter name
	 * @return		Return Long parameter values
	 */
	Long queryAsLong(String name);
	
	/**
	 * Returns a request parameter for a Boolean type
	 * 
	 * @param name	Parameter name
	 * @return		Return Boolean parameter values
	 */
	Boolean queryAsBool(String name);
	
	/**
	 * Returns a request parameter for a Float type
	 * 
	 * @param name	Parameter name
	 * @return		Return Float parameter values
	 */
	Float queryAsFloat(String name);
	
	/**
	 * Returns a request parameter for a Double type
	 * 
	 * @param name	Parameter name
	 * @return		Return Double parameter values
	 */
	Double queryAsDouble(String name);

	/**
	 * @return	Return request method
	 */
	String method();
	
	/**
	 * @return	Return HttpMethod
	 */
	HttpMethod httpMethod();

	/**
	 * @return	Return server remote address
	 */
	String address();
	
	/**
	 * @return	Return current session
	 */
	Session session();
	
	/**
	 * Return to the current or create a session
	 * @param create	create session
	 * @return			Return session
	 */
	Session session(boolean create);
	
	/**
	 * @return	Return contentType
	 */
	String contentType();

	/**
	 * @return	Return Server Port
	 */
	int port();

	/**
	 * @return	Return whether to use the SSL connection
	 */
	boolean isSecure();

	/**
	 * @return	Return current request is a AJAX request
	 */
	boolean isAjax();

	/**
	 * @return	Return Cookie Map
	 */
	Map<String, Cookie> cookies();
	
	/**
	 * Get String Cookie Value
	 * 
	 * @param name	cookie name
	 * @return		Return Cookie Value
	 */
	String cookie(String name);
	
	/**
	 * Get Cookie
	 * 
	 * @param name	cookie name
	 * @return		Return Cookie
	 */
	Cookie cookieRaw(String name);

	/**
	 * @return	Return header information Map
	 */
	Map<String,String> headers();

	/**
	 * Get header information
	 * 
	 * @param name	Parameter name
	 * @return		Return header information
	 */
	String header(String name);
	
	/**
	 * Setting request encoding
	 * 
	 * @param encoding	coded string
	 */
	void encoding(String encoding);
	
	/**
	 * Setting Request Attribute
	 * 
	 * @param name	Parameter name
	 * @param value	Parameter Value
	 */
	void attribute(String name, Object value);
	
	/**
	 * Get a Request Attribute
	 * @param name	Parameter name
	 * @return		Return parameter value
	 */
	<T> T attribute(String name);
	
	/**
	 * @return	Return all Attribute in Request
	 */
	Set<String> attributes();
	
	/**
	 * @return	Return the requested file list
	 */
	FileItem[] files();
	
	/**
	 * @return	Return request body
	 */
	BodyParser body();
	
	/**
	 * Serialized form data, converted to the javabean
	 * 
	 * @param slug		bean slug, e.g: <input name="person.uid" value="123"/>, the slug is person
	 * @param clazz		bean type
	 * @return			Return converted Bean Object
	 */
	<T> T model(String slug, Class<? extends Serializable> clazz);
	
	/**
	 * Setting route, execute request for use
	 * 
	 * @param route	route object
	 */
	void setRoute(Route route);
	
	/**
	 * @return	Return Route of the current request
	 */
	Route route();
	
	/**
	 * Abort current request
	 */
	void abort();
	
	/**
	 * @return	Return is abort request
	 */
	boolean isAbort();
	
	/**
	 * Request body interface
	 * @author biezhi
	 */
	interface BodyParser {
		String asString();
		InputStream asInputStream();
		byte[] asByte();
	}
	
}
