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

import java.io.EOFException;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;

import org.eclipse.jetty.util.BufferUtil;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.thread.Locker;
import org.eclipse.jetty.util.thread.Scheduler;

/* ------------------------------------------------------------ */
/** ByteArrayEndPoint.
 *
 */
public class ByteArrayEndPoint extends AbstractEndPoint
{
    static final Logger LOG = Log.getLogger(ByteArrayEndPoint.class);
    static final InetAddress  NOIP;
    static final InetSocketAddress NOIPPORT;
    
    static
    {
        InetAddress noip=null;
        try
        {
            noip = Inet4Address.getByName("0.0.0.0");
        }
        catch (UnknownHostException e)
        {
            LOG.warn(e);
        }
        finally
        {
            NOIP=noip;
            NOIPPORT=new InetSocketAddress(NOIP,0);
        }
    }
    
    
    private static final ByteBuffer EOF = BufferUtil.allocate(0);

    private final Runnable _runFillable = new Runnable()
    {
        @Override
        public void run()
        {
            getFillInterest().fillable();
        }
    };

    private final Locker _locker = new Locker();
    private final Condition _hasOutput = _locker.newCondition();
    private final Queue<ByteBuffer> _inQ = new ArrayDeque<>();
    private ByteBuffer _out;
    private boolean _growOutput;

    /* ------------------------------------------------------------ */
    /**
     *
     */
    public ByteArrayEndPoint()
    {
        this(null,0,null,null);
    }

    /* ------------------------------------------------------------ */
    /**
     * @param input the input bytes
     * @param outputSize the output size
     */
    public ByteArrayEndPoint(byte[] input, int outputSize)
    {
        this(null,0,input!=null?BufferUtil.toBuffer(input):null,BufferUtil.allocate(outputSize));
    }

    /* ------------------------------------------------------------ */
    /**
     * @param input the input string (converted to bytes using default encoding charset)
     * @param outputSize the output size
     */
    public ByteArrayEndPoint(String input, int outputSize)
    {
        this(null,0,input!=null?BufferUtil.toBuffer(input):null,BufferUtil.allocate(outputSize));
    }

    /* ------------------------------------------------------------ */
    public ByteArrayEndPoint(Scheduler scheduler, long idleTimeoutMs)
    {
        this(scheduler,idleTimeoutMs,null,null);
    }

