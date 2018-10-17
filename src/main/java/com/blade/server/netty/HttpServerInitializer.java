package com.blade.server.netty;

import com.blade.Blade;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * HttpServerInitializer
 */
@Slf4j
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServerHandler HTTP_SERVER_HANDLER = new HttpServerHandler();

    private final SslContext sslCtx;
    private final Blade      blade;
    private final boolean    enableCors;
    private final boolean    isWebSocket;
    private final boolean    useGZIP;

    public static volatile String date = DateKit.gmtDate(LocalDateTime.now());

    private static WebSocketHandler WEB_SOCKET_HANDLER;

    public HttpServerInitializer(SslContext sslCtx, Blade blade, ScheduledExecutorService service) {
        this.sslCtx = sslCtx;
        this.blade = blade;
        this.enableCors = blade.environment().getBoolean(Const.ENV_KEY_CORS_ENABLE, false);
        this.useGZIP = blade.environment().getBoolean(Const.ENV_KEY_GZIP_ENABLE, false);
        this.isWebSocket = StringKit.isNotEmpty(blade.webSocketPath());

        if (isWebSocket) {
            WEB_SOCKET_HANDLER = new WebSocketHandler(blade);
        }

        service.scheduleWithFixedDelay(() -> date = DateKit.gmtDate(LocalDateTime.now()), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        try {
            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }

            pipeline.addLast(new HttpRequestDecoder());
            pipeline.addLast(new HttpResponseEncoder());

            if (useGZIP) {
                pipeline.addLast(new HttpContentCompressor());
            }

            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new HttpServerExpectContinueHandler());

            if (enableCors) {
                pipeline.addLast(new CorsHandler(CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build()));
            }

            if (isWebSocket) {
                pipeline.addLast(new WebSocketServerProtocolHandler(blade.webSocketPath(), null, true));
                pipeline.addLast(WEB_SOCKET_HANDLER);
            }
            pipeline.addLast(HTTP_SERVER_HANDLER);
        } catch (Exception e) {
            log.error("Add channel pipeline error", e);
        }
    }

}
