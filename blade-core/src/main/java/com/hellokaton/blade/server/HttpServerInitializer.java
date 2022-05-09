package com.hellokaton.blade.server;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.Environment;
import com.hellokaton.blade.kit.DateKit;
import com.hellokaton.blade.options.CorsOptions;
import com.hellokaton.blade.options.HttpOptions;
import com.hellokaton.blade.options.StaticOptions;
import com.hellokaton.blade.server.decode.FullHttpRequestDecode;
import com.hellokaton.blade.server.decode.HttpObjectAggregatorDecode;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.hellokaton.blade.mvc.BladeConst.*;

/**
 * HttpServerInitializer
 */
@Slf4j
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServerHandler httpServerHandler;
    private final SslContext sslCtx;
    private CorsConfig corsConfig;
    private int maxContentSize;
    private boolean enableGzip;

    public static volatile String date = DateKit.gmtDate(LocalDateTime.now());


    public HttpServerInitializer(SslContext sslCtx, Blade blade, ScheduledExecutorService service) {
        this.sslCtx = sslCtx;
        this.httpServerHandler = new HttpServerHandler();
        this.mergeCorsConfig(blade.corsOptions());
        this.mergeStaticOptions(blade.staticOptions(), blade.environment());
        this.mergeHttpOptions(blade.httpOptions(), blade.environment());
        service.scheduleWithFixedDelay(() -> date = DateKit.gmtDate(LocalDateTime.now()), 1000, 1000, TimeUnit.MILLISECONDS);
    }


    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        try {
            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }
            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregatorDecode(maxContentSize));
            pipeline.addLast(new HttpServerExpectContinueHandler());

            if (enableGzip) {
                pipeline.addLast(new HttpContentCompressor());
            }
            if (null != corsConfig) {
                pipeline.addLast(new CorsHandler(corsConfig));
            }
            pipeline.addLast(new FullHttpRequestDecode());
            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(httpServerHandler);
        } catch (Exception e) {
            log.error("Add channel pipeline error", e);
        }
    }

    private void mergeCorsConfig(CorsOptions corsOptions) {
        if (null == corsOptions) {
            return;
        }
        CorsConfigBuilder corsConfigBuilder = null;
        if (corsOptions.isAnyOrigin()) {
            corsConfigBuilder = CorsConfigBuilder.forAnyOrigin();
        }
        if (null != corsOptions.getOrigins() && !corsOptions.getOrigins().isEmpty()) {
            corsConfigBuilder = CorsConfigBuilder.forOrigins(corsOptions.getOrigins().toArray(new String[0]));
        }
        if (null == corsConfigBuilder) {
            return;
        }
        if (corsOptions.isAllowNullOrigin()) {
            corsConfigBuilder.allowNullOrigin();
        }
        if (corsOptions.isAllowCredentials()) {
            corsConfigBuilder.allowCredentials();
        }
        if (corsOptions.isDisable()) {
            corsConfigBuilder.disable();
        }
        corsConfigBuilder.maxAge(corsOptions.getMaxAge());

        if (null != corsOptions.getExposeHeaders() && !corsOptions.getExposeHeaders().isEmpty()) {
            corsConfigBuilder.exposeHeaders(corsOptions.getExposeHeaders().toArray(new String[0]));
        }
        if (null != corsOptions.getAllowedMethods() && !corsOptions.getAllowedMethods().isEmpty()) {
            corsConfigBuilder.allowedRequestMethods(corsOptions.getAllowedMethods().stream()
                    .map(item -> HttpMethod.valueOf(item.name()))
                    .toArray(HttpMethod[]::new));
        }
        if (null != corsOptions.getAllowedHeaders() && !corsOptions.getAllowedHeaders().isEmpty()) {
            corsConfigBuilder.allowedRequestHeaders(corsOptions.getAllowedHeaders().toArray(new String[0]));
        }
        this.corsConfig = corsConfigBuilder.build();
    }

    private void mergeStaticOptions(StaticOptions staticOptions, Environment environment) {
        int cacheSeconds = staticOptions.getCacheSeconds();
        if (cacheSeconds > 0 && StaticOptions.DEFAULT_CACHE_SECONDS != cacheSeconds) {
            environment.set(ENV_KEY_STATIC_CACHE_SECONDS, cacheSeconds);
        } else {
            cacheSeconds = environment.getInt(ENV_KEY_STATIC_CACHE_SECONDS, StaticOptions.DEFAULT_CACHE_SECONDS);
            staticOptions.setCacheSeconds(cacheSeconds);
        }

        boolean showList = staticOptions.isShowList();
        if (showList) {
            environment.set(ENV_KEY_STATIC_LIST, true);
        } else {
            showList = environment.getBoolean(ENV_KEY_STATIC_LIST, Boolean.FALSE);
            staticOptions.setShowList(showList);
        }
    }

    private void mergeHttpOptions(HttpOptions httpOptions, Environment environment) {
        this.maxContentSize = httpOptions.getMaxContentSize();
        this.enableGzip = httpOptions.isEnableGzip();

        if (this.maxContentSize != HttpOptions.DEFAULT_MAX_CONTENT_SIZE) {
            environment.set(ENV_KEY_HTTP_MAX_CONTENT, this.maxContentSize);
        } else {
            this.maxContentSize = environment.getInt(ENV_KEY_HTTP_MAX_CONTENT, HttpOptions.DEFAULT_MAX_CONTENT_SIZE);
            httpOptions.setMaxContentSize(maxContentSize);
        }

        if (this.enableGzip) {
            environment.set(ENV_KEY_GZIP_ENABLE, true);
        } else {
            this.enableGzip = environment.getBoolean(ENV_KEY_GZIP_ENABLE, false);
            httpOptions.setEnableGzip(enableGzip);
        }

        boolean requestCost = httpOptions.isEnableRequestCost();
        if (requestCost) {
            environment.set(ENV_KEY_HTTP_REQUEST_COST, true);
        } else {
            requestCost = environment.getBoolean(ENV_KEY_HTTP_REQUEST_COST, false);
            httpOptions.setEnableRequestCost(requestCost);
        }

        boolean enableSession = httpOptions.isEnableSession();
        if (enableSession) {
            environment.set(ENV_KEY_SESSION_ENABLED, true);
        } else {
            enableSession = environment.getBoolean(ENV_KEY_SESSION_ENABLED, false);
            httpOptions.setEnableSession(enableSession);
        }
    }

}
