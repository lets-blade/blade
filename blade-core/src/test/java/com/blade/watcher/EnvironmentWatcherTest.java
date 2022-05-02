package com.blade.watcher;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author biezhi
 * @date 2017/12/26
 */
public class EnvironmentWatcherTest {

    @Test
    public void testWatcher(){
        EnvironmentWatcher environmentWatcher = new EnvironmentWatcher();
        Thread thread = new Thread(environmentWatcher);
        thread.start();
        try {
            TimeUnit.SECONDS.sleep(2);
            thread.interrupt();
        } catch (Exception e){

        }
    }

}
