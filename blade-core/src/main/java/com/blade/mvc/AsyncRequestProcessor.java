package com.blade.mvc;

import com.blade.Const;
import com.blade.context.WebContextHolder;
import com.blade.ioc.Ioc;
import com.blade.kit.DispatchKit;
import com.blade.kit.StringKit;
import com.blade.mvc.handler.RouteHandler;
import com.blade.mvc.http.*;
import com.blade.mvc.http.wrapper.ServletRequest;
import com.blade.mvc.http.wrapper.ServletResponse;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import com.blade.mvc.view.ModelAndView;
import com.blade.mvc.view.ViewSettings;
import com.blade.mvc.view.resolve.RouteViewResolve;
import com.blade.mvc.view.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * Created by biezhi on 2017/2/20.
 */
public class AsyncRequestProcessor implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncRequestProcessor.class);

    private AsyncContext asyncContext;

    private Ioc ioc;

    private HttpServletRequest httpRequest;

    private HttpServletResponse httpResponse;

    private RouteMatcher routeMatcher;

    private RouteViewResolve routeViewHandler;

    private DispatcherHandler dispatcherHandler;

    public AsyncRequestProcessor(AsyncContext asyncContext, Ioc ioc, RouteMatcher routeMatcher, RouteViewResolve routeViewHandler) {
        this.asyncContext = asyncContext;

        this.httpRequest = (HttpServletRequest) asyncContext.getRequest();
        this.httpResponse = (HttpServletResponse) asyncContext.getResponse();

        this.ioc = ioc;
        this.routeMatcher = routeMatcher;
        this.routeViewHandler = routeViewHandler;
    }

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