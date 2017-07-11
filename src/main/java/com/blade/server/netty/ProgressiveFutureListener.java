package com.blade.server.netty;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.io.RandomAccessFile;

/**
 * File progressive future listener
 *
 * @author biezhi
 */
@Slf4j
public class ProgressiveFutureListener implements ChannelProgressiveFutureListener {

    private RandomAccessFile raf;

    public ProgressiveFutureListener(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        if (total < 0) { // total unknown
            log.debug("{} Transfer progress: {}", future.channel(), progress);
        } else {
            log.debug("{} Transfer progress: {}/{}", future.channel(), progress, total);
        }
    }

    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        try {
            raf.close();
            log.debug("{} Transfer complete.", future.channel());
        } catch (Exception e) {
            log.error("RandomAccessFile close error", e);
        }
    }

    public static ProgressiveFutureListener build(RandomAccessFile raf) {
        return new ProgressiveFutureListener(raf);
    }

}