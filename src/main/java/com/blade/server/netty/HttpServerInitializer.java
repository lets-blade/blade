package com.blade.server.netty;

import com.blade.Blade;
import com.blade.kit.DateKit;
import com.blade.mvc.Const;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http2.Http2ConnectionHandler;
import io.netty.handler.codec.http2.Http2ConnectionHandlerBuilder;
import io.netty.handler.codec.http2.Http2FrameAdapter;
import io.netty.handler.codec.http2.Http2FrameCodecBuilder;
import io.netty.handler.ssl.SslContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * HttpServerInitializer
 */
@Slf4j
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final HttpServerHandler httpServerHandler;

    private final SslContext sslCtx;
    private final Blade blade;
    private final boolean isWebSocket;
    private final boolean useGZIP;

    public static volatile String date = DateKit.gmtDate(LocalDateTime.now());


    public HttpServerInitializer(SslContext sslCtx, Blade blade, ScheduledExecutorService service) {
        this.sslCtx = sslCtx;
        this.blade = blade;
        this.useGZIP = blade.environment().getBoolean(Const.ENV_KEY_GZIP_ENABLE, false);
        this.isWebSocket = blade.routeMatcher().getWebSockets().size() > 0;
        this.httpServerHandler = new HttpServerHandler();

        service.scheduleWithFixedDelay(() -> date = DateKit.gmtDate(LocalDateTime.now()), 1000, 1000, TimeUnit.MILLISECONDS);
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        try {
            if (sslCtx != null) {
                pipeline.addLast(sslCtx.newHandler(ch.alloc()));
            }
//            Http2ConnectionHandler http2ConnectionHandler = (new Http2ConnectionHandlerBuilder().frameListener(new Http2FrameAdapter())).build();
//            pipeline.addLast(http2ConnectionHandler);
            pipeline.addLast(Http2FrameCodecBuilder.forServer().build());
            pipeline.addLast(new HttpServerExpectContinueHandler());

            if (useGZIP) {
                pipeline.addLast(new HttpContentCompressor());
            }

            if (isWebSocket) {
                pipeline.addLast(new WebSocketHandler(blade));
            }
            pipeline.addLast(new MergeRequestHandler());
            pipeline.addLast(httpServerHandler);
        } catch (Exception e) {
            log.error("Add channel pipeline error", e);
        }
    }

}
