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
import java.net.SocketAddress;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.Executor;

import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Scheduler;

/**
 * <p>{@link SelectorManager} manages a number of {@link ManagedSelector}s that
 * simplify the non-blocking primitives provided by the JVM via the {@code java.nio} package.</p>
 * <p>{@link SelectorManager} subclasses implement methods to return protocol-specific
 * {@link EndPoint}s and {@link Connection}s.</p>
 */
public abstract class SelectorManager extends ContainerLifeCycle implements Dumpable
{
    public static final int DEFAULT_CONNECT_TIMEOUT = 15000;
    protected static final Logger LOG = Log.getLogger(SelectorManager.class);

    private final Executor executor;
    private final Scheduler scheduler;
    private final ManagedSelector[] _selectors;
    private long _connectTimeout = DEFAULT_CONNECT_TIMEOUT;
    private long _selectorIndex;

    protected SelectorManager(Executor executor, Scheduler scheduler)
    {
        this(executor, scheduler, (Runtime.getRuntime().availableProcessors() + 1) / 2);
    }

    protected SelectorManager(Executor executor, Scheduler scheduler, int selectors)
    {
        if (selectors <= 0)
            throw new IllegalArgumentException("No selectors");
        this.executor = executor;
        this.scheduler = scheduler;
        _selectors = new ManagedSelector[selectors];
    }

    public Executor getExecutor()
    {
        return executor;
    }

    public Scheduler getScheduler()
    {
        return scheduler;
    }

    /**
     * Get the connect timeout
     *
     * @return the connect timeout (in milliseconds)
     */
    public long getConnectTimeout()
    {
        return _connectTimeout;
    }

    /**
     * Set the connect timeout (in milliseconds)
     *
     * @param milliseconds the number of milliseconds for the timeout
     */
    public void setConnectTimeout(long milliseconds)
    {
        _connectTimeout = milliseconds;
    }

    /**
     * Executes the given task in a different thread.
     *
     * @param task the task to execute
     */
    protected void execute(Runnable task)
    {
        executor.execute(task);
    }

    /**
     * @return the number of selectors in use
     */
    public int getSelectorCount()
    {
        return _selectors.length;
    }

    private ManagedSelector chooseSelector(SelectableChannel channel)
    {
        // Ideally we would like to have all connections from the same client end
        // up on the same selector (to try to avoid smearing the data from a single
        // client over all cores), but because of proxies, the remote address may not
        // really be the client - so we have to hedge our bets to ensure that all
        // channels don't end up on the one selector for a proxy.
        ManagedSelector candidate1 = null;
        if (channel != null)
        {
            try
            {
                if (channel instanceof SocketChannel)
                {
                    SocketAddress remote = ((SocketChannel)channel).getRemoteAddress();
                    if (remote instanceof InetSocketAddress)
                    {
                        byte[] addr = ((InetSocketAddress)remote).getAddress().getAddress();
                        if (addr != null)
                        {
                            int s = addr[addr.length - 1] & 0xFF;
                            candidate1 = _selectors[s % getSelectorCount()];
                        }
                    }
                }
            }
            catch (IOException x)
            {
                LOG.ignore(x);
            }
        }

        // The ++ increment here is not atomic, but it does not matter,
        // so long as the value changes sometimes, then connections will
        // be distributed over the available selectors.
        long s = _selectorIndex++;
        int index = (int)(s % getSelectorCount());
        ManagedSelector candidate2 = _selectors[index];

        if (candidate1 == null || candidate1.size() >= candidate2.size() * 2)
            return candidate2;
        return candidate1;
    }

    /**
     * <p>Registers a channel to perform a non-blocking connect.</p>
     * <p>The channel must be set in non-blocking mode, {@link SocketChannel#connect(SocketAddress)}
     * must be called prior to calling this method, and the connect operation must not be completed
     * (the return value of {@link SocketChannel#connect(SocketAddress)} must be false).</p>
     *
     * @param channel    the channel to register
     * @param attachment the attachment object
     * @see #accept(SelectableChannel, Object)
     */
    public void connect(SelectableChannel channel, Object attachment)
    {
        ManagedSelector set = chooseSelector(channel);
        set.submit(set.new Connect(channel, attachment));
    }

    /**
     * @param channel the channel to accept
     * @see #accept(SelectableChannel, Object)
     */
    public void accept(SelectableChannel channel)
    {
        accept(channel, null);
    }

    /**
     * <p>Registers a channel to perform non-blocking read/write operations.</p>
     * <p>This method is called just after a channel has been accepted by {@link ServerSocketChannel#accept()},
     * or just after having performed a blocking connect via {@link Socket#connect(SocketAddress, int)}, or
     * just after a non-blocking connect via {@link SocketChannel#connect(SocketAddress)} that completed
     * successfully.</p>
     *
     * @param channel    the channel to register
     * @param attachment the attachment object
     */
    public void accept(SelectableChannel channel, Object attachment)
    {
        final ManagedSelector selector = chooseSelector(channel);
        selector.submit(selector.new Accept(channel, attachment));
    }

