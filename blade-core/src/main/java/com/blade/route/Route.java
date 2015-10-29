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

import com.blade.http.HttpMethod;
import com.blade.http.Path;

/**
 * 
 * <p>
 * 路由对象
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class Route {
	
	/**
	 * HTTP请求方法
	 */
	private HttpMethod httpMethod;

	/**
	 * 路由路径
	 */
	private String path;
	
	/**
	 * 执行逻辑的目标对象
	 */
	private Object target;

	/**
	 * 执行逻辑的目标方法
	 */
	private Method action;
	
	public Route() {
	}

	public Route(HttpMethod httpMethod, String path, Object target, Method action) {
		super();
		this.httpMethod = httpMethod;
		this.path = Path.fixPath(path);
		this.target = target;
		this.action = action;
	}
	
	public HttpMethod getHttpMethod() {
		return httpMethod;
	}

	public void setHttpMethod(HttpMethod httpMethod) {
		this.httpMethod = httpMethod;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Object getTarget() {
		return target;
	}

	public void setTarget(Object target) {
		this.target = target;
	}

	public Method getAction() {
		return action;
	}

	public void setAction(Method action) {
		this.action = action;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((httpMethod == null) ? 0 : httpMethod.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		Route other = (Route) obj;
		if (httpMethod != other.httpMethod)
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return httpMethod + "\t" + path;
	}
	
}
