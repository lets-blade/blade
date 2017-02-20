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
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.GatheringByteChannel;
import java.nio.channels.SelectionKey;

import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Invocable;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.thread.Scheduler;

/**
 * Channel End Point.
 * <p>Holds the channel and socket for an NIO endpoint.
 */
public abstract class ChannelEndPoint extends AbstractEndPoint implements ManagedSelector.Selectable
{
    private static final Logger LOG = Log.getLogger(ChannelEndPoint.class);

    private final Locker _locker = new Locker();
    private final ByteChannel _channel;
    private final GatheringByteChannel _gather;
    protected final ManagedSelector _selector;
    protected final SelectionKey _key;
    private boolean _updatePending;

    /**
     * The current value for {@link SelectionKey#interestOps()}.
     */
    protected int _currentInterestOps;

    /**
     * The desired value for {@link SelectionKey#interestOps()}.
     */
    protected int _desiredInterestOps;

    private abstract class RunnableTask  implements Runnable, Invocable
    {
        final String _operation;

        protected RunnableTask(String op)
        {
            _operation=op;
        }

        @Override
        public String toString()
        {
            return String.format("CEP:%s:%s:%s",ChannelEndPoint.this,_operation,getInvocationType());
        }
    }

    private abstract class RunnableCloseable extends RunnableTask implements Closeable
    {
        protected RunnableCloseable(String op)
        {
            super(op);
        }

        @Override
        public void close()
        {
            try
            {
                ChannelEndPoint.this.close();
            }
            catch (Throwable x)
            {
                LOG.warn(x);
            }
        }
    }

    private final Runnable _runUpdateKey = new RunnableTask("runUpdateKey")
    {
        @Override
        public InvocationType getInvocationType()
        {
            return InvocationType.NON_BLOCKING;
        }

        @Override
        public void run()
        {
            updateKey();
        }
    };

    private final Runnable _runFillable = new RunnableCloseable("runFillable")
    {
        @Override
        public InvocationType getInvocationType()
        {
            return getFillInterest().getCallbackInvocationType();
        }
        
        @Override
        public void run()
        {
            getFillInterest().fillable();
        }
    };

    private final Runnable _runCompleteWrite = new RunnableCloseable("runCompleteWrite")
    {
        @Override
        public InvocationType getInvocationType()
        {
            return getWriteFlusher().getCallbackInvocationType();
        }
        
        @Override
        public void run()
        {
            getWriteFlusher().completeWrite();
        }

        @Override
        public String toString()
        {
            return String.format("CEP:%s:%s:%s->%s",ChannelEndPoint.this,_operation,getInvocationType(),getWriteFlusher());
        }
        
    };

    private final Runnable _runCompleteWriteFillable = new RunnableCloseable("runCompleteWriteFillable")
    {
        @Override
        public InvocationType getInvocationType()
        {
            InvocationType fillT = getFillInterest().getCallbackInvocationType();
            InvocationType flushT = getWriteFlusher().getCallbackInvocationType();
            if (fillT==flushT)
                return fillT;
            
            if (fillT==InvocationType.EITHER && flushT==InvocationType.NON_BLOCKING)
                return InvocationType.EITHER;
            
            if (fillT==InvocationType.NON_BLOCKING && flushT==InvocationType.EITHER)
                return InvocationType.EITHER;
            
            return InvocationType.BLOCKING;
        }
        
        @Override
        public void run()
        {
            getWriteFlusher().completeWrite();
            getFillInterest().fillable();
        }
    };

    public ChannelEndPoint(ByteChannel channel, ManagedSelector selector, SelectionKey key, Scheduler scheduler)
    {
        super(scheduler);
        _channel=channel;
        _selector=selector;
        _key=key;
        _gather=(channel instanceof GatheringByteChannel)?(GatheringByteChannel)channel:null;
    }

    @Override
    public boolean isOptimizedForDirectBuffers()
    {
        return true;
    }

    @Override
    public boolean isOpen()
    {
        return _channel.isOpen();
    }

