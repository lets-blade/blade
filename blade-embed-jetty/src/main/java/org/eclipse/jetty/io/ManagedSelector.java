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

import java.io.Closeable;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.eclipse.jetty.util.component.AbstractLifeCycle;
import org.eclipse.jetty.util.component.ContainerLifeCycle;
import org.eclipse.jetty.util.component.Dumpable;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.ExecutionStrategy;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Invocable.InvocationType;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.thread.Scheduler;
import org.eclipse.jetty.util.thread.strategy.ExecuteProduceConsume;
import org.eclipse.jetty.util.thread.strategy.ProduceExecuteConsume;

/**
 * <p>{@link ManagedSelector} wraps a {@link Selector} simplifying non-blocking operations on channels.</p>
 * <p>{@link ManagedSelector} runs the select loop, which waits on {@link Selector#select()} until events
 * happen for registered channels. When events happen, it notifies the {@link EndPoint} associated
 * with the channel.</p>
 */
public class ManagedSelector extends AbstractLifeCycle implements Dumpable
{
    private static final Logger LOG = Log.getLogger(ManagedSelector.class);

    private final Locker _locker = new Locker();
    private boolean _selecting = false;
    private final Queue<Runnable> _actions = new ArrayDeque<>();
    private final SelectorManager _selectorManager;
    private final int _id;
    private final ExecutionStrategy _strategy;
    private final ExecutionStrategy _lowPriorityStrategy;
    private Selector _selector;

    public ManagedSelector(SelectorManager selectorManager, int id)
    {
        _selectorManager = selectorManager;
        _id = id;
        SelectorProducer producer = new SelectorProducer();
        Executor executor = selectorManager.getExecutor();
        _strategy = new ExecuteProduceConsume(producer, executor, InvocationType.BLOCKING);
        _lowPriorityStrategy = new LowPriorityProduceExecuteConsume(producer, executor);
        setStopTimeout(5000);
    }

    @Override
    protected void doStart() throws Exception
    {
        super.doStart();

        _selector = _selectorManager.newSelector();

        // The producer used by the strategies will never
        // be idle (either produces a task or blocks).

        // The normal strategy obtains the produced task, schedules
        // a new thread to produce more, runs the task and then exits.
        _selectorManager.execute(_strategy::produce);

        // The low priority strategy knows the producer will never
        // be idle, that tasks are scheduled to run in different
        // threads, therefore lowPriorityProduce() never exits.
        _selectorManager.execute(this::lowPriorityProduce);
    }

    private void lowPriorityProduce()
    {
        Thread current = Thread.currentThread();
        String name = current.getName();
        int priority = current.getPriority();
        current.setPriority(Thread.MIN_PRIORITY);
        current.setName(name+"-lowPrioritySelector");
        try
        {
            _lowPriorityStrategy.produce();
        }
        finally
        {
            current.setPriority(priority);
            current.setName(name);
        }
    }

    public int size()
    {
        Selector s = _selector;
        if (s == null)
            return 0;
        return s.keys().size();
    }

    @Override
    protected void doStop() throws Exception
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Stopping {}", this);
        CloseEndPoints close_endps = new CloseEndPoints();
        submit(close_endps);
        close_endps.await(getStopTimeout());
        super.doStop();
        CloseSelector close_selector = new CloseSelector();
        submit(close_selector);
        close_selector.await(getStopTimeout());

