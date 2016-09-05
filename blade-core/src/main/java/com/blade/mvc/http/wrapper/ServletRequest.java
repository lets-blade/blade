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
package com.blade.mvc.http.wrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.kit.IOKit;
import com.blade.kit.ObjectKit;
import com.blade.kit.StringKit;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Path;
import com.blade.mvc.http.Request;
import com.blade.mvc.multipart.FileItem;
import com.blade.mvc.multipart.Multipart;
import com.blade.mvc.multipart.MultipartException;
import com.blade.mvc.multipart.MultipartHandler;
import com.blade.mvc.route.Route;

/**
 * ServletRequest
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.5
 */
public class ServletRequest implements Request {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ServletRequest.class);
	
	private static final String USER_AGENT = "user-agent";
	
	protected Route route;
	
	private HttpServletRequest request;
	
	// path parameter eg: /user/12
	private Map<String,String> pathParams = null;
	
	// query parameter eg: /user?name=jack
	private Map<String,String> queryParams = null;

	private List<FileItem> files = null;
	
	private Session session = null;
	
	private boolean isAbort = false;
	
	public ServletRequest(HttpServletRequest request) throws MultipartException, IOException {
		this.request = request;
		this.pathParams = new HashMap<String,String>(8);
		this.queryParams = new HashMap<String,String>(16);
		this.files = new ArrayList<FileItem>(8);
		this.init();
	}
	
	public void init() throws IOException, MultipartException {
		// retrieve multipart/form-data parameters
		if (Multipart.isMultipartContent(request)) {
			Multipart multipart = new Multipart();
			multipart.parse(request, new MultipartHandler() {
				@Override
				public void handleFormItem(String name, String value) {
					queryParams.put( name, value );
				}
				@Override
				public void handleFileItem(String name, FileItem fileItem) {
					files.add(fileItem);
				}
			});
		}
	}
	
	private String join(String[] arr) {
		StringBuffer ret = new StringBuffer();
		for (String item : arr) {
			ret.append(',').append(item);
		}
		if (ret.length() > 0) {
			return ret.substring(1);
		}
		return ret.toString();
	}
	
	@Override
	public void initPathParams(String routePath) {
		this.pathParams.clear();
		
		List<String> variables = getPathParam(routePath);
		String regexPath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
		
		String uri = Path.getRelativePath(uri(), contextPath());
		
		Matcher matcher = Pattern.compile("(?i)" + regexPath).matcher(uri);
		
		if(matcher.matches()){
			// start index at 1 as group(0) always stands for the entire expression
			for (int i=1, len = variables.size(); i <= len; i++) {
				String value = matcher.group(i);
				this.pathParams.put(variables.get(i-1), value);
			}
		}
	}
	
	private List<String> getPathParam(String routePath) {
		List<String> variables = new ArrayList<String>(8);
		Matcher matcher = Pattern.compile(Path.VAR_REGEXP).matcher(routePath);
		while (matcher.find()) {
			variables.add(matcher.group(1));
		}
		return variables;
	}
	
	@Override
	public HttpServletRequest raw() {
		return request;
	}
	
	@Override
	public String host() {
		return request.getServerName();
	}

	@Override
	public String url() {
		return request.getRequestURL().toString();
	}

	@Override
	public String uri() {
		return Path.fixPath(request.getRequestURI());
	}
	
	@Override
	public String userAgent() {
		return request.getHeader(USER_AGENT);
	}
	
	@Override
	public String pathInfo() {
		return request.getPathInfo();
	}
	
	@Override
	public String protocol() {
		return request.getProtocol();
	}
	
	@Override
	public String servletPath() {
		return request.getServletPath();
	}
	
	@Override
	public String contextPath() {
		return request.getContextPath();
	}
	
	@Override
	public ServletContext context() {
		return request.getServletContext();
	}
	
	@Override
	public Map<String, String> pathParams() {
		return pathParams;
	}

	@Override
	public String param(String name) {
		return pathParams.get(name);
	}
	
	@Override
	public String param(String name, String defaultValue) {
		String val = pathParams.get(name);
		if(null == val){
			val = defaultValue;
		}
		return val;
	}

	@Override
	public Integer paramAsInt(String name) {
		String value = param(name);
		if (StringKit.isNotBlank(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

	@Override
	public Long paramAsLong(String name) {
		String value = param(name);
		if (StringKit.isNotBlank(value)) {
			return Long.parseLong(value);
		}
		return null;
	}

	@Override
	public Boolean paramAsBool(String name) {
		String value = param(name);
		if (StringKit.isNotBlank(value)) {
			return Boolean.parseBoolean(value);
		}
		return null;
	}

	@Override
	public String queryString() {
		return request.getQueryString();
	}

	@Override
	public Map<String, String> querys() {
		Map<String,String> params = new HashMap<String,String>();

		Map<String,String[]> requestParams = request.getParameterMap();
		for (Map.Entry<String,String[]> entry : requestParams.entrySet()) {
			params.put( entry.getKey(), join(entry.getValue()) );
		}
		params.putAll(queryParams);
		return Collections.unmodifiableMap(params);
	}

	@Override
	public String query(String name) {
		String[] param = request.getParameterValues(name);
		String val = null;
		if (param != null) {
			val = join(param);
		} else {
			val = queryParams.get(name);
		}
		return val;
	}
	
	@Override
	public String query(String name, String defaultValue) {
		String[] param = request.getParameterValues(name);
		String val = null;
		if (param != null) {
			val = join(param);
		} else {
			val = queryParams.get(name);
		}
		if(null == val){
			val = defaultValue;
		}
		return val;
	}

	@Override
	public Integer queryAsInt(String name) {
		String value = query(name);
		if (StringKit.isNotBlank(value) && StringKit.isNumber(value)) {
			return Integer.parseInt(value);
		}
		return null;
	}

	@Override
	public Long queryAsLong(String name) {
		String value = query(name);
		if (StringKit.isNotBlank(value) && StringKit.isNumber(value)) {
			return Long.parseLong(value);
		}
		return null;
	}

	@Override
	public Boolean queryAsBool(String name) {
		String value = query(name);
		if (StringKit.isNotBlank(value) && StringKit.isBoolean(value)) {
			return Boolean.parseBoolean(value);
		}
		return null;
	}

	@Override
	public Float queryAsFloat(String name) {
		String value = query(name);
		if (StringKit.isNotBlank(value)) {
			try {
				return Float.parseFloat(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public Double queryAsDouble(String name) {
		String value = query(name);
		if (StringKit.isNotBlank(value)) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
			}
		}
		return null;
	}

	@Override
	public String method() {
		return request.getMethod();
	}
	
	@Override
	public HttpMethod httpMethod() {
		return HttpMethod.valueOf(request.getMethod().toUpperCase());
	}

	@Override
	public String address() {
		return request.getRemoteAddr();
	}

	@Override
	public Session session() {
		if (session == null) {
            session = new Session(request.getSession());
        }
        return session;
	}
	
	@Override
	public Session session(boolean create) {
		if (session == null) {
            HttpSession httpSession = request.getSession(create);
            if (httpSession != null) {
                session = new Session(httpSession);
            }
        }
        return session;
	}

	@Override
	public void attribute(String name, Object value) {
		request.setAttribute(name, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T attribute(String name) {
		Object object = request.getAttribute(name);
		if(null != object){
			return (T) object;
		}
		return null;
	}

	@Override
	public Set<String> attributes() {
		Set<String> attrList = new HashSet<String>(8);
        Enumeration<String> attributes = (Enumeration<String>) request.getAttributeNames();
        while (attributes.hasMoreElements()) {
            attrList.add(attributes.nextElement());
        }
        return attrList;
	}

	@Override
	public String contentType() {
		return request.getContentType();
	}

	@Override
	public int port() {
		return request.getServerPort();
	}

	@Override
	public boolean isSecure() {
		return request.isSecure();
	}

	@Override
	public boolean isAjax() {
		if (null == header("x-requested-with")) {
            return false;
        }
        return "XMLHttpRequest".equals(header("x-requested-with"));
	}

	@Override
	public Map<String, Cookie> cookies() {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		Map<String,Cookie> cookies = new HashMap<String,Cookie>(8);
		for (javax.servlet.http.Cookie c : servletCookies) {
			cookies.put( c.getName(), map(c) );
		}
		return Collections.unmodifiableMap(cookies);
	}
	
	private Cookie map(Cookie servletCookie) {
		Cookie cookie = new Cookie(servletCookie.getName(), servletCookie.getValue());
		cookie.setMaxAge(servletCookie.getMaxAge());
		cookie.setHttpOnly(servletCookie.isHttpOnly());
		String path = servletCookie.getPath();
		if(null != path){
			cookie.setPath(path);
		}
		String domain = servletCookie.getDomain();
		if(null != domain){
			cookie.setDomain(domain);
		}
		cookie.setSecure(servletCookie.getSecure());
		return cookie;
	}
	
	@Override
	public String cookie(String name) {
		Cookie cookie = cookieRaw(name);
		if(null != cookie){
			return cookie.getValue();
		}
		return null;
	}
	
	@Override
	public Cookie cookieRaw(String name) {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();
		if (servletCookies == null) {
			return null;
		}
		for (javax.servlet.http.Cookie c : servletCookies) {
			if (c.getName().equals(name)) {
				return map(c);
			}
		}
		return null;
	}

	@Override
	public Map<String, String> headers() {
		Enumeration<String> servletHeaders = request.getHeaderNames();
		Map<String,String> headers = new HashMap<String,String>(16);
		while(servletHeaders.hasMoreElements()) {
			String headerName = servletHeaders.nextElement();
			headers.put(headerName, request.getHeader(headerName));
		}
		return headers;
	}

	@Override
	public String header(String name) {
		return request.getHeader(name);
	}

	@Override
	public void encoding(String encoding) {
		try {
			request.setCharacterEncoding(encoding);
		} catch (UnsupportedEncodingException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void setRoute(Route route) {
		this.route = route;
		initPathParams(route.getPath());
	}
	
	@Override
	public Route route() {
		return this.route;
	}

	@Override
	public void abort() {
		this.isAbort = true;
	}
	
	@Override
	public boolean isAbort() {
		 return this.isAbort;
	}
	
	@Override
	public <T> T model(String slug, Class<? extends Serializable> clazz) {
		if(StringKit.isNotBlank(slug) && null != clazz){
			return ObjectKit.model(slug, clazz, querys());
		}
		return null;
	}
	
	@Override
	public FileItem[] files() {
		FileItem[] fileParts = new FileItem[files.size()];
		for (int i=0, len=files.size(); i < len; i++) {
			fileParts[i] = files.get(i);
		}
		return fileParts;
	}

	@Override
	public BodyParser body() {
		return new BodyParser() {
			@Override
			public String asString() {
				try {
					BufferedReader reader = new BufferedReader( new InputStreamReader(request.getInputStream()) );
					StringBuilder sb = new StringBuilder();
					String line = reader.readLine();
					while (null != line) {
						sb.append(line + "\r\n");
						line = reader.readLine();
					}
					reader.close();
					return sb.toString();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			}
			
			@Override
			public InputStream asInputStream() {
				try {
					return request.getInputStream();
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			}

			@Override
			public byte[] asByte() {
				try {
					return IOKit.toByteArray(request.getInputStream());
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
				return null;
			}
		};
	}
	
}