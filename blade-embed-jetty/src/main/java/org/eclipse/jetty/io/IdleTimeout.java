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

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Scheduler;

/**
 * An Abstract implementation of an Idle Timeout.
 * <p>
 * This implementation is optimised that timeout operations are not cancelled on
 * every operation. Rather timeout are allowed to expire and a check is then made
 * to see when the last operation took place.  If the idle timeout has not expired,
 * the timeout is rescheduled for the earliest possible time a timeout could occur.
 */
public abstract class IdleTimeout
{
    private static final Logger LOG = Log.getLogger(IdleTimeout.class);
    private final Scheduler _scheduler;
    private final AtomicReference<Scheduler.Task> _timeout = new AtomicReference<>();
    private volatile long _idleTimeout;
    private volatile long _idleTimestamp = System.currentTimeMillis();

    private final Runnable _idleTask = new Runnable()
    {
        @Override
        public void run()
        {
            long idleLeft = checkIdleTimeout();
            if (idleLeft >= 0)
                scheduleIdleTimeout(idleLeft > 0 ? idleLeft : getIdleTimeout());
        }
    };

    /**
     * @param scheduler A scheduler used to schedule checks for the idle timeout.
     */
    public IdleTimeout(Scheduler scheduler)
    {
        _scheduler = scheduler;
    }

    public Scheduler getScheduler()
    {
        return _scheduler;
    }
    
    public long getIdleTimestamp()
    {
        return _idleTimestamp;
    }

    public long getIdleFor()
    {
        return System.currentTimeMillis() - getIdleTimestamp();
    }

    public long getIdleTimeout()
    {
        return _idleTimeout;
    }

    public void setIdleTimeout(long idleTimeout)
    {
        long old = _idleTimeout;
        _idleTimeout = idleTimeout;

        // Do we have an old timeout
        if (old > 0)
        {
            // if the old was less than or equal to the new timeout, then nothing more to do
            if (old <= idleTimeout)
                return;

            // old timeout is too long, so cancel it.
            deactivate();
        }

        // If we have a new timeout, then check and reschedule
        if (isOpen())
            activate();
    }

    /**
     * This method should be called when non-idle activity has taken place.
     */
    public void notIdle()
    {
        _idleTimestamp = System.currentTimeMillis();
    }

    private void scheduleIdleTimeout(long delay)
    {
        Scheduler.Task newTimeout = null;
        if (isOpen() && delay > 0 && _scheduler != null)
            newTimeout = _scheduler.schedule(_idleTask, delay, TimeUnit.MILLISECONDS);
        Scheduler.Task oldTimeout = _timeout.getAndSet(newTimeout);
        if (oldTimeout != null)
            oldTimeout.cancel();
    }

    public void onOpen()
    {
        activate();
    }

    private void activate()
    {
        if (_idleTimeout > 0)
            _idleTask.run();
    }

    public void onClose()
    {
        deactivate();
    }

    private void deactivate()
    {
        Scheduler.Task oldTimeout = _timeout.getAndSet(null);
        if (oldTimeout != null)
            oldTimeout.cancel();
    }

    protected long checkIdleTimeout()
    {
        if (isOpen())
        {
            long idleTimestamp = getIdleTimestamp();
            long idleTimeout = getIdleTimeout();
            long idleElapsed = System.currentTimeMillis() - idleTimestamp;
            long idleLeft = idleTimeout - idleElapsed;

            if (LOG.isDebugEnabled())
                LOG.debug("{} idle timeout check, elapsed: {} ms, remaining: {} ms", this, idleElapsed, idleLeft);

            if (idleTimestamp != 0 && idleTimeout > 0)
            {
                if (idleLeft <= 0)
                {
                    if (LOG.isDebugEnabled())
                        LOG.debug("{} idle timeout expired", this);
                    try
                    {
                        onIdleExpired(new TimeoutException("Idle timeout expired: " + idleElapsed + "/" + idleTimeout + " ms"));
                    }
                    finally
                    {
                        notIdle();
                    }
                }
            }

            return idleLeft >= 0 ? idleLeft : 0;
        }
        return -1;
    }

    /**
     * This abstract method is called when the idle timeout has expired.
     *
     * @param timeout a TimeoutException
     */
    protected abstract void onIdleExpired(TimeoutException timeout);

    /**
     * This abstract method should be called to check if idle timeouts
     * should still be checked.
     *
     * @return True if the entity monitored should still be checked for idle timeouts
     */
    public abstract boolean isOpen();
}
