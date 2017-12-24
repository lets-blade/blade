package com.blade.server.netty;

import com.blade.exception.NotFoundException;
import com.blade.mvc.WebContext;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

/**
 * @author biezhi
 * @date 2017/12/24
 */
@Slf4j
public class RequestExecution implements Runnable {

    private final ChannelHandlerContext ctx;
    private final FullHttpRequest       fullHttpRequest;

    public RequestExecution(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest) {
        this.ctx = ctx;
        this.fullHttpRequest = fullHttpRequest;
    }

    @Override
    public void run() {
        Request  request  = HttpRequest.build(ctx, fullHttpRequest);
        Response response = HttpResponse.build(ctx, HttpServerHandler.date);
        boolean  isStatic = false;
        // route signature
        Signature signature = Signature.builder().request(request).response(response).build();
        try {

            // request uri
            String uri = request.uri();

            // write session
            WebContext.set(new WebContext(request, response));

            if (isStaticFile(uri)) {
                HttpServerHandler.staticFileHandler.handle(ctx, request, response);
                isStatic = true;
                return;
            }

            Route route = HttpServerHandler.routeMatcher.lookupRoute(request.method(), uri);
            if (null == route) {
                log.warn("Not Found\t{}", uri);
                throw new NotFoundException(uri);
            }

            log.info("{}\t{}\t{}", request.protocol(), request.method(), uri);

            request.initPathParams(route);

            // get method parameters
            signature.setRoute(route);

            // middleware
            if (HttpServerHandler.hasMiddleware && !HttpServerHandler.requestInvoker.invokeMiddleware(HttpServerHandler.routeMatcher.getMiddleware(), signature)) {
                this.sendFinish(response);
                return;
            }

            // web hook before
            if (HttpServerHandler.hasBeforeHook && !HttpServerHandler.requestInvoker.invokeHook(HttpServerHandler.routeMatcher.getBefore(uri), signature)) {
                this.sendFinish(response);
                return;
            }

            // execute
            signature.setRoute(route);
            HttpServerHandler.requestInvoker.routeHandle(signature);

            // webHook
            if (HttpServerHandler.hasAfterHook) {
                HttpServerHandler.requestInvoker.invokeHook(HttpServerHandler.routeMatcher.getAfter(uri), signature);
            }
        } catch (Exception e) {
            if (null != HttpServerHandler.exceptionHandler) {
                HttpServerHandler.exceptionHandler.handle(e);
            } else {
                log.error("Blade Invoke Error", e);
            }
        } finally {
            if (!isStatic) this.sendFinish(response);
            WebContext.remove();
        }
    }


    private boolean isStaticFile(String uri) {
        Optional<String> result = HttpServerHandler.statics.stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        return result.isPresent();
    }

    private void sendFinish(Response response) {
        if (!response.isCommit()) {
            response.body(Unpooled.EMPTY_BUFFER);
        }
    }

}
