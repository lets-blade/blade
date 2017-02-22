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

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Asynchronous Requeest Processor
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.7.1-alpha
 */
public class AsyncRequestProcessor implements Runnable {

    private AsyncContext asyncContext;
    private HttpServletRequest httpRequest;
    private HttpServletResponse httpResponse;
    private DispatcherHandler dispatcherHandler;

    public AsyncRequestProcessor(AsyncContext asyncContext, DispatcherHandler dispatcherHandler) {
        this.asyncContext = asyncContext;
        this.dispatcherHandler = dispatcherHandler;
        this.httpRequest = (HttpServletRequest) asyncContext.getRequest();
        this.httpResponse = (HttpServletResponse) asyncContext.getResponse();
    }

    @Override
    public void run() {
        try {
            dispatcherHandler.handle(httpRequest, httpResponse);
        } finally {
            asyncContext.complete();
        }
    }

}