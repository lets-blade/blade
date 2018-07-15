package com.blade.server.netty;

import com.blade.Blade;
import com.blade.kit.DateKit;
import com.blade.kit.StringKit;
import com.blade.mvc.Const;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.util.AsciiString;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * HttpServerInitializer
 */
@Slf4j
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServerDispatcher HTTP_SERVER_HANDLER = new HttpServerDispatcher();

    private final SslContext sslCtx;
    private final Blade      blade;
    private final boolean    enableCors;
    private final boolean    isWebSocket;
    private final boolean    useGZIP;

    public static volatile CharSequence date = new AsciiString(DateKit.gmtDate(LocalDateTime.now()));

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

        service.scheduleWithFixedDelay(() -> date = new AsciiString(DateKit.gmtDate(LocalDateTime.now())), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline p = ch.pipeline();
        try {
            if (sslCtx != null) {
                p.addLast(sslCtx.newHandler(ch.alloc()));
            }

            p.addLast(new HttpServerCodec(36192 * 2, 36192 * 8, 36192 * 16, false));
            p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
            p.addLast(new HttpServerExpectContinueHandler());

            if (useGZIP) {
                p.addLast(new HttpContentCompressor());
            }

            p.addLast(new ChunkedWriteHandler());

            if (enableCors) {
                p.addLast(new CorsHandler(CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build()));
            }
            if (isWebSocket) {
                p.addLast(new WebSocketServerProtocolHandler(blade.webSocketPath(), null, true));
                p.addLast(WEB_SOCKET_HANDLER);
            }
            p.addLast(HTTP_SERVER_HANDLER);
        } catch (Exception e) {
            log.error("Add channel pipeline error", e);
        }
    }

}
