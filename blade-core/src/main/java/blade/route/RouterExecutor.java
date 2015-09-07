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

/**
 * 多个路由的执行器
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class RouterExecutor{
	
	private String[] paths;
	
	private HttpMethod httpMethod;
	
	public RouterExecutor(String[] paths, HttpMethod httpMethod) {
		this.paths = paths;
		this.httpMethod = httpMethod;
	}
	
	public void run(Router router) {
		// 拦截器
		if(this.httpMethod == HttpMethod.BEFORE || this.httpMethod == HttpMethod.AFTER){
			for(String path : paths){
				RouteMatcherBuilder.buildInterceptor(path, router, httpMethod);
			}
		} else {
			// 路由
			for(String path : paths){
				RouteMatcherBuilder.buildHandler(path, router, httpMethod);
			}
		}
	}
	
}
