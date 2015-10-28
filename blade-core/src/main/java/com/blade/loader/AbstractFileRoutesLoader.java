package com.blade.loader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.blade.http.HttpMethod;
import com.blade.http.Request;
import com.blade.http.Response;
import com.blade.route.Route;
import com.blade.route.RoutesException;

public abstract class AbstractFileRoutesLoader implements RoutesLoader {

	private ControllerLoader controllerLoader = new ClassPathControllerLoader();

	@Override
	public List<Route> load() throws ParseException, RoutesException {
		InputStream inputStream = null;
		try {
			inputStream = getInputStream();
		} catch (Exception e) {
			throw new RoutesException("Problem loading the routes.config file: " + e.getMessage(), e);
		}

		try {
			return load(inputStream);
		} catch (IOException e) {
			throw new RoutesException("Problem loading the routes.config file: " + e.getMessage(), e);
		}
	}

	private List<Route> load(InputStream inputStream) throws ParseException, IOException {
		int line = 0; // reset line positioning
		List<Route> routes = new ArrayList<Route>(); // this is what we will fill and return

		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(inputStream));

			String input;
			while ( (input = in.readLine()) != null ) {
				line++;

				input = input.trim();

				// only parse line if it is not empty and not a comment
				if (!input.equals("") && !input.startsWith("#")) {
					Route route = parse(input, line);
					routes.add(route);
				}
			}

		} finally {
			closeResource(in);
		}

		return routes;
	}

	private Route parse(String input, int line) throws ParseException {
		StringTokenizer st = new StringTokenizer(input, " \t");
		if (st.countTokens() != 3) {
			throw new ParseException("Unrecognized format", line);
		}

		// retrieve and validate the three arguments
		String httpMethod = validateHttpMethod( st.nextToken().trim(), line );
		String path = validatePath( st.nextToken().trim(), line );
		String controllerAndMethod = validateControllerAndMethod( st.nextToken().trim(), line );

		// retrieve controller name
		int hashPos = controllerAndMethod.indexOf('#');
		String controllerName = controllerAndMethod.substring(0, hashPos);

		// retrieve controller method
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

	private String validateControllerAndMethod(String beanAndMethod, int line) throws ParseException {
		int hashPos = beanAndMethod.indexOf('#');
		if (hashPos == -1) {
			throw new ParseException("Unrecognized format for '" + beanAndMethod + "'", line);
		}

		return beanAndMethod;
	}

	private Route buildRoute(String httpMethod, String path, String controllerName, String methodName) throws RoutesException {
		Object controller = controllerLoader.load(controllerName);
		Method method = getMethod(controller, methodName);

		return new Route(HttpMethod.valueOf(httpMethod.toUpperCase()), path, controller, method);
	}

	private Method getMethod(Object controller, String methodName) throws RoutesException {
		try {
			return controller.getClass().getMethod(methodName, Request.class, Response.class);
		} catch (Exception e) {
			throw new RoutesException(e);
		}
	}

	private void closeResource(Reader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (Exception e) {
			}
		}
	}

	protected abstract InputStream getInputStream() throws Exception;
	
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
