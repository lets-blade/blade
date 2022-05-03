package com.blade.server;

import com.blade.Blade;
import com.blade.kit.DateKit;
import com.blade.mvc.Const;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
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

    private final HttpServerHandler httpServerHandler;

    private final SslContext sslCtx;
    private final boolean useGZIP;

    public static volatile String date = DateKit.gmtDate(LocalDateTime.now());


    public HttpServerInitializer(SslContext sslCtx, Blade blade, ScheduledExecutorService service) {
        this.sslCtx = sslCtx;
        this.useGZIP = blade.environment().getBoolean(Const.ENV_KEY_GZIP_ENABLE, false);
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

            pipeline.addLast(new HttpServerCodec());
            pipeline.addLast(new HttpObjectAggregator(1024 * 1024));
            pipeline.addLast(new HttpServerExpectContinueHandler());

            if (useGZIP) {
                pipeline.addLast(new HttpContentCompressor());
            }
//            pipeline.addLast(new ChunkedWriteHandler());
            pipeline.addLast(new FullHttpRequestDecode());
            pipeline.addLast(httpServerHandler);
        } catch (Exception e) {
            log.error("Add channel pipeline error", e);
        }
    }

}