    /* ------------------------------------------------------------ */
    public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, byte[] input, int outputSize)
    {
        this(timer,idleTimeoutMs,input!=null?BufferUtil.toBuffer(input):null,BufferUtil.allocate(outputSize));
    }

    /* ------------------------------------------------------------ */
    public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, String input, int outputSize)
    {
        this(timer,idleTimeoutMs,input!=null?BufferUtil.toBuffer(input):null,BufferUtil.allocate(outputSize));
    }

    /* ------------------------------------------------------------ */
    public ByteArrayEndPoint(Scheduler timer, long idleTimeoutMs, ByteBuffer input, ByteBuffer output)
    {
        super(timer);
        if (BufferUtil.hasContent(input))
            addInput(input);
        _out=output==null?BufferUtil.allocate(1024):output;
        setIdleTimeout(idleTimeoutMs);
        onOpen();
    }
    
    /* ------------------------------------------------------------ */
    @Override
    public void doShutdownOutput()
    {
        super.doShutdownOutput(); 
        try(Locker.Lock lock = _locker.lock())
        {
            _hasOutput.signalAll();
        }  
    }

    /* ------------------------------------------------------------ */
    @Override
    public void doClose()
    {
        super.doClose();
        try(Locker.Lock lock = _locker.lock())
        {
            _hasOutput.signalAll();
        }
    }

    /* ------------------------------------------------------------ */
    @Override
    public InetSocketAddress getLocalAddress()
    {
        return NOIPPORT;
    }

    /* ------------------------------------------------------------ */
    @Override
    public InetSocketAddress getRemoteAddress()
    {
        return NOIPPORT;
    }

    /* ------------------------------------------------------------ */
    @Override
    protected void onIncompleteFlush()
    {
        // Don't need to do anything here as takeOutput does the signalling.
    }

    /* ------------------------------------------------------------ */
    protected void execute(Runnable task)
    {
        new Thread(task,"BAEPoint-"+Integer.toHexString(hashCode())).start();
    }

    /* ------------------------------------------------------------ */
    @Override
    protected void needsFillInterest() throws IOException
    {
        try(Locker.Lock lock = _locker.lock())
        {
            if (!isOpen())
                throw new ClosedChannelException();

            ByteBuffer in = _inQ.peek();
            if (BufferUtil.hasContent(in) || in==EOF)
                execute(_runFillable);
        }
    }

    /* ------------------------------------------------------------ */
    /**
     */
    public void addInputEOF()
    {
        addInput((ByteBuffer)null);
    }

    /* ------------------------------------------------------------ */
    /**
     * @param in The in to set.
     */
    public void addInput(ByteBuffer in)
    {
        boolean fillable=false;
        try(Locker.Lock lock = _locker.lock())
        {
            if (_inQ.peek()==EOF)
                throw new RuntimeIOException(new EOFException());
            boolean was_empty=_inQ.isEmpty();
            if (in==null)
            {
                _inQ.add(EOF);
                fillable=true;
            }
            if (BufferUtil.hasContent(in))
            {
                _inQ.add(in);
                fillable=was_empty;
            }
        }
        if (fillable)
            _runFillable.run();
    }

    /* ------------------------------------------------------------ */
    public void addInputAndExecute(ByteBuffer in)
    {
        boolean fillable=false;
        try(Locker.Lock lock = _locker.lock())
        {
            if (_inQ.peek()==EOF)
                throw new RuntimeIOException(new EOFException());
            boolean was_empty=_inQ.isEmpty();
            if (in==null)
            {
                _inQ.add(EOF);
                fillable=true;
            }
            if (BufferUtil.hasContent(in))
            {
                _inQ.add(in);
                fillable=was_empty;
            }
        }
        if (fillable)
            execute(_runFillable);
    }

    /* ------------------------------------------------------------ */
    public void addInput(String s)
    {
        addInput(BufferUtil.toBuffer(s,StandardCharsets.UTF_8));
    }

    /* ------------------------------------------------------------ */
    public void addInput(String s,Charset charset)
    {
        addInput(BufferUtil.toBuffer(s,charset));
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the out.
     */
    public ByteBuffer getOutput()
    {
        try(Locker.Lock lock = _locker.lock())
        {
            return _out;
        }
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the out.
     */
    public String getOutputString()
    {
        return getOutputString(StandardCharsets.UTF_8);
    }

    /* ------------------------------------------------------------ */
    /**
     * @param charset the charset to encode the output as
     * @return Returns the out.
     */
    public String getOutputString(Charset charset)
    {
        return BufferUtil.toString(_out,charset);
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the out.
     */
    public ByteBuffer takeOutput()
    {
        ByteBuffer b;

        try(Locker.Lock lock = _locker.lock())
        {
            b=_out;
            _out=BufferUtil.allocate(b.capacity());
        }
        getWriteFlusher().completeWrite();
        return b;
    }

    /* ------------------------------------------------------------ */
    /** Wait for some output
     * @param time Time to wait
     * @param unit Units for time to wait
     * @return The buffer of output
     * @throws InterruptedException if interrupted
     */
    public ByteBuffer waitForOutput(long time,TimeUnit unit) throws InterruptedException
    {
        ByteBuffer b;

        try(Locker.Lock lock = _locker.lock())
        {
            while (BufferUtil.isEmpty(_out) && !isOutputShutdown())
            {
                _hasOutput.await(time,unit);
            }
            b=_out;
            _out=BufferUtil.allocate(b.capacity());
        }
        getWriteFlusher().completeWrite();
        return b;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the out.
     */
    public String takeOutputString()
    {
        return takeOutputString(StandardCharsets.UTF_8);
    }

    /* ------------------------------------------------------------ */
    /**
     * @param charset the charset to encode the output as
     * @return Returns the out.
     */
    public String takeOutputString(Charset charset)
    {
        ByteBuffer buffer=takeOutput();
        return BufferUtil.toString(buffer,charset);
    }

    /* ------------------------------------------------------------ */
    /**
     * @param out The out to set.
     */
    public void setOutput(ByteBuffer out)
    {
        try(Locker.Lock lock = _locker.lock())
        {
            _out = out;
        }
        getWriteFlusher().completeWrite();
    }

    /* ------------------------------------------------------------ */
    /**
     * @return <code>true</code> if there are bytes remaining to be read from the encoded input
     */
    public boolean hasMore()
    {
        return getOutput().position()>0;
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.io.EndPoint#fill(org.eclipse.io.Buffer)
     */
    @Override
    public int fill(ByteBuffer buffer) throws IOException
    {
        int filled=0;
        try(Locker.Lock lock = _locker.lock())
        {
            while(true)
            {
                if (!isOpen())
                    throw new EofException("CLOSED");

                if (isInputShutdown())
                    return -1;

                if (_inQ.isEmpty())
                    break;

                ByteBuffer in= _inQ.peek();
                if (in==EOF)
                {
                    filled=-1;
                    break;
                }

                if (BufferUtil.hasContent(in))
                {
                    filled=BufferUtil.append(buffer,in);
                    if (BufferUtil.isEmpty(in))
                        _inQ.poll();
                    break;
                }
                _inQ.poll();
            }
        }

        if (filled>0)
            notIdle();
        else if (filled<0)
            shutdownInput();
        return filled;
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.io.EndPoint#flush(org.eclipse.io.Buffer, org.eclipse.io.Buffer, org.eclipse.io.Buffer)
     */
    @Override
    public boolean flush(ByteBuffer... buffers) throws IOException
    {
        boolean flushed=true;
        try(Locker.Lock lock = _locker.lock())
        {
            if (!isOpen())
                throw new IOException("CLOSED");
            if (isOutputShutdown())
                throw new IOException("OSHUT");
            
            boolean idle=true;

            for (ByteBuffer b : buffers)
            {
                if (BufferUtil.hasContent(b))
                {
                    if (_growOutput && b.remaining()>BufferUtil.space(_out))
                    {
                        BufferUtil.compact(_out);
                        if (b.remaining()>BufferUtil.space(_out))
                        {
                            ByteBuffer n = BufferUtil.allocate(_out.capacity()+b.remaining()*2);
                            BufferUtil.append(n,_out);
                            _out=n;
                        }
                    }

                    if (BufferUtil.append(_out,b)>0)
                        idle=false;

                    if (BufferUtil.hasContent(b))
                    {
                        flushed=false;
                        break;
                    }
                }
            }
            if (!idle)
            {
                notIdle();
                _hasOutput.signalAll();
            }
        }
        return flushed;
    }

    /* ------------------------------------------------------------ */
    /**
     *
     */
    public void reset()
    {
        try(Locker.Lock lock = _locker.lock())
        {
            _inQ.clear();
            _hasOutput.signalAll();
            BufferUtil.clear(_out);
        }
        super.reset();
    }

    /* ------------------------------------------------------------ */
    /*
     * @see org.eclipse.io.EndPoint#getConnection()
     */
    @Override
    public Object getTransport()
    {
        return null;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return the growOutput
     */
    public boolean isGrowOutput()
    {
        return _growOutput;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param growOutput the growOutput to set
     */
    public void setGrowOutput(boolean growOutput)
    {
        _growOutput=growOutput;
    }

    /* ------------------------------------------------------------ */
    @Override
    public String toString()
    {
        int q;
        ByteBuffer b;
        String o;
        try(Locker.Lock lock = _locker.lock())
        {
            q=_inQ.size();
            b=_inQ.peek();
            o=BufferUtil.toDetailString(_out);
        }
        return String.format("%s[q=%d,q[0]=%s,o=%s]",super.toString(),q,b,o);
    }

}
