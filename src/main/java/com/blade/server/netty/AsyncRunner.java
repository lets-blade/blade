package com.blade.server.netty;

import com.blade.kit.BladeCache;
import com.blade.mvc.WebContext;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static com.blade.kit.BladeKit.log200;
import static com.blade.mvc.Const.REQUEST_COST_TIME;

/**
 * @author biezhi
 * @date 2018/10/15
 */
@Slf4j
public class AsyncRunner {

    private CompletableFuture<Void> future;
    private Request                 request;
    private Response                response;
    private WebContext              webContext;
    private Instant                 started;
    private RouteMethodHandler      routeHandler;
    private boolean                 isFinished;

    public AsyncRunner(RouteMethodHandler routeHandler, WebContext webContext) {
        this.routeHandler = routeHandler;
        this.request = webContext.getRequest();
        this.response = webContext.getResponse();
        this.webContext = webContext;
        if (WebContext.blade().allowCost()) {
            this.started = Instant.now();
        }
    }

    /**
     * Routing logic execution
     *
     * @return
     */
    public AsyncRunner handle() {
        log.info("handle...");
        WebContext.set(webContext);
        String uri    = request.uri();
        String method = request.method();
        try {
            routeHandler.handle(webContext.getHandlerContext(), request, response);
            if (WebContext.blade().allowCost()) {
                String paddingMethod = BladeCache.getPaddingMethod(method);
                long   cost          = log200(log, this.started, paddingMethod, uri);
                request.attribute(REQUEST_COST_TIME, cost);
            }
        } catch (Exception e) {
            routeHandler.exceptionCaught(uri, method, e);
        }
        return this;
    }

    public void finishWrite() {
        System.out.println("finishWrite 线程: " + Thread.currentThread());
        routeHandler.finishWrite(webContext.getHandlerContext(), request, response);
        WebContext.remove();
        isFinished = true;
        if (null != future) {
            future.complete(null);
        }
    }

    public void setFuture(CompletableFuture<Void> future) {
        this.future = future;
        if (isFinished) {
            future.complete(null);
        }
    }

}
