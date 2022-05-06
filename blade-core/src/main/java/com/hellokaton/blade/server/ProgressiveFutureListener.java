package com.hellokaton.blade.server;

import io.netty.channel.ChannelProgressiveFuture;
import io.netty.channel.ChannelProgressiveFutureListener;
import lombok.extern.slf4j.Slf4j;

import java.nio.channels.FileChannel;

/**
 * File progressive future watcher
 *
 * @author biezhi
 */
@Slf4j
public class ProgressiveFutureListener implements ChannelProgressiveFutureListener {

    private final String fileName;
    private final FileChannel channel;

    public ProgressiveFutureListener(String fileName, FileChannel channel) {
        this.fileName = fileName;
        this.channel = channel;
    }

    @Override
    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
        if (total < 0) { // total unknown
            log.warn("File {} transfer progress: {}", fileName, progress);
        } else {
            log.debug("File {} transfer progress: {}/{}", fileName, progress, total);
        }

    }

    @Override
    public void operationComplete(ChannelProgressiveFuture future) {
        try {
            channel.close();
            log.info("File {} transfer complete.", fileName);
        } catch (Exception e) {
            log.error("File {} channel close error.", fileName, e);
        }
    }

    public static ProgressiveFutureListener create(String fileName, FileChannel channel) {
        return new ProgressiveFutureListener(fileName, channel);
    }

}