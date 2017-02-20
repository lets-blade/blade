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
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.List;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Scheduler;

public class NetworkTrafficSelectChannelEndPoint extends SelectChannelEndPoint
{
    private static final Logger LOG = Log.getLogger(NetworkTrafficSelectChannelEndPoint.class);

    private final List<NetworkTrafficListener> listeners;

    public NetworkTrafficSelectChannelEndPoint(SocketChannel channel, ManagedSelector selectSet, SelectionKey key, Scheduler scheduler, long idleTimeout, List<NetworkTrafficListener> listeners) throws IOException
    {
        super(channel, selectSet, key, scheduler, idleTimeout);
        this.listeners = listeners;
    }

    @Override
    public int fill(ByteBuffer buffer) throws IOException
    {
        int read = super.fill(buffer);
        notifyIncoming(buffer, read);
        return read;
    }

    @Override
    public boolean flush(ByteBuffer... buffers) throws IOException
    {
        boolean flushed=true;
        for (ByteBuffer b : buffers)
        {
            if (b.hasRemaining())
            {
                int position = b.position();
                ByteBuffer view=b.slice();
                flushed&=super.flush(b);
                int l=b.position()-position;
                view.limit(view.position()+l);
                notifyOutgoing(view);
                if (!flushed)
                    break;
            }
        }
        return flushed;
    }

    

    @Override
    public void onOpen()
    {
        super.onOpen();
        if (listeners != null && !listeners.isEmpty())
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    listener.opened(getSocket());
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }

    @Override
    public void onClose()
    {
        super.onClose();
        if (listeners != null && !listeners.isEmpty())
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    listener.closed(getSocket());
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }


    public void notifyIncoming(ByteBuffer buffer, int read)
    {
        if (listeners != null && !listeners.isEmpty() && read > 0)
        {
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    ByteBuffer view = buffer.asReadOnlyBuffer();
                    listener.incoming(getSocket(), view);
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }

    public void notifyOutgoing(ByteBuffer view)
    {
        if (listeners != null && !listeners.isEmpty() && view.hasRemaining())
        {
            Socket socket=getSocket();
            for (NetworkTrafficListener listener : listeners)
            {
                try
                {
                    listener.outgoing(socket, view);   
                }
                catch (Exception x)
                {
                    LOG.warn(x);
                }
            }
        }
    }

}
