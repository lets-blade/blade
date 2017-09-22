package com.blade.server.netty;

import io.netty.channel.MultithreadEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import lombok.Builder;
import lombok.Getter;

/**
 * @author biezhi
 * @date 2017/9/22
 */
@Builder
@Getter
public class NettyServerGroup {

    private Class<? extends ServerSocketChannel> socketChannel;
    private MultithreadEventLoopGroup boosGroup;
    private MultithreadEventLoopGroup workerGroup;
}
