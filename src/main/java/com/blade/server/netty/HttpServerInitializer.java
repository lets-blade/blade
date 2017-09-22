package com.blade.server.netty;

import com.blade.Blade;
import com.blade.mvc.Const;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpServerExpectContinueHandler;
import io.netty.handler.codec.http.cors.CorsConfig;
import io.netty.handler.codec.http.cors.CorsConfigBuilder;
import io.netty.handler.codec.http.cors.CorsHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * HttpServerInitializer
 */
public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final Blade   blade;
    private final boolean enableGzip;
    private final boolean enableCors;

    public HttpServerInitializer(Blade blade) {
        this.blade = blade;
        this.enableGzip = blade.environment().getBoolean(Const.ENV_KEY_GZIP_ENABLE, false);
        this.enableCors = blade.environment().getBoolean(Const.ENV_KEY_CORS_ENABLE, false);
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();
        if (enableGzip) {
            p.addLast(new HttpContentCompressor());
        }
        p.addLast(new HttpServerCodec(36192 * 2, 36192 * 8, 36192 * 16, false));
        p.addLast(new HttpServerExpectContinueHandler());
        p.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        p.addLast(new ChunkedWriteHandler());
        if (enableCors) {
            CorsConfig corsConfig = CorsConfigBuilder.forAnyOrigin().allowNullOrigin().allowCredentials().build();
            p.addLast(new CorsHandler(corsConfig));
        }
        p.addLast(new HttpServerHandler(blade));
    }
}
