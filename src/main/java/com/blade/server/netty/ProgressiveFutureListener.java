package com.blade.server.netty;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.RandomAccessFile;

public class ProgressiveFutureListener implements ChannelProgressiveFutureListener {

    private static final Logger log = LoggerFactory.getLogger(ProgressiveFutureListener.class);

    private RandomAccessFile raf;

    public ProgressiveFutureListener(RandomAccessFile raf) {
        this.raf = raf;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        if (total < 0) { // total unknown
            log.debug(future.channel() + " Transfer progress: " + progress);
        } else {
            log.debug(future.channel() + " Transfer progress: " + progress + " / " + total);
        }
    }

    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        try {
            raf.close();
            log.debug(future.channel() + " Transfer complete.");
        } catch (Exception e) {
            log.error("RandomAccessFile close error", e);
        }
    }

    public static ProgressiveFutureListener build(RandomAccessFile raf) {
        return new ProgressiveFutureListener(raf);
    }

}