    /**
     * <p>Registers a server channel for accept operations.
     * When a {@link SocketChannel} is accepted from the given {@link ServerSocketChannel}
     * then the {@link #accepted(SelectableChannel)} method is called, which must be
     * overridden by a derivation of this class to handle the accepted channel
     *
     * @param server the server channel to register
     */
    public void acceptor(SelectableChannel server)
    {
        final ManagedSelector selector = chooseSelector(null);
        selector.submit(selector.new Acceptor(server));
    }

    /**
     * Callback method when a channel is accepted from the {@link ServerSocketChannel}
     * passed to {@link #acceptor(SelectableChannel)}.
     * The default impl throws an {@link UnsupportedOperationException}, so it must
     * be overridden by subclasses if a server channel is provided.
     *
     * @param channel the
     * @throws IOException if unable to accept channel
     */
    protected void accepted(SelectableChannel channel) throws IOException
    {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doStart() throws Exception
    {
        for (int i = 0; i < _selectors.length; i++)
        {
            ManagedSelector selector = newSelector(i);
            _selectors[i] = selector;
            addBean(selector);
        }
        super.doStart();
    }

    /**
     * <p>Factory method for {@link ManagedSelector}.</p>
     *
     * @param id an identifier for the {@link ManagedSelector to create}
     * @return a new {@link ManagedSelector}
     */
    protected ManagedSelector newSelector(int id)
    {
        return new ManagedSelector(this, id);
    }

    @Override
    protected void doStop() throws Exception
    {
        super.doStop();
        for (ManagedSelector selector : _selectors)
            removeBean(selector);
    }

    /**
     * <p>Callback method invoked when an endpoint is opened.</p>
     *
     * @param endpoint the endpoint being opened
     */
    protected void endPointOpened(EndPoint endpoint)
    {
    }

    /**
     * <p>Callback method invoked when an endpoint is closed.</p>
     *
     * @param endpoint the endpoint being closed
     */
    protected void endPointClosed(EndPoint endpoint)
    {
    }

    /**
     * <p>Callback method invoked when a connection is opened.</p>
     *
     * @param connection the connection just opened
     */
    public void connectionOpened(Connection connection)
    {
        try
        {
            connection.onOpen();
        }
        catch (Throwable x)
        {
            if (isRunning())
                LOG.warn("Exception while notifying connection " + connection, x);
            else
                LOG.debug("Exception while notifying connection " + connection, x);
            throw x;
        }
    }

    /**
     * <p>Callback method invoked when a connection is closed.</p>
     *
     * @param connection the connection just closed
     */
    public void connectionClosed(Connection connection)
    {
        try
        {
            connection.onClose();
        }
        catch (Throwable x)
        {
            LOG.debug("Exception while notifying connection " + connection, x);
        }
    }

    protected boolean doFinishConnect(SelectableChannel channel) throws IOException
    {
        return ((SocketChannel)channel).finishConnect();
    }

    protected boolean isConnectionPending(SelectableChannel channel)
    {
        return ((SocketChannel)channel).isConnectionPending();
    }

    protected SelectableChannel doAccept(SelectableChannel server) throws IOException
    {
        return ((ServerSocketChannel)server).accept();
    }


    /**
     * <p>Callback method invoked when a non-blocking connect cannot be completed.</p>
     * <p>By default it just logs with level warning.</p>
     *
     * @param channel    the channel that attempted the connect
     * @param ex         the exception that caused the connect to fail
     * @param attachment the attachment object associated at registration
     */
    protected void connectionFailed(SelectableChannel channel, Throwable ex, Object attachment)
    {
        LOG.warn(String.format("%s - %s", channel, attachment), ex);
    }

    protected Selector newSelector() throws IOException
    {
        return Selector.open();
    }

    /**
     * <p>Factory method to create {@link EndPoint}.</p>
     * <p>This method is invoked as a result of the registration of a channel via {@link #connect(SelectableChannel, Object)}
     * or {@link #accept(SelectableChannel)}.</p>
     *
     * @param channel      the channel associated to the endpoint
     * @param selector     the selector the channel is registered to
     * @param selectionKey the selection key
     * @return a new endpoint
     * @throws IOException if the endPoint cannot be created
     * @see #newConnection(SelectableChannel, EndPoint, Object)
     */
    protected abstract EndPoint newEndPoint(SelectableChannel channel, ManagedSelector selector, SelectionKey selectionKey) throws IOException;

    /**
     * <p>Factory method to create {@link Connection}.</p>
     *
     * @param channel    the channel associated to the connection
     * @param endpoint   the endpoint
     * @param attachment the attachment
     * @return a new connection
     * @throws IOException if unable to create new connection
     */
    public abstract Connection newConnection(SelectableChannel channel, EndPoint endpoint, Object attachment) throws IOException;
}
