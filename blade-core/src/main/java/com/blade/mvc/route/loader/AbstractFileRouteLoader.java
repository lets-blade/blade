/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.route.loader;

import com.blade.exception.RouteException;
import com.blade.kit.IOKit;
import com.blade.mvc.http.HttpMethod;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Abstract loader implementation 
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.5
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
     * @param inputStream route inputstream
     * @return return route list
     * @throws ParseException parse exception
     * @throws IOException    io exception
     */
    private List<Route> load(InputStream inputStream) throws ParseException, IOException {
        int line = 0;
        List<Route> routes = new ArrayList<Route>();
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(inputStream));
            String input;
            while ((input = in.readLine()) != null) {
                line++;

                input = input.trim();

                if (!input.equals("") && !input.startsWith(".")) {
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

        // Verify HTTP request
        String httpMethod = validateHttpMethod(st.nextToken().trim(), line);

        String path = validatePath(st.nextToken().trim(), line);
        String controllerAndMethod = validateControllerAndMethod(st.nextToken().trim(), line);

        int hashPos = controllerAndMethod.indexOf(".");
        String controllerName = controllerAndMethod.substring(0, hashPos);

        // Acquisition controller method
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
        for (int i = 0; i < path.length(); i++) {

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
        char[] invalidChars = {'?', '.', ' '};
        for (char invalidChar : invalidChars) {
            if (c == invalidChar) {
                return false;
            }
        }

        if (openedKey) {
            char[] moreInvalidChars = {'/', '{'};
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
     * @param beanAndMethod        controller and method, using.
     * @param line                line number
     * @return return a string that is verified after the verification.
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
     * @param httpMethod        request httpMethod
     * @param path                route path
     * @param controllerName    controller name
     * @param methodName        method name
     * @return return route object
     * @throws RouteException
     */
    private Route buildRoute(String httpMethod, String path, String controllerName, String methodName) throws RouteException {
        Object controller = controllerLoader.load(controllerName);
        Class<?> controllerType = controller.getClass();
        Method method = getMethod(controllerType, methodName);
        return new Route(HttpMethod.valueOf(httpMethod.toUpperCase()), path, controller, controllerType, method);
    }

    private Method getMethod(Class<?> controllerType, String methodName) throws RouteException {
        try {
            return controllerType.getMethod(methodName, Request.class, Response.class);
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
