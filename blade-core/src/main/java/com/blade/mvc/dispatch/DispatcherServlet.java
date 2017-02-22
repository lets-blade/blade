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
package com.blade.mvc.dispatch;

import com.blade.Const;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Blade Core DispatcherServlet
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.1-alpha
 */
public class DispatcherServlet extends AbsDispatcherServlet {

    @Override
    protected void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException, IOException {
        httpRequest.setCharacterEncoding(blade.encoding());
        httpResponse.setCharacterEncoding(blade.encoding());
        httpResponse.setHeader("X-Powered-By", "Blade(" + Const.VERSION + ")");
        httpRequest.setAttribute("org.apache.catalina.ASYNC_SUPPORTED", true);
        dispatcherHandler.handle(httpRequest, httpResponse);
    }

}