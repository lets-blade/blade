package com.hellokaton.blade.server;

import com.hellokaton.blade.Blade;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ServerTest {

    @Test
    public void testCreateServer() throws Exception {
        Server server = new NettyServer();
        server.start(Blade.create().listen(10086));
        server.stop();
    }

    @Test
    public void testStart() throws Exception {
        NettyServer nettyServer = new NettyServer();
        nettyServer.start(Blade.create().listen(10087));
        nettyServer.stop();
    }
}
