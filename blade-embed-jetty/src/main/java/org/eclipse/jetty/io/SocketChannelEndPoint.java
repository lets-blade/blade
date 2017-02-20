//
//  ========================================================================
//  Copyright (c) 1995-2016 Mort Bay Consulting Pty. Ltd.
//  ------------------------------------------------------------------------
//  All rights reserved. This program and the accompanying materials
//  are made available under the terms of the Eclipse Public License v1.0
//  and Apache License v2.0 which accompanies this distribution.
//
//      The Eclipse Public License is available at
//      http://www.eclipse.org/legal/epl-v10.html
//
//      The Apache License v2.0 is available at
//      http://www.opensource.org/licenses/apache2.0.php
//
//  You may elect to redistribute this code under either of these licenses.
//  ========================================================================
//

package org.eclipse.jetty.io;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Scheduler;

public class SocketChannelEndPoint extends ChannelEndPoint
{
    private static final Logger LOG = Log.getLogger(SocketChannelEndPoint.class);
    private final Socket _socket;
    private final InetSocketAddress _local;
    private final InetSocketAddress _remote;

    public SocketChannelEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler)
    {
        this((SocketChannel)channel,selector,key,scheduler);
    }
    
    public SocketChannelEndPoint(SocketChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler)
    {
        super(channel,selector,key,scheduler);
        
        _socket=channel.socket();
        _local=(InetSocketAddress)_socket.getLocalSocketAddress();
        _remote=(InetSocketAddress)_socket.getRemoteSocketAddress();
    }

    public Socket getSocket()
    {
        return _socket;
    }

    public InetSocketAddress getLocalAddress()
    {
        return _local;
    }

    public InetSocketAddress getRemoteAddress()
    {
        return _remote;
    }
    
    @Override
    protected void doShutdownOutput()
    {
        try
        {
            if (!_socket.isOutputShutdown())
                _socket.shutdownOutput();
        }
        catch (IOException e)
        {
            LOG.debug(e);
        }
    }
}
