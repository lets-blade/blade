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
package blade.wrapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import blade.route.RouteMatcher;
import blade.servlet.Request;
import blade.servlet.Response;

/**
 * Request、Response构建器
 * <p>
 * 构建Request和Response
 * </p>
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class RequestResponseBuilder {
	
    private RequestResponseBuilder() {
    }
    
    /**
     * 构建一个Request对象
     * 
     * @param match 		匹配到的路由对象
     * @param request 		HttpServletRequest请求对象
     * @return Request		返回包装后的request
     */
    public static Request build(RouteMatcher match, HttpServletRequest request) {
        return new Request(match, request);
    }

    /**
     * 构建一个Response对象
     * 
     * @param response 		HttpServletResponse响应对象
     * @return Response		返回包装后的response
     */
    public static Response build(HttpServletResponse response) {
        return new Response(response);
    }

}
