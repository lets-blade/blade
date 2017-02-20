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

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeoutException;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Invocable;

/**
 * <p>A convenience base implementation of {@link Connection}.</p>
 * <p>This class uses the capabilities of the {@link EndPoint} API to provide a
 * more traditional style of async reading.  A call to {@link #fillInterested()}
 * will schedule a callback to {@link #onFillable()} or {@link #onFillInterestedFailed(Throwable)}
 * as appropriate.</p>
 */
public abstract class AbstractConnection implements Connection
{
    private static final Logger LOG = Log.getLogger(AbstractConnection.class);

    private final List<Listener> listeners = new CopyOnWriteArrayList<>();
    private final long _created=System.currentTimeMillis();
    private final EndPoint _endPoint;
    private final Executor _executor;
    private final Callback _readCallback;
    private int _inputBufferSize=2048;

    protected AbstractConnection(EndPoint endp, Executor executor)
    {
        if (executor == null)
            throw new IllegalArgumentException("Executor must not be null!");
        _endPoint = endp;
        _executor = executor;
        _readCallback = new ReadCallback();
    }

    @Override
    public void addListener(Listener listener)
    {
        listeners.add(listener);
    }

    @Override
    public void removeListener(Listener listener)
    {
        listeners.remove(listener);
    }

    public int getInputBufferSize()
    {
        return _inputBufferSize;
    }

    public void setInputBufferSize(int inputBufferSize)
    {
        _inputBufferSize = inputBufferSize;
    }

    protected Executor getExecutor()
    {
        return _executor;
    }

    protected void failedCallback(final Callback callback, final Throwable x)
    {
        Runnable failCallback = () ->
        {
            try
            {
                callback.failed(x);
            }
            catch (Exception e)
            {
                LOG.warn(e);
            }
        };
        
        switch(Invocable.getInvocationType(callback))
        {
            case BLOCKING:
                try
                {
                    getExecutor().execute(failCallback); 
                }
                catch(RejectedExecutionException e)
                {
                    LOG.debug(e);
                    callback.failed(x);
                }
                break;
                
            case NON_BLOCKING:
                failCallback.run();
                break;
                
            case EITHER:
                Invocable.invokeNonBlocking(failCallback);

        }
    }

    /**
     * <p>Utility method to be called to register read interest.</p>
     * <p>After a call to this method, {@link #onFillable()} or {@link #onFillInterestedFailed(Throwable)}
     * will be called back as appropriate.</p>
     * @see #onFillable()
     */
    public void fillInterested()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("fillInterested {}",this);
        getEndPoint().fillInterested(_readCallback);
    }

    public void tryFillInterested()
    {
        tryFillInterested(_readCallback);
    }

    public void tryFillInterested(Callback callback)
    {
        getEndPoint().tryFillInterested(callback);
    }

    public boolean isFillInterested()
    {
        return getEndPoint().isFillInterested();
    }

    /**
     * <p>Callback method invoked when the endpoint is ready to be read.</p>
     * @see #fillInterested()
     */
    public abstract void onFillable();

    /**
     * <p>Callback method invoked when the endpoint failed to be ready to be read.</p>
     * @param cause the exception that caused the failure
     */
    protected void onFillInterestedFailed(Throwable cause)
    {
        if (LOG.isDebugEnabled())
            LOG.debug("{} onFillInterestedFailed {}", this, cause);
        if (_endPoint.isOpen())
        {
            boolean close = true;
            if (cause instanceof TimeoutException)
                close = onReadTimeout();
            if (close)
            {
                if (_endPoint.isOutputShutdown())
                    _endPoint.close();
                else
                {
                    _endPoint.shutdownOutput();
                    fillInterested();
                }
            }
        }
    }

    /**
     * <p>Callback method invoked when the endpoint failed to be ready to be read after a timeout</p>
     * @return true to signal that the endpoint must be closed, false to keep the endpoint open
     */
    protected boolean onReadTimeout()
    {
        return true;
    }

    @Override
    public void onOpen()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("onOpen {}", this);

        for (Listener listener : listeners)
            listener.onOpened(this);
    }

    @Override
    public void onClose()
    {
        if (LOG.isDebugEnabled())
            LOG.debug("onClose {}",this);

        for (Listener listener : listeners)
            listener.onClosed(this);
    }

    @Override
    public EndPoint getEndPoint()
    {
        return _endPoint;
    }

    @Override
    public void close()
    {
        getEndPoint().close();
    }

    @Override
    public boolean onIdleExpired()
    {
        return true;
    }

    @Override
    public long getMessagesIn()
    {
        return -1;
    }

    @Override
    public long getMessagesOut()
    {
        return -1;
    }

    @Override
    public long getBytesIn()
    {
        return -1;
    }

    @Override
    public long getBytesOut()
    {
        return -1;
    }

    @Override
    public long getCreatedTimeStamp()
    {
        return _created;
    }

    @Override
    public final String toString()
    {
        return String.format("%s<-%s",toConnectionString(),getEndPoint());
    }

    public String toConnectionString()
    {
        return String.format("%s@%h",
            getClass().getSimpleName(),
            this);
    }

    private class ReadCallback implements Callback
    {
        @Override
        public void succeeded()
        {
            onFillable();
        }

        @Override
        public void failed(final Throwable x)
        {
            onFillInterestedFailed(x);
        }

        @Override
        public String toString()
        {
            return String.format("AC.ReadCB@%h{%s}", AbstractConnection.this,AbstractConnection.this);
        }
    }

}
