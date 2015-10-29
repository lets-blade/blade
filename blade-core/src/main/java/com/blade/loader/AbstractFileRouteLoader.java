package com.blade.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.blade.http.HttpMethod;
import com.blade.http.Request;
import com.blade.http.Response;
import com.blade.route.Route;
import com.blade.route.RouteException;

import blade.kit.IOKit;

/**
 * <p>
 * 抽象加载器实现
 * </p>
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
	 * 加载路由
	 * 
	 * @param inputStream		路由文件流
	 * @return					返回路由列表
	 * @throws ParseException	解析异常
	 * @throws IOException		IO异常
	 */
	private List<Route> load(InputStream inputStream) throws ParseException, IOException {
		int line = 0;
		List<Route> routes = new ArrayList<Route>();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));
			String input;
			while ( (input = in.readLine()) != null ) {
				line++;

				input = input.trim();

				if (!input.equals("") && !input.startsWith("#")) {
					Route route = parse(input, line);
					routes.add(route);
				}
			}
		} finally {
			IOKit.closeQuietly(in);
		}
		return routes;
	}

	private Route parse(String input, int line) throws ParseException {
		StringTokenizer st = new StringTokenizer(input, " \t");
		if (st.countTokens() != 3) {
			throw new ParseException("Unrecognized format", line);
		}

		// 验证HTTP请求方
		String httpMethod = validateHttpMethod( st.nextToken().trim(), line );
		
		String path = validatePath( st.nextToken().trim(), line );
		String controllerAndMethod = validateControllerAndMethod( st.nextToken().trim(), line );

		int hashPos = controllerAndMethod.indexOf('#');
		String controllerName = controllerAndMethod.substring(0, hashPos);

		// 获取控制器方法
		String controllerMethod = controllerAndMethod.substring(hashPos + 1);

		return buildRoute(httpMethod, path, controllerName, controllerMethod);
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
		char[] invalidChars = { '?', '#', ' ' };
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
	 * 验证控制器方法
	 * 
	 * @param beanAndMethod		控制器和方法，使用#隔开
	 * @param line				所在行数
	 * @return					返回验证后的字符串，异常则抛出
	 * @throws ParseException
	 */
	private String validateControllerAndMethod(String beanAndMethod, int line) throws ParseException {
		int hashPos = beanAndMethod.indexOf('#');
		if (hashPos == -1) {
			throw new ParseException("Unrecognized format for '" + beanAndMethod + "'", line);
		}

		return beanAndMethod;
	}

	/**
	 * 构建一个路由对象
	 * 
	 * @param httpMethod		请求方法
	 * @param path				路由路径
	 * @param controllerName	控制器名称
	 * @param methodName		执行的方法名称
	 * @return					返回路由对象
	 * @throws RouteException
	 */
	private Route buildRoute(String httpMethod, String path, String controllerName, String methodName) throws RouteException {
		Object controller = controllerLoader.load(controllerName);
		Method method = getMethod(controller, methodName);

		return new Route(HttpMethod.valueOf(httpMethod.toUpperCase()), path, controller, method);
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
