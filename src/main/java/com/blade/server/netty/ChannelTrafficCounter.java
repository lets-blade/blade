package com.blade.server.netty;

import com.blade.metric.Connection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.traffic.ChannelTrafficShapingHandler;

import java.time.LocalDateTime;

public class ChannelTrafficCounter extends ChannelTrafficShapingHandler {

    private final Connection ci;

    public ChannelTrafficCounter(long checkInterval, Connection ci) {
        super(checkInterval);
        this.ci = ci;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        this.trafficCounter().start();
    }

    @Override
    public synchronized void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        this.trafficCounter().stop();
        ci.setClosed(LocalDateTime.now());
        ci.setBytesReceived(this.trafficCounter().cumulativeReadBytes());
        ci.setBytesSent(this.trafficCounter().cumulativeWrittenBytes());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        cause.printStackTrace();
    }
}
