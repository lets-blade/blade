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
package com.blade.loader;

import blade.kit.IOKit;
import com.blade.route.Route;
import com.blade.route.RouteException;
import com.blade.route.RouteGroup;
import com.blade.web.http.HttpMethod;
import com.blade.web.http.Request;
import com.blade.web.http.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.StringTokenizer;

/**
 * Abstract loader implementation 
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class AbstractFileRouteLoader implements RouteLoader {

	private ControllerLoader controllerLoader = new ClassPathControllerLoader();

	protected abstract InputStream getInputStream() throws Exception;
	
	@Override
	public List<Route> load() throws ParseException, RouteException {
		InputStream inputStream = null;
		try {
			inputStream = getInputStream();
		} catch (Exception e) {
			throw new RouteException("Loading the route config file error: " + e.getMessage(), e);
		}
		try {
			return load(inputStream);
		} catch (IOException e) {
			throw new RouteException("Loading the route config file error: " + e.getMessage(), e);
		}
	}

	/**
	 * Load Route
	 * 
	 * @param inputStream		route inputstream 
	 * @return					return route list 
	 * @throws ParseException	parse exception
	 * @throws IOException		io exception
	 */
	private List<Route> load(InputStream inputStream) throws ParseException, IOException {
		int line = 0;
		List<Route> routes = new ArrayList<Route>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			String input;
			Stack<Group> groups = new Stack<Group>();
			while ( (input = in.readLine()) != null ) {
				line++;

				input = input.trim();

				if (!input.equals("") && !input.startsWith(".")) {
					if (input.startsWith("GROUP")) {
						groups.push(parseGroup(input, line, groups.isEmpty() ? null : groups.peek()));
					} else if (input.startsWith("END")) {
						if (!groups.isEmpty()) {
							groups.pop();
						}
					} else {
						Route route = parse(input, line, groups.isEmpty() ? null : groups.peek());
						routes.add(route);
					}
				}
			}
		} finally {
			IOKit.closeQuietly(in);
		}
		return routes;
	}

	protected Group parseGroup(String input, int line, Group parent) throws ParseException {
		StringTokenizer st = new StringTokenizer(input, " \t");
		if (st.countTokens() != 3 && (st.countTokens() != 2 || parent == null)) {
			throw new ParseException("Unrecognized format", line);
		}

		//"GROUP" do nothing
		st.nextToken();

		String path;
		String controllerName;
		if (parent != null) {
			path = RouteGroup.formatPath(parent.getPath(), st.nextToken().trim());
		} else {
			path = validatePath( st.nextToken().trim(), line );
		}
		if (st.hasMoreTokens()) {
			controllerName = st.nextToken().trim();
		} else {
			controllerName = parent.getControllerName();
		}
		return new Group(path, controllerName);
	}

	private class Group {
		private String path;

		private String controllerName;

		public Group(String path, String controllerName) {
			this.path = path;
			this.controllerName = controllerName;
		}

		public String getPath() {
			return path;
		}

		public String getControllerName() {
			return controllerName;
		}

	}

	private Route parse(String input, int line, Group group) throws ParseException {
		StringTokenizer st = new StringTokenizer(input, " \t");
		if (st.countTokens() != 3) {
			throw new ParseException("Unrecognized format", line);
		}

		// Verify HTTP request 
		String httpMethod = validateHttpMethod( st.nextToken().trim(), line );
		
		String path = st.nextToken().trim();
		if (group == null) {
			path = validatePath(path, line);

			String controllerAndMethod = validateControllerAndMethod(st.nextToken().trim(), line);

			int hashPos = controllerAndMethod.indexOf(".");

			String controllerName = controllerAndMethod.substring(0, hashPos);

			// Acquisition controller method
			String controllerMethod = controllerAndMethod.substring(hashPos + 1);

			return buildRoute(httpMethod, path, controllerName, controllerMethod);
		} else {
			path = validatePath(RouteGroup.formatPath(group.getPath(), path), line);

			String controllerAndMethod = st.nextToken().trim();

			int hashPos = controllerAndMethod.indexOf(".");

			if (hashPos == -1) {
				return buildRoute(httpMethod, path, group.getControllerName(), controllerAndMethod);
			} else {
				String controllerName = controllerAndMethod.substring(0, hashPos);

				// Acquisition controller method
				String controllerMethod = controllerAndMethod.substring(hashPos + 1);

				return buildRoute(httpMethod, path, controllerName, controllerMethod);
			}

		}
	}

	private String validateHttpMethod(String httpMethod, int line) throws ParseException {
		if (!httpMethod.equalsIgnoreCase("GET") &&
				!httpMethod.equalsIgnoreCase("POST") &&
				!httpMethod.equalsIgnoreCase("PUT") &&
				!httpMethod.equalsIgnoreCase("DELETE")) {
			throw new ParseException("Unrecognized HTTP method: " + httpMethod, line);
		}
		return httpMethod;
	}

	private String validatePath(String path, int line) throws ParseException {
		if (!path.startsWith("/")) {
			throw new ParseException("Path must start with '/'", line);
		}
		
		boolean openedKey = false;
		for (int i=0; i < path.length(); i++) {

			boolean validChar = isValidCharForPath(path.charAt(i), openedKey);
			if (!validChar) {
				throw new ParseException(path, i);
			}

			if (path.charAt(i) == '{') {
				openedKey = true;
			}

			if (path.charAt(i) == '}') {
				openedKey = false;
			}
		}
		return path;
	}
	
	private boolean isValidCharForPath(char c, boolean openedKey) {
		char[] invalidChars = { '?', '.', ' ' };
		for (char invalidChar : invalidChars) {
			if (c == invalidChar) {
				return false;
			}
		}

		if (openedKey) {
			char[] moreInvalidChars = { '/', '{' };
			for (char invalidChar : moreInvalidChars) {
				if (c == invalidChar) {
					return false;
				}
			}
		}
		
		return true;
	}

	/**
	 * Verification controller method 
	 * 
	 * @param beanAndMethod		controller and method, using. 
	 * @param line				line number
	 * @return					return a string that is verified after the verification. 
	 * @throws ParseException
	 */
	private String validateControllerAndMethod(String beanAndMethod, int line) throws ParseException {
		int hashPos = beanAndMethod.indexOf(".");
		if (hashPos == -1) {
			throw new ParseException("Unrecognized format for '" + beanAndMethod + "'", line);
		}

		return beanAndMethod;
	}

	/**
	 * Construct a routing object 
	 * 
	 * @param httpMethod		request httpMethod
	 * @param path				route path
	 * @param controllerName	controller name
	 * @param methodName		method name
	 * @return					return route object
	 * @throws RouteException
	 */
	private Route buildRoute(String httpMethod, String path, String controllerName, String methodName) throws RouteException {
		Object controller = controllerLoader.load(controllerName);
		Method method = getMethod(controller, methodName);

		return new Route(HttpMethod.valueOf(httpMethod.toUpperCase()), path, null, method);
	}

	private Method getMethod(Object controller, String methodName) throws RouteException {
		try {
			return controller.getClass().getMethod(methodName, Request.class, Response.class);
		} catch (Exception e) {
			throw new RouteException(e);
		}
	}

	public void setBasePackage(String basePackage) {
		this.controllerLoader = new ClassPathControllerLoader(basePackage);
	}

	public ControllerLoader getControllerLoader() {
		return controllerLoader;
	}

	public void setControllerLoader(ControllerLoader controllerLoader) {
		this.controllerLoader = controllerLoader;
	}

}
