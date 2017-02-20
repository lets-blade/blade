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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.eclipse.jetty.util.annotation.ManagedAttribute;
import org.eclipse.jetty.util.annotation.ManagedObject;
import org.eclipse.jetty.util.annotation.ManagedOperation;
import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.statistic.CounterStatistic;
import org.eclipse.jetty.util.statistic.SampleStatistic;

/**
 * <p>A {@link Connection.Listener} that tracks connection statistics.</p>
 * <p>Adding an instance of this class as a bean to a server Connector
 * (for the server) or to HttpClient (for the client) will trigger the
 * tracking of the connection statistics for all connections managed
 * by the server Connector or by HttpClient.</p>
 */
@ManagedObject("Tracks statistics on connections")
public class ConnectionStatistics extends AbstractLifeCycle implements Connection.Listener, Dumpable
{
    private final CounterStatistic _connections = new CounterStatistic();
    private final SampleStatistic _connectionsDuration = new SampleStatistic();
    private final LongAdder _rcvdBytes = new LongAdder();
    private final AtomicLong _bytesInStamp = new AtomicLong();
    private final LongAdder _sentBytes = new LongAdder();
    private final AtomicLong _bytesOutStamp = new AtomicLong();
    private final LongAdder _messagesIn = new LongAdder();
    private final AtomicLong _messagesInStamp = new AtomicLong();
    private final LongAdder _messagesOut = new LongAdder();
    private final AtomicLong _messagesOutStamp = new AtomicLong();

    @ManagedOperation(value = "Resets the statistics", impact = "ACTION")
    public void reset()
    {
        _connections.reset();
        _connectionsDuration.reset();
        _rcvdBytes.reset();
        _bytesInStamp.set(System.nanoTime());
        _sentBytes.reset();
        _bytesOutStamp.set(System.nanoTime());
        _messagesIn.reset();
        _messagesInStamp.set(System.nanoTime());
        _messagesOut.reset();
        _messagesOutStamp.set(System.nanoTime());
    }

    @Override
    protected void doStart() throws Exception
    {
        reset();
    }

    @Override
    public void onOpened(Connection connection)
    {
        if (!isStarted())
            return;

        _connections.increment();
    }

    @Override
    public void onClosed(Connection connection)
    {
        if (!isStarted())
            return;

        _connections.decrement();

        long elapsed = System.currentTimeMillis() - connection.getCreatedTimeStamp();
        _connectionsDuration.set(elapsed);

        long bytesIn = connection.getBytesIn();
        if (bytesIn > 0)
            _rcvdBytes.add(bytesIn);
        long bytesOut = connection.getBytesOut();
        if (bytesOut > 0)
            _sentBytes.add(bytesOut);

        long messagesIn = connection.getMessagesIn();
        if (messagesIn > 0)
            _messagesIn.add(messagesIn);
        long messagesOut = connection.getMessagesOut();
        if (messagesOut > 0)
            _messagesOut.add(messagesOut);
    }

    @ManagedAttribute("Total number of bytes received by tracked connections")
    public long getReceivedBytes()
    {
        return _rcvdBytes.sum();
    }

    @ManagedAttribute("Total number of bytes received per second since the last invocation of this method")
    public long getReceivedBytesRate()
    {
        long now = System.nanoTime();
        long then = _bytesInStamp.getAndSet(now);
        long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return elapsed == 0 ? 0 : getReceivedBytes() * 1000 / elapsed;
    }

    @ManagedAttribute("Total number of bytes sent by tracked connections")
    public long getSentBytes()
    {
        return _sentBytes.sum();
    }

    @ManagedAttribute("Total number of bytes sent per second since the last invocation of this method")
    public long getSentBytesRate()
    {
        long now = System.nanoTime();
        long then = _bytesOutStamp.getAndSet(now);
        long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return elapsed == 0 ? 0 : getSentBytes() * 1000 / elapsed;
    }

    @ManagedAttribute("The max duration of a connection in ms")
    public long getConnectionDurationMax()
    {
        return _connectionsDuration.getMax();
    }

    @ManagedAttribute("The mean duration of a connection in ms")
    public double getConnectionDurationMean()
    {
        return _connectionsDuration.getMean();
    }

    @ManagedAttribute("The standard deviation of the duration of a connection")
    public double getConnectionDurationStdDev()
    {
        return _connectionsDuration.getStdDev();
    }

    @ManagedAttribute("The total number of connections opened")
    public long getConnectionsTotal()
    {
        return _connections.getTotal();
    }

    @ManagedAttribute("The current number of open connections")
    public long getConnections()
    {
        return _connections.getCurrent();
    }

    @ManagedAttribute("The max number of open connections")
    public long getConnectionsMax()
    {
        return _connections.getMax();
    }

    @ManagedAttribute("The total number of messages received")
    public long getReceivedMessages()
    {
        return _messagesIn.sum();
    }

    @ManagedAttribute("Total number of messages received per second since the last invocation of this method")
    public long getReceivedMessagesRate()
    {
        long now = System.nanoTime();
        long then = _messagesInStamp.getAndSet(now);
        long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return elapsed == 0 ? 0 : getReceivedMessages() * 1000 / elapsed;
    }

    @ManagedAttribute("The total number of messages sent")
    public long getSentMessages()
    {
        return _messagesOut.sum();
    }

    @ManagedAttribute("Total number of messages sent per second since the last invocation of this method")
    public long getSentMessagesRate()
    {
        long now = System.nanoTime();
        long then = _messagesOutStamp.getAndSet(now);
        long elapsed = TimeUnit.NANOSECONDS.toMillis(now - then);
        return elapsed == 0 ? 0 : getSentMessages() * 1000 / elapsed;
    }

    @Override
    public String dump()
    {
        return ContainerLifeCycle.dump(this);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException
    {
        ContainerLifeCycle.dumpObject(out, this);
        List<String> children = new ArrayList<>();
        children.add(String.format("connections=%s", _connections));
        children.add(String.format("durations=%s", _connectionsDuration));
        children.add(String.format("bytes in/out=%s/%s", getReceivedBytes(), getSentBytes()));
        children.add(String.format("messages in/out=%s/%s", getReceivedMessages(), getSentMessages()));
        ContainerLifeCycle.dump(out, indent, children);
    }

    @Override
    public String toString()
    {
        return String.format("%s@%x", getClass().getSimpleName(), hashCode());
    }
}