    @Override
    public void doClose()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("doClose {}", this);
        try
        {
            _channel.close();
        }
        catch (IOException e)
        {
            LOG.debug(e);
        }
        finally
        {
            super.doClose();
        }
    }

    @Override
    public void onClose()
    {
        try
        {
            super.onClose();
        }
        finally
        {
            if (_selector!=null)
                _selector.destroyEndPoint(this);
        }
    }

    @Override
    public int fill(ByteBuffer buffer) throws IOException
    {
        if (isInputShutdown())
            return -1;

        int pos=BufferUtil.flipToFill(buffer);
        try
        {
            int filled = _channel.read(buffer);
            if (LOG.isDebugEnabled()) // Avoid boxing of variable 'filled'
                LOG.debug("filled {} {}", filled, this);

            if (filled>0)
                notIdle();
            else if (filled==-1)
                shutdownInput();

            return filled;
        }
        catch(IOException e)
        {
            LOG.debug(e);
            shutdownInput();
            return -1;
        }
        finally
        {
            BufferUtil.flipToFlush(buffer,pos);
        }
    }

    @Override
    public boolean flush(ByteBuffer... buffers) throws IOException
    {
        long flushed=0;
        try
        {
            if (buffers.length==1)
                flushed=_channel.write(buffers[0]);
            else if (_gather!=null && buffers.length>1)
                flushed=_gather.write(buffers,0,buffers.length);
            else
            {
                for (ByteBuffer b : buffers)
                {
                    if (b.hasRemaining())
                    {
                        int l=_channel.write(b);
                        if (l>0)
                            flushed+=l;
                        if (b.hasRemaining())
                            break;
                    }
                }
            }
            if (LOG.isDebugEnabled())
                LOG.debug("flushed {} {}", flushed, this);
        }
        catch (IOException e)
        {
            throw new EofException(e);
        }

        if (flushed>0)
            notIdle();

        for (ByteBuffer b : buffers)
            if (!BufferUtil.isEmpty(b))
                return false;

        return true;
    }

    public ByteChannel getChannel()
    {
        return _channel;
    }

    @Override
    public Object getTransport()
    {
        return _channel;
    }


    @Override
    protected void needsFillInterest()
    {
        changeInterests(SelectionKey.OP_READ);
    }

    @Override
    protected void onIncompleteFlush()
    {
        changeInterests(SelectionKey.OP_WRITE);
    }

    @Override
    public Runnable onSelected()
    {
        /**
         * This method may run concurrently with {@link #changeInterests(int)}.
         */

        int readyOps = _key.readyOps();
        int oldInterestOps;
        int newInterestOps;
        try (Locker.Lock lock = _locker.lock())
        {
            _updatePending = true;
            // Remove the readyOps, that here can only be OP_READ or OP_WRITE (or both).
            oldInterestOps = _desiredInterestOps;
            newInterestOps = oldInterestOps & ~readyOps;
            _desiredInterestOps = newInterestOps;
        }

        boolean fillable = (readyOps & SelectionKey.OP_READ) != 0;
        boolean flushable = (readyOps & SelectionKey.OP_WRITE) != 0;

        if (LOG.isDebugEnabled())
            LOG.debug("onSelected {}->{} r={} w={} for {}", oldInterestOps, newInterestOps, fillable, flushable, this);

        // return task to complete the job
        Runnable task= fillable 
                ? (flushable 
                        ? _runCompleteWriteFillable 
                        : _runFillable)
                : (flushable 
                        ? _runCompleteWrite 
                        : null);

        if (LOG.isDebugEnabled())
            LOG.debug("task {}",task);
        return task;
    }

    @Override
    public void updateKey()
    {
        /**
         * This method may run concurrently with {@link #changeInterests(int)}.
         */

        try
        {
            int oldInterestOps;
            int newInterestOps;
            try (Locker.Lock lock = _locker.lock())
            {
                _updatePending = false;
                oldInterestOps = _currentInterestOps;
                newInterestOps = _desiredInterestOps;
                if (oldInterestOps != newInterestOps)
                {
                    _currentInterestOps = newInterestOps;
                    _key.interestOps(newInterestOps);
                }
            }

            if (LOG.isDebugEnabled())
                LOG.debug("Key interests updated {} -> {} on {}", oldInterestOps, newInterestOps, this);
        }
        catch (CancelledKeyException x)
        {
            LOG.debug("Ignoring key update for concurrently closed channel {}", this);
            close();
        }
        catch (Throwable x)
        {
            LOG.warn("Ignoring key update for " + this, x);
            close();
        }
    }

    private void changeInterests(int operation)
    {
        /**
         * This method may run concurrently with
         * {@link #updateKey()} and {@link #onSelected()}.
         */

        int oldInterestOps;
        int newInterestOps;
        boolean pending;
        try (Locker.Lock lock = _locker.lock())
        {
            pending = _updatePending;
            oldInterestOps = _desiredInterestOps;
            newInterestOps = oldInterestOps | operation;
            if (newInterestOps != oldInterestOps)
                _desiredInterestOps = newInterestOps;
        }

        if (LOG.isDebugEnabled())
            LOG.debug("changeInterests p={} {}->{} for {}", pending, oldInterestOps, newInterestOps, this);

        if (!pending && _selector!=null)
            _selector.submit(_runUpdateKey);
    }


    @Override
    public String toEndPointString()
    {
        // We do a best effort to print the right toString() and that's it.
        try
        {
            boolean valid = _key != null && _key.isValid();
            int keyInterests = valid ? _key.interestOps() : -1;
            int keyReadiness = valid ? _key.readyOps() : -1;
            return String.format("%s{io=%d/%d,kio=%d,kro=%d}",
                    super.toEndPointString(),
                    _currentInterestOps,
                    _desiredInterestOps,
                    keyInterests,
                    keyReadiness);
        }
        catch (Throwable x)
        {
            return String.format("%s{io=%s,kio=-2,kro=-2}", super.toString(), _desiredInterestOps);
        }
    }
}
