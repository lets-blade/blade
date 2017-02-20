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
import java.nio.ByteBuffer;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Scheduler;

public abstract class AbstractEndPoint extends IdleTimeout implements EndPoint
{
    private static final Logger LOG = Log.getLogger(AbstractEndPoint.class);

    private final AtomicReference<State> _state = new AtomicReference<>(State.OPEN);
    private final long _created=System.currentTimeMillis();
    private volatile Connection _connection;

    private final FillInterest _fillInterest = new FillInterest()
    {
        @Override
        protected void needsFillInterest() throws IOException
        {
            AbstractEndPoint.this.needsFillInterest();
        }
    };

    private final WriteFlusher _writeFlusher = new WriteFlusher(this)
    {
        @Override
        protected void onIncompleteFlush()
        {
            AbstractEndPoint.this.onIncompleteFlush();
        }
    };

    protected AbstractEndPoint(Scheduler scheduler)
    {
        super(scheduler);
    }

    protected final void shutdownInput()
    {
        while(true)
        {
            State s = _state.get();
            switch(s)
            {
                case OPEN:
                    if (!_state.compareAndSet(s,State.ISHUTTING))
                        continue;
                    try
                    {
                        doShutdownInput();
                    }
                    finally
                    {
                        if(!_state.compareAndSet(State.ISHUTTING,State.ISHUT))
                        {
                            // If somebody else switched to CLOSED while we were ishutting,
                            // then we do the close for them
                            if (_state.get()==State.CLOSED)
                                doOnClose(null);
                            else
                                throw new IllegalStateException();
                        }
                    }
                    return;

                case ISHUTTING:  // Somebody else ishutting
                case ISHUT: // Already ishut
                    return;

                case OSHUTTING:
                    if (!_state.compareAndSet(s,State.CLOSED))
                        continue;
                    // The thread doing the OSHUT will close
                    return;

                case OSHUT:
                    if (!_state.compareAndSet(s,State.CLOSED))
                        continue;
                    // Already OSHUT so we close
                    doOnClose(null);
                    return;

                case CLOSED: // already closed
                    return;
            }
        }
    }

    @Override
    public final void shutdownOutput()
    {
        while(true)
        {
            State s = _state.get();
            switch(s)
            {
                case OPEN:
                    if (!_state.compareAndSet(s,State.OSHUTTING))
                        continue;
                    try
                    {
                        doShutdownOutput();
                    }
                    finally
                    {
                        if(!_state.compareAndSet(State.OSHUTTING,State.OSHUT))
                        {
                            // If somebody else switched to CLOSED while we were oshutting,
                            // then we do the close for them
                            if (_state.get()==State.CLOSED)
                                doOnClose(null);
                            else
                                throw new IllegalStateException();
                        }
                    }
                    return;

                case ISHUTTING:
                    if (!_state.compareAndSet(s,State.CLOSED))
                        continue;
                    // The thread doing the ISHUT will close
                    return;

                case ISHUT:
                    if (!_state.compareAndSet(s,State.CLOSED))
                        continue;
                    // Already ISHUT so we close
                    doOnClose(null);
                    return;

                case OSHUTTING:  // Somebody else oshutting
                case OSHUT: // Already oshut
                    return;

                case CLOSED: // already closed
                    return;
            }
        }
    }

    @Override
    public final void close()
    {
        close(null);
    }

    protected final void close(Throwable failure)
    {
        while(true)
        {
            State s = _state.get();
            switch(s)
            {
                case OPEN:
                case ISHUT: // Already ishut
                case OSHUT: // Already oshut
                    if (!_state.compareAndSet(s,State.CLOSED))
                        continue;
                    doOnClose(failure);
                    return;

                case ISHUTTING: // Somebody else ishutting
                case OSHUTTING: // Somebody else oshutting
                    if (!_state.compareAndSet(s,State.CLOSED))
                        continue;
                    // The thread doing the IO SHUT will call doOnClose
                    return;

                case CLOSED: // already closed
                    return;
            }
        }
    }

    protected void doShutdownInput()
    {
    }

    protected void doShutdownOutput()
    {
    }

    private void doOnClose(Throwable failure)
    {
        try
        {
            doClose();
        }
        finally
        {
            if (failure == null)
                onClose();
            else
                onClose(failure);
        }
    }

    protected void doClose()
    {
    }

    protected void onClose(Throwable failure)
    {
        super.onClose();
        _writeFlusher.onFail(failure);
        _fillInterest.onFail(failure);
    }

