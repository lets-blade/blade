package com.blade.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by biezhi on 2017/2/20.
 */
public class WorkerContextListener implements ServletContextListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(WorkerContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        // create the thread pool
        ThreadPoolExecutor executor = new ThreadPoolExecutor(100, 200, 50000L,
                TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(100));
        servletContextEvent.getServletContext().setAttribute("executor",
                executor);
        LOGGER.info("init worker thread pool.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) sce.getServletContext().getAttribute("executor");
        executor.shutdown();
        LOGGER.info("shutdown worker thread pool.");
    }
}
