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
package com.blade.web.http.wrapper;

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

import com.blade.Blade;
import com.blade.route.Route;
import com.blade.web.http.HttpMethod;
import com.blade.web.http.Path;
import com.blade.web.http.Request;
import com.blade.web.multipart.FileItem;
import com.blade.web.multipart.Multipart;
import com.blade.web.multipart.MultipartException;
import com.blade.web.multipart.MultipartHandler;

import blade.kit.IOKit;
import blade.kit.ObjectKit;
import blade.kit.StringKit;

/**
 * ServletRequest
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ServletRequest implements Request {
	
	private static final String USER_AGENT = "user-agent";
	
	protected Route route;
	
	private HttpServletRequest request;
	
	protected Map<String,String> pathParams = null;
	
	private Map<String,String> multipartParams = null;

	private List<FileItem> files = null;
	
	private Session session = null;
	
	private Blade blade = null;
	
	private boolean isAbort = false;
	
	public ServletRequest(HttpServletRequest request) throws MultipartException, IOException {
		this.request = request;
		this.pathParams = new HashMap<String,String>();
		this.multipartParams = new HashMap<String,String>();
		this.files = new ArrayList<FileItem>();
		this.blade = Blade.me();
		init();
	}
	
	public ServletRequest init() throws IOException, MultipartException {
		// retrieve multipart/form-data parameters
		if (Multipart.isMultipartContent(request)) {
			Multipart multipart = new Multipart();
			multipart.parse(request, new MultipartHandler() {

				@Override
				public void handleFormItem(String name, String value) {
					multipartParams.put( name, value );
				}

				@Override
				public void handleFileItem(String name, FileItem fileItem) {
					files.add(fileItem);
				}

			});
		}
		return this;
	}
	
	private String join(String[] arr) {
		String ret = "";
		for (String item : arr) {
			ret += "," + item;
		}
		if (ret.length() > 0) {
			ret = ret.substring(1);
		}
		return ret;
	}
	
	@Override
	public void initPathParams(String routePath) {
		pathParams.clear();
		
		List<String> variables = getPathParam(routePath);
		String regexPath = routePath.replaceAll(Path.VAR_REGEXP, Path.VAR_REPLACE);
		
		String uri = Path.getRelativePath(uri(), contextPath());
		
		Matcher matcher = Pattern.compile("(?i)" + regexPath).matcher(uri);
		
		if(matcher.matches()){
			// start index at 1 as group(0) always stands for the entire expression
			for (int i=1, len = variables.size(); i <= len; i++) {
				String value = matcher.group(i);
				pathParams.put(variables.get(i-1), value);
			}
		}
	}
	
	private List<String> getPathParam(String routePath) {
		List<String> variables = new ArrayList<String>();
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
		String val = pathParams.get(name);
		if(null != val && blade.enableXSS()){
			return blade.xss().filter(val);
		}
		return val;
	}
	
	@Override
	public String param(String name, String defaultValue) {
		String val = pathParams.get(name);
		if(null == val){
			val = defaultValue;
		}
		if (null != val && blade.enableXSS()) {
			return blade.xss().filter(val);
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
		params.putAll(multipartParams);
		return Collections.unmodifiableMap(params);
	}

	@Override
	public String query(String name) {
		String[] param = request.getParameterValues(name);
		String val = null;
		if (param != null) {
			val = join(param);
		} else {
			val = multipartParams.get(name);
		}
		if(null != val && blade.enableXSS()){
			return blade.xss().filter(val);
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
			val = multipartParams.get(name);
		}
		if(null == val){
			val = defaultValue;
		}
		if(blade.enableXSS()){
			return blade.xss().filter(val);
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
		Set<String> attrList = new HashSet<String>();
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
		if (request.getHeader("x-requested-with") == null) {
            return false;
        }
        return "XMLHttpRequest".equals(request.getHeader("x-requested-with"));
	}

	@Override
	public Map<String, Cookie> cookies() {
		javax.servlet.http.Cookie[] servletCookies = request.getCookies();

		Map<String,Cookie> cookies = new HashMap<String,Cookie>();
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
		Map<String,String> headers = new HashMap<String,String>();
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
			e.printStackTrace();
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
		for (int i=0; i < files.size(); i++) {
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
					while (line != null) {
						sb.append(line + "\n");
						line = reader.readLine();
					}
					reader.close();
					String data = sb.toString();

					return data;
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public InputStream asInputStream() {
				try {
					return request.getInputStream();
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public byte[] asByte() {
				try {
					return IOKit.toByteArray(request.getInputStream());
				} catch (IOException e) {
					e.printStackTrace();
				}
				return null;
			}
		};
	}

}