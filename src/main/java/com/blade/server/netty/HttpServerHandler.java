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

import com.blade.exception.BladeException;
import com.blade.exception.NotFoundException;
import com.blade.kit.BladeCache;
import com.blade.kit.LRUSet;
import com.blade.mvc.RouteContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.*;
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

import static com.blade.kit.BladeKit.*;
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

    private final boolean ALLOW_COST =
            WebContext.blade().environment()
                    .getBoolean(ENV_KEY_HTTP_REQUEST_COST, true);

    public static final boolean PERFORMANCE =
            WebContext.blade().environment()
                    .getBoolean(ENV_KEY_PERFORMANCE, false);

    private final StaticFileHandler staticFileHandler = new StaticFileHandler(WebContext.blade());
    private final RouteMethodHandler routeHandler = new RouteMethodHandler();
    private final Set<String> notStaticUri = new LRUSet<>(128);
    private final RouteMatcher routeMatcher = WebContext.blade().routeMatcher();

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

        String remoteAddress = ctx.channel().remoteAddress().toString();
        req.init(remoteAddress);
        return WebContext.create(req, new HttpResponse(), ctx);
    }

    private void writeResponse(ChannelHandlerContext ctx, CompletableFuture<HttpRequest> future, FullHttpResponse msg) {
        ctx.writeAndFlush(msg);
        future.complete(null);
    }

    private FullHttpResponse handleException(Throwable e) {
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

    private FullHttpResponse buildResponse(WebContext webContext) {
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
            String method = request.method();
            String uri = request.uri();
            Instant start = null;

            if (ALLOW_COST && !PERFORMANCE) {
                start = Instant.now();
            }

            if (isStaticFile(method, uri)) {
                staticFileHandler.handle(webContext);
            } else {
                if (HttpMethod.OPTIONS.name().equals(method) && null != WebContext.blade().corsMiddleware()) {
                    WebContext.blade().corsMiddleware().handle(new RouteContext(webContext.getRequest(), webContext.getResponse()));
                } else {
                    Route route = routeMatcher.lookupRoute(method, uri);
                    if (null != route) {
                        webContext.setRoute(route);
                    } else {
                        throw new NotFoundException(uri);
                    }
                    routeHandler.handle(webContext);
                }

                if (PERFORMANCE) {
                    return webContext;
                }

                if (ALLOW_COST) {
                    long cost = log200AndCost(log, start, BladeCache.getPaddingMethod(method), uri);
                    request.attribute(REQUEST_COST_TIME, cost);
                } else {
                    log200(log, BladeCache.getPaddingMethod(method), uri);
                }
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

    private boolean isStaticFile(String method, String uri) {
        if (HttpMethod.POST.name().equals(method) || notStaticUri.contains(uri)) {
            return false;
        }

        if (WebContext.blade().getStatics().stream().noneMatch(s -> s.equals(uri) || uri.startsWith(s))) {
            notStaticUri.add(uri);
            return false;
        }
        return true;
    }

}