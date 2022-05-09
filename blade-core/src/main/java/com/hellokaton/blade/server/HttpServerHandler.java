/**
 * Copyright (c) 2018, biezhi 王爵 nice (hellokaton@gmail.com)
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
package com.hellokaton.blade.server;

import com.hellokaton.blade.exception.BladeException;
import com.hellokaton.blade.exception.NotFoundException;
import com.hellokaton.blade.kit.BladeCache;
import com.hellokaton.blade.kit.LRUSet;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.handler.ExceptionHandler;
import com.hellokaton.blade.mvc.http.*;
import com.hellokaton.blade.mvc.route.Route;
import com.hellokaton.blade.mvc.route.RouteMatcher;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import static com.hellokaton.blade.kit.BladeKit.log200AndCost;
import static com.hellokaton.blade.kit.BladeKit.log500;
import static com.hellokaton.blade.mvc.BladeConst.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2018/10/15
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpRequest> {

    public static final FastThreadLocal<WebContext> WEB_CONTEXT_THREAD_LOCAL = new FastThreadLocal<>();

    private final StaticFileHandler staticFileHandler = new StaticFileHandler(WebContext.blade());
    private final RouteMethodHandler routeHandler = new RouteMethodHandler();
    private final Set<String> notStaticUri = new LRUSet<>(128);
    private final RouteMatcher routeMatcher = WebContext.blade().routeMatcher();

    private boolean allowCost() {
        return WebContext.blade().httpOptions().isEnableRequestCost();
    }

    private boolean recordRequestLog() {
        return WebContext.blade().environment()
                .getBoolean(ENV_KEY_REQUEST_LOG, true);
    }

    private boolean enablePerformance() {
        return WebContext.blade().environment()
                .getBoolean(ENV_KEY_PERFORMANCE, false);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        CompletableFuture<HttpRequest> future = CompletableFuture.completedFuture(httpRequest);

        Executor executor = ctx.executor();

        future.thenApplyAsync(req -> buildWebContext(ctx, req), executor)
                .thenApplyAsync(this::executeLogic, executor)
                .thenApplyAsync(this::buildResponse, executor)
                .exceptionally(this::handleException)
                .thenAcceptAsync(msg -> writeResponse(ctx, future, msg), ctx.channel().eventLoop());
    }

    private WebContext buildWebContext(ChannelHandlerContext ctx,
                                       HttpRequest req) {
        return WebContext.create(req, new HttpResponse(), ctx);
    }

    private void writeResponse(ChannelHandlerContext ctx, CompletableFuture<HttpRequest> future, io.netty.handler.codec.http.HttpResponse msg) {
        ctx.writeAndFlush(msg);
        future.complete(null);
    }

    private io.netty.handler.codec.http.HttpResponse handleException(Throwable e) {
        Request request = WebContext.request();
        Response response = WebContext.response();
        String method = request.method();
        String uri = request.uri();

        Exception srcException = (Exception) e.getCause().getCause();
        if (srcException instanceof BladeException) {
        } else {
            log500(log, method, uri);
        }
        if (null != WebContext.blade().exceptionHandler()) {
            WebContext.blade().exceptionHandler().handle(srcException);
        } else {
            log.error("", srcException);
        }

        return routeHandler.handleResponse(
                request, response, WebContext.get().getChannelHandlerContext()
        );
    }

    private io.netty.handler.codec.http.HttpResponse buildResponse(WebContext webContext) {
        WebContext.set(webContext);
        return routeHandler.handleResponse(
                webContext.getRequest(), webContext.getResponse(),
                webContext.getChannelHandlerContext()
        );
    }

    private WebContext executeLogic(WebContext webContext) {
        try {
            WebContext.set(webContext);
            Request request = webContext.getRequest();
            HttpMethod method = request.httpMethod();
            String uri = request.uri();
            Instant start = Instant.now();

            if (isStaticFile(method, uri)) {
                staticFileHandler.handle(webContext);
                return webContext;
            }

            Route route = routeMatcher.lookupRoute(method.name(), uri);
            if (null != route) {
                webContext.setRoute(route);
            } else {
                throw new NotFoundException(uri);
            }
            routeHandler.handle(webContext);

            if (enablePerformance()) {
                return webContext;
            }

            if (recordRequestLog()) {
                long cost = log200AndCost(log, start, BladeCache.getPaddingMethod(method.name()), uri);
                request.attribute(REQUEST_COST_TIME, cost);
            }
            return webContext;
        } catch (Exception e) {
            throw BladeException.wrapper(e);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ExceptionHandler.isResetByPeer(cause)) {
            log.error(cause.getMessage(), cause);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isStaticFile(HttpMethod method, String uri) {
        if (!HttpMethod.GET.equals(method) || notStaticUri.contains(uri)) {
            return false;
        }

        if (WebContext.blade().getStatics().stream().noneMatch(s -> s.equals(uri) || uri.startsWith(s))) {
            notStaticUri.add(uri);
            return false;
        }
        return true;
    }

}