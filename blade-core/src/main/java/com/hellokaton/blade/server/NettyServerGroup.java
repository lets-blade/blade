package com.hellokaton.blade.server;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import lombok.Builder;
import lombok.Getter;

/**
 * NettyServerGroup
 *
 * @author biezhi
 * @date 2017/9/22
 */
@Builder
@Getter
public class NettyServerGroup {

    private final Class<? extends ServerSocketChannel> socketChannel;
    private final MultithreadEventLoopGroup            boosGroup;
    private final MultithreadEventLoopGroup            workerGroup;
}
