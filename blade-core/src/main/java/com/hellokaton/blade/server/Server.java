package com.hellokaton.blade.server;

import com.hellokaton.blade.Blade;

/**
 * Blade web server
 *
 * @author biezhi
 * 2017/6/6
 */
public interface Server {

    /**
     * Start blade application
     *
     * @param blade blade instance
     * @throws Exception
     */
    void start(Blade blade) throws Exception;

    /**
     * Join current server
     *
     * @throws Exception
     */
    void join() throws Exception;

    /**
     * Stop current server
     */
    void stop();

    /**
     * Stop current, Will have been waiting for the service to stop
     */
    void stopAndWait();

}
