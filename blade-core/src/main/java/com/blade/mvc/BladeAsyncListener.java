package com.blade.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import java.io.IOException;

/**
 * Created by biezhi on 2017/2/20.
 */
public class BladeAsyncListener implements AsyncListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BladeAsyncListener.class);

    @Override
    public void onComplete(AsyncEvent event) throws IOException {
    }

    @Override
    public void onTimeout(AsyncEvent event) throws IOException {
        LOGGER.warn("asyn request timeout: {}", event.getAsyncContext().getTimeout());
    }

    @Override
    public void onError(AsyncEvent event) throws IOException {
        LOGGER.warn("asyn request error", event.getThrowable());
    }

    @Override
    public void onStartAsync(AsyncEvent event) throws IOException {
    }
}
