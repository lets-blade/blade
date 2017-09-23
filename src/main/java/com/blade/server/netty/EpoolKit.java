package com.blade.server.netty;

import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;

import java.util.concurrent.ExecutorService;

/**
 * Epoll kit
 * <p>
 * enable epool event loop group
 *
 * @author biezhi
 * @date 2017/9/22
 */
public class EpoolKit {

    public static NettyServerGroup group(int threadCount, ExecutorService bossExecutors, int workers, ExecutorService workerExecutors) {
        EpollEventLoopGroup bossGroup   = new EpollEventLoopGroup(threadCount, bossExecutors);
        EpollEventLoopGroup workerGroup = new EpollEventLoopGroup(workers, workerExecutors);
        return NettyServerGroup.builder().boosGroup(bossGroup).workerGroup(workerGroup).socketChannel(EpollServerSocketChannel.class).build();
    }

}
