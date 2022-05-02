package com.blade.server.netty;

import com.blade.kit.NamedThreadFactory;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import lombok.var;

/**
 * Epoll kit
 * <p>
 * enable epool event loop group
 *
 * @author biezhi
 * @date 2017/9/22
 */
class EpollKit {

    static NettyServerGroup group(int threadCount, int workers) {
        var bossGroup   = new EpollEventLoopGroup(threadCount, new NamedThreadFactory("epoll-boss@"));
        var workerGroup = new EpollEventLoopGroup(workers, new NamedThreadFactory("epoll-worker@"));
        return NettyServerGroup.builder().boosGroup(bossGroup).workerGroup(workerGroup).socketChannel(EpollServerSocketChannel.class).build();
    }

}
