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

import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jetty.util.BufferUtil;

/**
 * <p>A {@link ByteBuffer} pool.</p>
 * <p>Acquired buffers may be {@link #release(ByteBuffer) released} but they do not need to;
 * if they are released, they may be recycled and reused, otherwise they will be garbage
 * collected as usual.</p>
 */
public interface ByteBufferPool
{
    /**
     * <p>Requests a {@link ByteBuffer} of the given size.</p>
     * <p>The returned buffer may have a bigger capacity than the size being
     * requested but it will have the limit set to the given size.</p>
     *
     * @param size   the size of the buffer
     * @param direct whether the buffer must be direct or not
     * @return the requested buffer
     * @see #release(ByteBuffer)
     */
    public ByteBuffer acquire(int size, boolean direct);

    /**
     * <p>Returns a {@link ByteBuffer}, usually obtained with {@link #acquire(int, boolean)}
     * (but not necessarily), making it available for recycling and reuse.</p>
     *
     * @param buffer the buffer to return
     * @see #acquire(int, boolean)
     */
    public void release(ByteBuffer buffer);

    default ByteBuffer newByteBuffer(int capacity, boolean direct)
    {
        return direct ? BufferUtil.allocateDirect(capacity) : BufferUtil.allocate(capacity);
    }

    public static class Lease
    {
        private final ByteBufferPool byteBufferPool;
        private final List<ByteBuffer> buffers;
        private final List<Boolean> recycles;

        public Lease(ByteBufferPool byteBufferPool)
        {
            this.byteBufferPool = byteBufferPool;
            this.buffers = new ArrayList<>();
            this.recycles = new ArrayList<>();
        }

        public ByteBuffer acquire(int capacity, boolean direct)
        {
            ByteBuffer buffer = byteBufferPool.acquire(capacity, direct);
            BufferUtil.clearToFill(buffer);
            return buffer;
        }

        public void append(ByteBuffer buffer, boolean recycle)
        {
            buffers.add(buffer);
            recycles.add(recycle);
        }

        public void insert(int index, ByteBuffer buffer, boolean recycle)
        {
            buffers.add(index, buffer);
            recycles.add(index, recycle);
        }

        public List<ByteBuffer> getByteBuffers()
        {
            return buffers;
        }

        public long getTotalLength()
        {
            long length = 0;
            for (int i = 0; i < buffers.size(); ++i)
                length += buffers.get(i).remaining();
            return length;
        }

        public int getSize()
        {
            return buffers.size();
        }

        public void recycle()
        {
            for (int i = 0; i < buffers.size(); ++i)
            {
                ByteBuffer buffer = buffers.get(i);
                if (recycles.get(i))
                    byteBufferPool.release(buffer);
            }
            buffers.clear();
            recycles.clear();
        }
    }

    class Bucket
    {
        private final Lock _lock = new ReentrantLock();
        private final Queue<ByteBuffer> _queue = new ArrayDeque<>();
        private final ByteBufferPool _pool;
        private final int _capacity;
        private final AtomicInteger _space;

        public Bucket(ByteBufferPool pool, int bufferSize, int maxSize)
        {
            _pool = pool;
            _capacity = bufferSize;
            _space = maxSize > 0 ? new AtomicInteger(maxSize) : null;
        }

        public ByteBuffer acquire(boolean direct)
        {
            ByteBuffer buffer = queuePoll();
            if (buffer == null)
                return _pool.newByteBuffer(_capacity, direct);
            if (_space != null)
                _space.incrementAndGet();
            return buffer;
        }

        public void release(ByteBuffer buffer)
        {
            BufferUtil.clear(buffer);
            if (_space == null)
                queueOffer(buffer);
            else if (_space.decrementAndGet() >= 0)
                queueOffer(buffer);
            else
                _space.incrementAndGet();
        }

        public void clear()
        {
            if (_space == null)
            {
                queueClear();
            }
            else
            {
                int s = _space.getAndSet(0);
                while (s-- > 0)
                {
                    if (queuePoll() == null)
                        _space.incrementAndGet();
                }
            }
        }

        private void queueOffer(ByteBuffer buffer)
        {
            Lock lock = _lock;
            lock.lock();
            try
            {
                _queue.offer(buffer);
            }
            finally
            {
                lock.unlock();
            }
        }

        private ByteBuffer queuePoll()
        {
            Lock lock = _lock;
            lock.lock();
            try
            {
                return _queue.poll();
            }
            finally
            {
                lock.unlock();
            }
        }

        private void queueClear()
        {
            Lock lock = _lock;
            lock.lock();
            try
            {
                _queue.clear();
            }
            finally
            {
                lock.unlock();
            }
        }

        boolean isEmpty()
        {
            Lock lock = _lock;
            lock.lock();
            try
            {
                return _queue.isEmpty();
            }
            finally
            {
                lock.unlock();
            }
        }

        int size()
        {
            Lock lock = _lock;
            lock.lock();
            try
            {
                return _queue.size();
            }
            finally
            {
                lock.unlock();
            }
        }

        @Override
        public String toString()
        {
            return String.format("Bucket@%x{%d/%d}", hashCode(), size(), _capacity);
        }
    }
}
