package com.blade.server;

import com.blade.Blade;

/**
 * Blade web server
 *
 * @author biezhi
 *         2017/6/6
 */
public interface Server {

    void start(Blade blade, String[] args) throws Exception;

    void join() throws Exception;

    void stop();

}
