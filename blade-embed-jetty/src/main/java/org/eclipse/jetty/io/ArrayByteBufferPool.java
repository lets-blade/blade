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

public class ArrayByteBufferPool implements ByteBufferPool
{
    private final int _min;
    private final int _maxQueue;
    private final Bucket[] _direct;
    private final Bucket[] _indirect;
    private final int _inc;

    public ArrayByteBufferPool()
    {
        this(-1,-1,-1,-1);
    }

    public ArrayByteBufferPool(int minSize, int increment, int maxSize)
    {
        this(minSize,increment,maxSize,-1);
    }
    
    public ArrayByteBufferPool(int minSize, int increment, int maxSize, int maxQueue)
    {
        if (minSize<=0)
            minSize=0;
        if (increment<=0)
            increment=1024;
        if (maxSize<=0)
            maxSize=64*1024;
        if (minSize>=increment)
            throw new IllegalArgumentException("minSize >= increment");
        if ((maxSize%increment)!=0 || increment>=maxSize)
            throw new IllegalArgumentException("increment must be a divisor of maxSize");
        _min=minSize;
        _inc=increment;

        _direct=new Bucket[maxSize/increment];
        _indirect=new Bucket[maxSize/increment];
        _maxQueue=maxQueue;

        int size=0;
        for (int i=0;i<_direct.length;i++)
        {
            size+=_inc;
            _direct[i]=new Bucket(this,size,_maxQueue);
            _indirect[i]=new Bucket(this,size,_maxQueue);
        }
    }

    @Override
    public ByteBuffer acquire(int size, boolean direct)
    {
        Bucket bucket = bucketFor(size,direct);
        if (bucket==null)
            return newByteBuffer(size,direct);
            
        return bucket.acquire(direct);
            
    }

    @Override
    public void release(ByteBuffer buffer)
    {
        if (buffer!=null)
        {    
            Bucket bucket = bucketFor(buffer.capacity(),buffer.isDirect());
            if (bucket!=null)
                bucket.release(buffer);
        }
    }

    public void clear()
    {
        for (int i=0;i<_direct.length;i++)
        {
            _direct[i].clear();
            _indirect[i].clear();
        }
    }

    private Bucket bucketFor(int size,boolean direct)
    {
        if (size<=_min)
            return null;
        int b=(size-1)/_inc;
        if (b>=_direct.length)
            return null;
        Bucket bucket = direct?_direct[b]:_indirect[b];
                
        return bucket;
    }

    // Package local for testing
    Bucket[] bucketsFor(boolean direct)
    {
        return direct ? _direct : _indirect;
    }
}