    @Override
    public boolean isOutputShutdown()
    {
        switch(_state.get())
        {
            case CLOSED:
            case OSHUT:
            case OSHUTTING:
                return true;
            default:
                return false;
        }
    }
    @Override
    public boolean isInputShutdown()
    {
        switch(_state.get())
        {
            case CLOSED:
            case ISHUT:
            case ISHUTTING:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean isOpen()
    {
        switch(_state.get())
        {
            case CLOSED:
                return false;
            default:
                return true;
        }
    }

    public void checkFlush() throws IOException
    {
        State s=_state.get();
        switch(s)
        {
            case OSHUT:
            case OSHUTTING:
            case CLOSED:
                throw new IOException(s.toString());
            default:
                break;
        }
    }

    public void checkFill() throws IOException
    {
        State s=_state.get();
        switch(s)
        {
            case ISHUT:
            case ISHUTTING:
            case CLOSED:
                throw new IOException(s.toString());
            default:
                break;
        }
    }

    @Override
    public long getCreatedTimeStamp()
    {
        return _created;
    }

    @Override
    public Connection getConnection()
    {
        return _connection;
    }

    @Override
    public void setConnection(Connection connection)
    {
        _connection = connection;
    }

    @Override
    public boolean isOptimizedForDirectBuffers()
    {
        return false;
    }

    protected void reset()
    {
        _state.set(State.OPEN);
        _writeFlusher.onClose();
        _fillInterest.onClose();
    }

    @Override
    public void onOpen()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("onOpen {}",this);
        if (_state.get()!=State.OPEN)
            throw new IllegalStateException();
    }

    @Override
    public void onClose()
    {
        super.onClose();
        _writeFlusher.onClose();
        _fillInterest.onClose();
    }

    @Override
    public void fillInterested(Callback callback)
    {
        notIdle();
        _fillInterest.register(callback);
    }

    @Override
    public boolean tryFillInterested(Callback callback)
    {
        notIdle();
        return _fillInterest.tryRegister(callback);
    }

    @Override
    public boolean isFillInterested()
    {
        return _fillInterest.isInterested();
    }

    @Override
    public void write(Callback callback, ByteBuffer... buffers) throws IllegalStateException
    {
        _writeFlusher.write(callback, buffers);
    }

    protected abstract void onIncompleteFlush();

    protected abstract void needsFillInterest() throws IOException;

    public FillInterest getFillInterest()
    {
        return _fillInterest;
    }

    protected WriteFlusher getWriteFlusher()
    {
        return _writeFlusher;
    }

    @Override
    protected void onIdleExpired(TimeoutException timeout)
    {
        Connection connection = _connection;
        if (connection != null && !connection.onIdleExpired())
            return;

        boolean output_shutdown=isOutputShutdown();
        boolean input_shutdown=isInputShutdown();
        boolean fillFailed = _fillInterest.onFail(timeout);
        boolean writeFailed = _writeFlusher.onFail(timeout);

        // If the endpoint is half closed and there was no fill/write handling, then close here.
        // This handles the situation where the connection has completed its close handling
        // and the endpoint is half closed, but the other party does not complete the close.
        // This perhaps should not check for half closed, however the servlet spec case allows
        // for a dispatched servlet or suspended request to extend beyond the connections idle
        // time.  So if this test would always close an idle endpoint that is not handled, then
        // we would need a mode to ignore timeouts for some HTTP states
        if (isOpen() && (output_shutdown || input_shutdown) && !(fillFailed || writeFailed))
            close();
        else
            LOG.debug("Ignored idle endpoint {}",this);
    }

    @Override
    public void upgrade(Connection newConnection)
    {
        Connection old_connection = getConnection();

        if (LOG.isDebugEnabled())
            LOG.debug("{} upgrading from {} to {}", this, old_connection, newConnection);

        ByteBuffer prefilled = (old_connection instanceof Connection.UpgradeFrom)
                ?((Connection.UpgradeFrom)old_connection).onUpgradeFrom():null;
        old_connection.onClose();
        old_connection.getEndPoint().setConnection(newConnection);

        if (newConnection instanceof Connection.UpgradeTo)
            ((Connection.UpgradeTo)newConnection).onUpgradeTo(prefilled);
        else if (BufferUtil.hasContent(prefilled))
            throw new IllegalStateException();

        newConnection.onOpen();
    }

    @Override
    public String toString()
    {
        return String.format("%s->%s",toEndPointString(),toConnectionString());
    }
    
    public String toEndPointString()
    {
        Class<?> c=getClass();
        String name=c.getSimpleName();
        while (name.length()==0 && c.getSuperclass()!=null)
        {
            c=c.getSuperclass();
            name=c.getSimpleName();
        }

        return String.format("%s@%h{%s<->%s,%s,fill=%s,flush=%s,to=%d/%d}",
                name,
                this,
                getRemoteAddress(),
                getLocalAddress(),
                _state.get(),
                _fillInterest.toStateString(),
                _writeFlusher.toStateString(),
                getIdleFor(),
                getIdleTimeout());
    }
    
    public String toConnectionString()
    {
        Connection connection = getConnection();
        if (connection == null) // can happen during upgrade
            return "<null>";
        if (connection instanceof AbstractConnection)
            return ((AbstractConnection)connection).toConnectionString();
        return String.format("%s@%x",connection.getClass().getSimpleName(),connection.hashCode());
    }
       

    private enum State
    {
        OPEN, ISHUTTING, ISHUT, OSHUTTING, OSHUT, CLOSED
    }
}