        if (LOG.isDebugEnabled())
            LOG.debug("Stopped {}", this);
    }

    public void submit(Runnable change)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("Queued change {} on {}", change, this);

        Selector selector = null;
        try (Locker.Lock lock = _locker.lock())
        {
            _actions.offer(change);
            if (_selecting)
            {
                selector = _selector;
                // To avoid the extra select wakeup.
                _selecting = false;
            }
        }
        if (selector != null)
            selector.wakeup();
    }

    /**
     * A {@link Selectable} is an {@link EndPoint} that wish to be
     * notified of non-blocking events by the {@link ManagedSelector}.
     */
    public interface Selectable
    {
        /**
         * Callback method invoked when a read or write events has been
         * detected by the {@link ManagedSelector} for this endpoint.
         *
         * @return a job that may block or null
         */
        Runnable onSelected();

        /**
         * Callback method invoked when all the keys selected by the
         * {@link ManagedSelector} for this endpoint have been processed.
         */
        void updateKey();
    }

    private static class LowPriorityProduceExecuteConsume extends ProduceExecuteConsume
    {
        private LowPriorityProduceExecuteConsume(SelectorProducer producer, Executor executor)
        {
            super(producer, executor, InvocationType.BLOCKING);
        }

        @Override
        protected boolean execute(Runnable task)
        {
            try
            {
                InvocationType invocation=Invocable.getInvocationType(task);
                if (LOG.isDebugEnabled())
                    LOG.debug("Low Priority Selector executing {} {}",invocation,task);
                switch (invocation)
                {
                    case NON_BLOCKING:
                        task.run();
                        return true;

                    case EITHER:
                        Invocable.invokeNonBlocking(task);
                        return true;

                    default:
                        return super.execute(task);
                }
            }
            finally
            {
                // Allow opportunity for main strategy to take over.
                Thread.yield();
            }
        }
    }

    private class SelectorProducer implements ExecutionStrategy.Producer
    {
        private Set<SelectionKey> _keys = Collections.emptySet();
        private Iterator<SelectionKey> _cursor = Collections.emptyIterator();

        @Override
        public Runnable produce()
        {
            // This method is called from both the
            // normal and low priority strategies.
            // Only one can produce at a time, so it's synchronized
            // to enforce that only one strategy actually produces.
            // When idle in select(), this method blocks;
            // the other strategy's thread will be blocked
            // waiting for this lock to be released.
            synchronized (this)
            {
                while (true)
                {
                    Runnable task = processSelected();
                    if (task != null)
                        return task;

                    Runnable action = nextAction();
                    if (action != null)
                        return action;

                    update();

                    if (!select())
                        return null;
                }
            }
        }

        private Runnable nextAction()
        {
            while (true)
            {
                Runnable action;
                try (Locker.Lock lock = _locker.lock())
                {
                    action = _actions.poll();
                    if (action == null)
                    {
                        // No more actions, so we need to select
                        _selecting = true;
                        return null;
                    }
                }

                if (Invocable.getInvocationType(action)==InvocationType.BLOCKING)
                    return action;

                try
                {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Running action {}", action);
                    // Running the change may queue another action.
                    action.run();
                }
                catch (Throwable x)
                {
                    LOG.debug("Could not run action " + action, x);
                }
            }
        }

        private boolean select()
        {
            try
            {
                Selector selector = _selector;
                if (selector != null && selector.isOpen())
                {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Selector loop waiting on select");
                    int selected = selector.select();
                    if (LOG.isDebugEnabled())
                        LOG.debug("Selector loop woken up from select, {}/{} selected", selected, selector.keys().size());

                    try (Locker.Lock lock = _locker.lock())
                    {
                        // finished selecting
                        _selecting = false;
                    }

                    _keys = selector.selectedKeys();
                    _cursor = _keys.iterator();

                    return true;
                }
            }
            catch (Throwable x)
            {
                closeNoExceptions(_selector);
                if (isRunning())
                    LOG.warn(x);
                else
                    LOG.debug(x);
            }
            return false;
        }

        private Runnable processSelected()
        {
            while (_cursor.hasNext())
            {
                SelectionKey key = _cursor.next();
                if (key.isValid())
                {
                    Object attachment = key.attachment();
                    if (LOG.isDebugEnabled())
                        LOG.debug("selected {} {} ",key,attachment);
                    try
                    {
                        if (attachment instanceof Selectable)
                        {
                            // Try to produce a task
                            Runnable task = ((Selectable)attachment).onSelected();
                            if (task != null)
                                return task;
                        }
                        else if (key.isConnectable())
                        {
                            Runnable task = processConnect(key, (Connect)attachment);
                            if (task != null)
                                return task;
                        }
                        else if (key.isAcceptable())
                        {
                            processAccept(key);
                        }
                        else
                        {
                            throw new IllegalStateException("key=" + key + ", att=" + attachment + ", iOps=" + key.interestOps() + ", rOps=" + key.readyOps());
                        }
                    }
                    catch (CancelledKeyException x)
                    {
                        LOG.debug("Ignoring cancelled key for channel {}", key.channel());
                        if (attachment instanceof EndPoint)
                            closeNoExceptions((EndPoint)attachment);
                    }
                    catch (Throwable x)
                    {
                        LOG.warn("Could not process key for channel " + key.channel(), x);
                        if (attachment instanceof EndPoint)
                            closeNoExceptions((EndPoint)attachment);
                    }
                }
                else
                {
                    if (LOG.isDebugEnabled())
                        LOG.debug("Selector loop ignoring invalid key for channel {}", key.channel());
                    Object attachment = key.attachment();
                    if (attachment instanceof EndPoint)
                        closeNoExceptions((EndPoint)attachment);
                }
            }
            return null;
        }

        private void update()
        {
            for (SelectionKey key : _keys)
                updateKey(key);
            _keys.clear();
        }

        private void updateKey(SelectionKey key)
        {
            Object attachment = key.attachment();
            if (attachment instanceof Selectable)
                ((Selectable)attachment).updateKey();
        }
    }
    
    private abstract static class NonBlockingAction implements Runnable, Invocable
    {
        @Override
        public final InvocationType getInvocationType()
        {
            return InvocationType.NON_BLOCKING;
        }
    }

    private Runnable processConnect(SelectionKey key, final Connect connect)
    {
        SelectableChannel channel = key.channel();
        try
        {
            key.attach(connect.attachment);
            boolean connected = _selectorManager.doFinishConnect(channel);
            if (LOG.isDebugEnabled())
                LOG.debug("Connected {} {}", connected, channel);
            if (connected)
            {
                if (connect.timeout.cancel())
                {
                    key.interestOps(0);
                    return new CreateEndPoint(channel, key)
                    {
                        @Override
                        protected void failed(Throwable failure)
                        {
                            super.failed(failure);
                            connect.failed(failure);
                        }
                    };
                }
                else
                {
                    throw new SocketTimeoutException("Concurrent Connect Timeout");
                }
            }
            else
            {
                throw new ConnectException();
            }
        }
        catch (Throwable x)
        {
            connect.failed(x);
            return null;
        }
    }

    private void processAccept(SelectionKey key)
    {
        SelectableChannel server = key.channel();
        SelectableChannel channel = null;
        try
        {
            channel = _selectorManager.doAccept(server);
            if (channel!=null)
                _selectorManager.accepted(channel);
        }
        catch (Throwable x)
        {
            closeNoExceptions(channel);
            LOG.warn("Accept failed for channel " + channel, x);
        }
    }

    private void closeNoExceptions(Closeable closeable)
    {
        try
        {
            if (closeable != null)
                closeable.close();
        }
        catch (Throwable x)
        {
            LOG.ignore(x);
        }
    }

    private EndPoint createEndPoint(SelectableChannel channel, SelectionKey selectionKey) throws IOException
    {
        EndPoint endPoint = _selectorManager.newEndPoint(channel, this, selectionKey);
        endPoint.onOpen();
        _selectorManager.endPointOpened(endPoint);
        Connection connection = _selectorManager.newConnection(channel, endPoint, selectionKey.attachment());
        endPoint.setConnection(connection);
        selectionKey.attach(endPoint);
        _selectorManager.connectionOpened(connection);
        if (LOG.isDebugEnabled())
            LOG.debug("Created {}", endPoint);
        return endPoint;
    }

    public void destroyEndPoint(final EndPoint endPoint)
    {
        final Connection connection = endPoint.getConnection();
        submit(() ->
        {
            if (LOG.isDebugEnabled())
                LOG.debug("Destroyed {}", endPoint);
            if (connection != null)
                _selectorManager.connectionClosed(connection);
            _selectorManager.endPointClosed(endPoint);
        });
    }

    @Override
    public String dump()
    {
        return ContainerLifeCycle.dump(this);
    }

    @Override
    public void dump(Appendable out, String indent) throws IOException
    {
        out.append(String.valueOf(this)).append(" id=").append(String.valueOf(_id)).append(System.lineSeparator());

        Selector selector = _selector;
        if (selector != null && selector.isOpen())
        {
            final ArrayList<Object> dump = new ArrayList<>(selector.keys().size() * 2);

            DumpKeys dumpKeys = new DumpKeys(dump);
            submit(dumpKeys);
            dumpKeys.await(5, TimeUnit.SECONDS);

            ContainerLifeCycle.dump(out, indent, dump);
        }
    }

    @Override
    public String toString()
    {
        Selector selector = _selector;
        return String.format("%s id=%s keys=%d selected=%d",
                super.toString(),
                _id,
                selector != null && selector.isOpen() ? selector.keys().size() : -1,
                selector != null && selector.isOpen() ? selector.selectedKeys().size() : -1);
    }

    private class DumpKeys implements Runnable
    {
        private final CountDownLatch latch = new CountDownLatch(1);
        private final List<Object> _dumps;

        private DumpKeys(List<Object> dumps)
        {
            this._dumps = dumps;
        }

        @Override
        public void run()
        {
            Selector selector = _selector;
            if (selector != null && selector.isOpen())
            {
                Set<SelectionKey> keys = selector.keys();
                _dumps.add(selector + " keys=" + keys.size());
                for (SelectionKey key : keys)
                {
                    try
                    {
                        _dumps.add(String.format("SelectionKey@%x{i=%d}->%s", key.hashCode(), key.interestOps(), key.attachment()));
                    }
                    catch (Throwable x)
                    {
                        LOG.ignore(x);
                    }
                }
            }
            latch.countDown();
        }

        public boolean await(long timeout, TimeUnit unit)
        {
            try
            {
                return latch.await(timeout, unit);
            }
            catch (InterruptedException x)
            {
                return false;
            }
        }
    }

    class Acceptor extends NonBlockingAction
    {
        private final SelectableChannel _channel;

        public Acceptor(SelectableChannel channel)
        {
            this._channel = channel;
        }

        @Override
        public void run()
        {
            try
            {
                SelectionKey key = _channel.register(_selector, SelectionKey.OP_ACCEPT, null);
                if (LOG.isDebugEnabled())
                    LOG.debug("{} acceptor={}", this, key);
            }
            catch (Throwable x)
            {
                closeNoExceptions(_channel);
                LOG.warn(x);
            }
        }
    }

    class Accept extends NonBlockingAction implements Closeable
    {
        private final SelectableChannel channel;
        private final Object attachment;

        Accept(SelectableChannel channel, Object attachment)
        {
            this.channel = channel;
            this.attachment = attachment;
        }

        @Override
        public void close()
        {
            LOG.debug("closed accept of {}", channel);
            closeNoExceptions(channel);
        }

        @Override
        public void run()
        {
            try
            {
                final SelectionKey key = channel.register(_selector, 0, attachment);
                submit(new CreateEndPoint(channel, key));
            }
            catch (Throwable x)
            {
                closeNoExceptions(channel);
                LOG.debug(x);
            }
        }
    }

    private class CreateEndPoint implements Runnable, Closeable
    {
        private final SelectableChannel channel;
        private final SelectionKey key;

        public CreateEndPoint(SelectableChannel channel, SelectionKey key)
        {
            this.channel = channel;
            this.key = key;
        }

        @Override
        public void run()
        {
            try
            {
                createEndPoint(channel, key);
            }
            catch (Throwable x)
            {
                LOG.debug(x);
                failed(x);
            }
        }

        @Override
        public void close()
        {
            LOG.debug("closed creation of {}", channel);
            closeNoExceptions(channel);
        }

        protected void failed(Throwable failure)
        {
            closeNoExceptions(channel);
            LOG.debug(failure);
        }
    }

    class Connect extends NonBlockingAction
    {
        private final AtomicBoolean failed = new AtomicBoolean();
        private final SelectableChannel channel;
        private final Object attachment;
        private final Scheduler.Task timeout;

        Connect(SelectableChannel channel, Object attachment)
        {
            this.channel = channel;
            this.attachment = attachment;
            this.timeout = ManagedSelector.this._selectorManager.getScheduler().schedule(new ConnectTimeout(this), ManagedSelector.this._selectorManager.getConnectTimeout(), TimeUnit.MILLISECONDS);
        }

        @Override
        public void run()
        {
            try
            {
                channel.register(_selector, SelectionKey.OP_CONNECT, this);
            }
            catch (Throwable x)
            {
                failed(x);
            }
        }

        private void failed(Throwable failure)
        {
            if (failed.compareAndSet(false, true))
            {
                timeout.cancel();
                closeNoExceptions(channel);
                ManagedSelector.this._selectorManager.connectionFailed(channel, failure, attachment);
            }
        }
    }

    private class ConnectTimeout extends NonBlockingAction
    {
        private final Connect connect;

        private ConnectTimeout(Connect connect)
        {
            this.connect = connect;
        }

        @Override
        public void run()
        {
            SelectableChannel channel = connect.channel;
            if (_selectorManager.isConnectionPending(channel))
            {
                if (LOG.isDebugEnabled())
                    LOG.debug("Channel {} timed out while connecting, closing it", channel);
                connect.failed(new SocketTimeoutException("Connect Timeout"));
            }
        }
    }

    private class CloseEndPoints extends NonBlockingAction
    {
        private final CountDownLatch _latch = new CountDownLatch(1);
        private CountDownLatch _allClosed;

        @Override
        public void run()
        {
            List<EndPoint> end_points = new ArrayList<>();
            for (SelectionKey key : _selector.keys())
            {
                if (key.isValid())
                {
                    Object attachment = key.attachment();
                    if (attachment instanceof EndPoint)
                        end_points.add((EndPoint)attachment);
                }
            }

            int size = end_points.size();
            if (LOG.isDebugEnabled())
                LOG.debug("Closing {} endPoints on {}", size, ManagedSelector.this);

            _allClosed = new CountDownLatch(size);
            _latch.countDown();

            for (EndPoint endp : end_points)
                submit(new EndPointCloser(endp, _allClosed));

            if (LOG.isDebugEnabled())
                LOG.debug("Closed {} endPoints on {}", size, ManagedSelector.this);
        }

        public boolean await(long timeout)
        {
            try
            {
                return _latch.await(timeout, TimeUnit.MILLISECONDS) &&
                        _allClosed.await(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException x)
            {
                return false;
            }
        }
    }

    private class EndPointCloser implements Runnable
    {
        private final EndPoint _endPoint;
        private final CountDownLatch _latch;

        private EndPointCloser(EndPoint endPoint, CountDownLatch latch)
        {
            _endPoint = endPoint;
            _latch = latch;
        }

        @Override
        public void run()
        {
            closeNoExceptions(_endPoint.getConnection());
            _latch.countDown();
        }
    }

    private class CloseSelector extends NonBlockingAction
    {
        private CountDownLatch _latch = new CountDownLatch(1);

        @Override
        public void run()
        {
            Selector selector = _selector;
            _selector = null;
            closeNoExceptions(selector);
            _latch.countDown();
        }

        public boolean await(long timeout)
        {
            try
            {
                return _latch.await(timeout, TimeUnit.MILLISECONDS);
            }
            catch (InterruptedException x)
            {
                return false;
            }
        }
    }

    public Selector getSelector()
    {
        return _selector;        
    }
}
