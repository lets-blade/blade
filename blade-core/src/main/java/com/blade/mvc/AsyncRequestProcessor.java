package com.blade.mvc;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by biezhi on 2017/2/20.
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