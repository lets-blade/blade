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
package com.blade.route;

import java.lang.reflect.Method;
import java.util.List;

import blade.kit.PathKit;

/**
 * 路由匹配对象
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RouteMatcher {
	
	/**
	 * 目标运行类实例
	 */
	private Class<?> target;
	
	/**
	 * 执行的类实例
	 */
	private RouteHandler routerHandler;
	
	/**
	 * 要运行的方法对象
	 */
	private Method execMethod;
	
	/**
	 * http请求方法
	 */
	private HttpMethod httpMethod;
    
    /**
     * 请求URI
     */
	private String requestURI;
    
    /**
     * 路由path
     */
	private String path;
    
    public RouteMatcher() {
    }
    
    public RouteMatcher(RouteHandler routerHandler, Class<?> target, Method execMethod, HttpMethod httpMethod, String path, String requestUri) {
        this.routerHandler = routerHandler;
        this.target = target;
        this.execMethod = execMethod;
        this.httpMethod = httpMethod;
        this.path = path;
        this.requestURI = requestUri;
    }
    
    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public Method getExecMethod() {
		return execMethod;
	}
    
    public String getPath() {
		return path;
	}
    
    public Class<?> getTarget() {
		return target;
	}
    
    public RouteHandler getRouterHandler() {
		return routerHandler;
	}

	/**
     * 根据http方法和path进行匹配
     * 
     * @param httpMethod		http方法，GET/POST
     * @param path				匹配的路径
     * @return					true:匹配成功,false:匹配失败
     */
	public boolean matches(HttpMethod httpMethod, String path) {
		
    	// 如果是拦截器的全部匹配模式则跳过，返回true
        if ((httpMethod == HttpMethod.BEFORE || httpMethod == HttpMethod.AFTER)
                && (this.httpMethod == httpMethod)
                && this.path.equals(PathKit.ALL_PATHS)) {
            return true;
        }
        
        boolean match = false;
        
        if (this.httpMethod == HttpMethod.ALL || this.httpMethod == httpMethod) {
            match = matchPath(path);
        }
        
        return match;
    }

	/**
	 * 继续匹配
	 * 
	 * @param uri
	 * @return
	 */
    private boolean matchPath(String uri) {
    	
    	// /hello
        if (!this.path.endsWith("*") && ((uri.endsWith("/") && !this.path.endsWith("/"))
                || (this.path.endsWith("/") && !uri.endsWith("/")))) {
            return false;
        }
        
        if (this.path.equals(uri)) {
            return true;
        }

        // 检查参数
        List<String> thisPathList = PathKit.convertRouteToList(this.path);
        List<String> uriList = PathKit.convertRouteToList(uri);

        int thisPathSize = thisPathList.size();
        int uriSize = uriList.size();

        if (thisPathSize == uriSize) {
            for (int i = 0; i < thisPathSize; i++) {
                String thisPathPart = thisPathList.get(i);
                String pathPart = uriList.get(i);

                if ((i == thisPathSize - 1) && (thisPathPart.equals("*") && this.path.endsWith("*"))) {
                    // 通配符匹配
                    return true;
                }

                if ((!thisPathPart.startsWith(":"))
                        && !thisPathPart.equals(pathPart)
                        && !thisPathPart.equals("*")) {
                    return false;
                }
            }
            // 全部匹配
            return true;
        } else {
            if (this.path.endsWith("*")) {
                if (uriSize == (thisPathSize - 1) && (path.endsWith("/"))) {
                	uriList.add("");
                	uriList.add("");
                	uriSize += 2;
                }

                if (thisPathSize < uriSize) {
                    for (int i = 0; i < thisPathSize; i++) {
                        String thisPathPart = thisPathList.get(i);
                        String pathPart = uriList.get(i);
                        if (thisPathPart.equals("*") && (i == thisPathSize - 1) && this.path.endsWith("*")) {
                            return true;
                        }
                        if (!thisPathPart.startsWith(":")
                                && !thisPathPart.equals(pathPart)
                                && !thisPathPart.equals("*")) {
                            return false;
                        }
                    }
                    return true;
                }
            }
            return false;
        }
    }
    
    @Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((execMethod == null) ? 0 : execMethod.hashCode());
		result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
		result = prime * result + ((routerHandler == null) ? 0 : routerHandler.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RouteMatcher other = (RouteMatcher) obj;
		if (execMethod == null) {
			if (other.execMethod != null)
				return false;
		} else if (!execMethod.equals(other.execMethod))
			return false;
		if (httpMethod != other.httpMethod)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		if (routerHandler == null) {
			if (other.routerHandler != null)
				return false;
		} else if (!routerHandler.equals(other.routerHandler))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}
	
	
	public void setTarget(Class<?> target) {
		this.target = target;
	}

	public void setRouterHandler(RouteHandler routerHandler) {
		this.routerHandler = routerHandler;
	}

	public void setExecMethod(Method execMethod) {
		this.execMethod = execMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	public void setPath(String path) {
		this.path = path;
	}

	@Override
    public String toString() {
        return httpMethod.name() + ":" + path;
    }

	public String getRequestURI() {
		return requestURI;
	}
	
}
