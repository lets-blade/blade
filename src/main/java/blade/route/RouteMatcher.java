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
package blade.route;

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
	Class<?> target;
	
	/**
	 * 要运行的方法对象
	 */
	Method execMethod;
	
	/**
	 * http请求方法
	 */
    HttpMethod httpMethod;
    
    /**
     * 路由path
     */
    String path;
    
    /**
     * 请求URI
     */
    String requestURI;
    
    /**
     * 允许的acceptType
     */
    String acceptType;
    
    public RouteMatcher() {
    }
    
    public RouteMatcher(Class<?> target, Method execMethod, HttpMethod httpMethod, String path, String requestUri, String acceptType) {
        super();
        this.target = target;
        this.execMethod = execMethod;
        this.httpMethod = httpMethod;
        this.path = path;
        this.requestURI = requestUri;
        this.acceptType = acceptType;
    }

    public String getAcceptType() {
        return acceptType;
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
    
	public String getRequestURI() {
        return requestURI;
    }
	
    public Class<?> getTarget() {
		return target;
	}
    
    /**
     * 根据http方法和path进行匹配
     * 
     * @param httpMethod		http方法，GET/POST
     * @param path				匹配的路径
     * @return					true:匹配成功,false:匹配失败
     */
	boolean matches(HttpMethod httpMethod, String path) {
		
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
    public boolean equals(Object obj) {
    	if(obj instanceof RouteMatcher){
    		RouteMatcher r = (RouteMatcher) obj;
    		return this.httpMethod == r.httpMethod && this.execMethod.getName().equals(r.execMethod.getName()) 
    				&& this.path.equals(r.path) && this.acceptType.equals(r.acceptType);
    	}
    	return false;
    }
    
    @Override
    public String toString() {
        return httpMethod.name() + "------" + path;
    }
	
}
