/**
 * Copyright (c) 2018, biezhi 王爵 nice (biezhi.me@gmail.com)
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
package com.blade.server.netty;

import com.blade.exception.NotFoundException;
import com.blade.kit.BladeCache;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.blade.kit.BladeKit.log200;
import static com.blade.kit.BladeKit.log200AndCost;
import static com.blade.mvc.Const.*;
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

    private final       boolean                     ALLOW_COST               =
            WebContext.blade().environment()
                    .getBoolean(ENV_KEY_HTTP_REQUEST_COST, true);

    public static final boolean PERFORMANCE =
            WebContext.blade().environment()
                    .getBoolean(ENV_KEY_PERFORMANCE, false);

    private final StaticFileHandler  staticFileHandler = new StaticFileHandler(WebContext.blade());
    private final RouteMethodHandler routeHandler      = new RouteMethodHandler();
    private final Set<String>        notStaticUri      = new HashSet<>(32);
    private final RouteMatcher       routeMatcher      = WebContext.blade().routeMatcher();
    private final ExecutorService logicPool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() + 1);

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpRequest httpRequest) {
        CompletableFuture<HttpRequest> future = CompletableFuture.completedFuture(httpRequest);
        Executor executor = ctx.executor();

        // write response
        future.thenApplyAsync(req -> buildWebContext(future, ctx, req), executor)
                .thenApplyAsync(this::dispatchRequest, executor)
                .thenApplyAsync(this::executeLogic, logicPool)
                .exceptionally(this::handleException)
                .thenApplyAsync(this::buildResponse, executor)
                .thenAcceptAsync(ctx::writeAndFlush, executor);
    }

    private WebContext handleException(Throwable e) {
        Request request = WebContext.request();
        String  method  = request.method();
        String  uri     = request.uri();
        routeHandler.exceptionCaught(uri, method, (Exception) e.getCause());
        return WebContext.get();
    }

    private FullHttpResponse buildResponse(WebContext webContext) {
        WebContext.set(webContext);
        return routeHandler.handleResponse(
                webContext.getRequest(), webContext.getResponse(),
                webContext.getChannelHandlerContext()
        );
    }

    private WebContext executeLogic(WebContext webContext) {
        try {
            Instant start = Instant.now();
            routeHandler.handle(webContext);
            if (PERFORMANCE) {
                return webContext;
            }
            Request request = webContext.getRequest();
            String  method  = request.method();
            String  uri     = request.uri();

            if (ALLOW_COST) {
                long cost = log200AndCost(log, start, BladeCache.getPaddingMethod(method), uri);
                request.attribute(REQUEST_COST_TIME, cost);
            } else {
                log200(log, BladeCache.getPaddingMethod(method), uri);
            }
        } catch (Exception e) {
            webContext.getFuture().completeExceptionally(e);
        }
        return webContext;
    }

    private WebContext dispatchRequest(WebContext webContext) {
        String uri    = webContext.getRequest().uri();
        String method = webContext.getRequest().method();

        try {
            if (isStaticFile(method, uri)) {
                staticFileHandler.handle(webContext);
            } else {
                Route route = routeMatcher.lookupRoute(method, uri);
                if (null != route) {
                    webContext.setRoute(route);
                } else {
                    throw new NotFoundException(uri);
                }
            }
        } catch (Exception e) {
            webContext.getFuture().completeExceptionally(e);
        }
        return webContext;
    }

    private WebContext buildWebContext(CompletableFuture future,
                                       ChannelHandlerContext ctx,
                                       HttpRequest req) {

        String remoteAddress = ctx.channel().remoteAddress().toString();
        req.init(remoteAddress);
        return WebContext.create(req, new HttpResponse(), ctx, future);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!ExceptionHandler.isResetByPeer(cause)) {
            log.error(cause.getMessage(), cause);
            FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, HttpResponseStatus.valueOf(500));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isStaticFile(String method, String uri) {
        if ("POST".equals(method) || notStaticUri.contains(uri)) {
            return false;
        }
        Optional<String> result = WebContext.blade().getStatics().stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        if (!result.isPresent()) {
            notStaticUri.add(uri);
            return false;
        }
        return true;
    }

}