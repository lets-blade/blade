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
import com.blade.mvc.LocalContext;
import com.blade.mvc.WebContext;
import com.blade.mvc.handler.ExceptionHandler;
import com.blade.mvc.http.HttpRequest;
import com.blade.mvc.http.HttpResponse;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.Route;
import com.blade.mvc.route.RouteMatcher;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.concurrent.FastThreadLocal;
import lombok.extern.slf4j.Slf4j;
import lombok.var;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.blade.mvc.Const.ENV_KEY_HTTP_REQUEST_COST;
import static com.blade.mvc.Const.ENV_KEY_PERFORMANCE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Http Server Handler
 *
 * @author biezhi
 * 2018/10/15
 */
@Slf4j
@ChannelHandler.Sharable
public class HttpServerHandler extends SimpleChannelInboundHandler<HttpObject> {

    public static final  FastThreadLocal<WebContext>   WEB_CONTEXT_THREAD_LOCAL   = new FastThreadLocal<>();
    private static final FastThreadLocal<LocalContext> LOCAL_CONTEXT_THREAD_LOCAL = new FastThreadLocal<>();
    private static final StaticFileHandler             STATIC_FILE_HANDLER        = new StaticFileHandler(WebContext.blade());
    private static final RouteMethodHandler            ROUTE_METHOD_HANDLER       = new RouteMethodHandler();
    private static final Set<String>                   NOT_STATIC_URI             = new HashSet<>(32);
    private static final RouteMatcher                  ROUTE_MATCHER              = WebContext.blade().routeMatcher();

    static final boolean ALLOW_COST =
            WebContext.blade().environment().getBoolean(ENV_KEY_HTTP_REQUEST_COST, true);

    public static final boolean PERFORMANCE =
            WebContext.blade().environment().getBoolean(ENV_KEY_PERFORMANCE, false);

    private static final ExecutorService LOGIC_EXECUTOR =
            Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 2);

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (LOCAL_CONTEXT_THREAD_LOCAL.get() != null && LOCAL_CONTEXT_THREAD_LOCAL.get().hasDecoder()) {
            LOCAL_CONTEXT_THREAD_LOCAL.get().decoder().cleanFiles();
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private static FastThreadLocal<HttpRequest> requestFastThreadLocal = new FastThreadLocal<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HttpObject msg) {
        if (msg instanceof io.netty.handler.codec.http.HttpRequest) {
            HttpRequest httpRequest = new HttpRequest();
            httpRequest.setNettyRequest((io.netty.handler.codec.http.HttpRequest) msg);
            requestFastThreadLocal.set(httpRequest);
            return;
        }

        HttpRequest httpRequest = requestFastThreadLocal.get();
        if (null == httpRequest) {
            return;
        }

        if (msg instanceof HttpContent) {
            httpRequest.appendContent((HttpContent) msg);
        }

        if (httpRequest.chunkIsEnd()) {
            String remoteAddress = ctx.channel().remoteAddress().toString();

            CompletableFuture<HttpRequest> future = CompletableFuture.completedFuture(httpRequest);

            // write response
            future.thenApplyAsync(req -> {
                req.init(remoteAddress);
                return WebContext.create(req, new HttpResponse(), ctx);
            }, LOGIC_EXECUTOR).thenApplyAsync(webContext -> {
                // dispatch
                String uri    = webContext.getRequest().uri();
                String method = webContext.getRequest().method();

                try {
                    if (isStaticFile(method, uri)) {
                        STATIC_FILE_HANDLER.handle(webContext);
                        this.cleanContext();
                    } else {
                        Route route = ROUTE_MATCHER.lookupRoute(method, uri);
                        if (null != route) {
                            webContext.setRoute(route);
                        } else {
                            throw new NotFoundException(uri);
                        }
                    }
                } catch (Exception e) {
                    ROUTE_METHOD_HANDLER.exceptionCaught(uri, method, e);
                    ROUTE_METHOD_HANDLER.finishWrite(webContext);
                    this.cleanContext();
                }
                return webContext;
            }, LOGIC_EXECUTOR).thenApplyAsync(webContext -> {
                // logic
                try {
                    ROUTE_METHOD_HANDLER.handle(webContext);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return webContext;
            }, LOGIC_EXECUTOR).thenApplyAsync(webContext -> {
                // return response
                return ROUTE_METHOD_HANDLER.handleResponse(
                        webContext.getRequest(), webContext.getResponse(),
                        webContext.getChannelHandlerContext()
                );
            }, LOGIC_EXECUTOR).exceptionally(e -> {
                e.printStackTrace();
                var httpResponse = new DefaultFullHttpResponse(HTTP_1_1,
                        HttpResponseStatus.valueOf(200), Unpooled.buffer());

                return httpResponse;
            }).thenAcceptAsync(ctx::writeAndFlush, LOGIC_EXECUTOR);
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
        if ("POST".equals(method) || NOT_STATIC_URI.contains(uri)) {
            return false;
        }
        Optional<String> result = WebContext.blade().getStatics().stream().filter(s -> s.equals(uri) || uri.startsWith(s)).findFirst();
        if (!result.isPresent()) {
            NOT_STATIC_URI.add(uri);
            return false;
        }
        return true;
    }

    public static LocalContext getLocalContext() {
        return LOCAL_CONTEXT_THREAD_LOCAL.get();
    }

    public static void setLocalContext(LocalContext localContext) {
        LOCAL_CONTEXT_THREAD_LOCAL.set(localContext);
    }

    private void cleanContext() {
        LOCAL_CONTEXT_THREAD_LOCAL.remove();
        WEB_CONTEXT_THREAD_LOCAL.remove();
    }

}