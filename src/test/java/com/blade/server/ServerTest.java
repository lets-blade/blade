package com.blade.server;

import com.blade.Blade;
import com.blade.server.netty.NettyServer;
import org.junit.Test;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ServerTest {

    @Test
    public void testCreateServer() throws Exception {
        Server server = new NettyServer();
        server.start(Blade.me().listen(10086), null);
        server.stop();
    }

}
