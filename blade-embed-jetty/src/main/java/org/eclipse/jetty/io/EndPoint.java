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
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ReadPendingException;
import java.nio.channels.WritePendingException;

import org.eclipse.jetty.util.Callback;
import org.eclipse.jetty.util.FutureCallback;
import org.eclipse.jetty.util.IteratingCallback;

/**
 *
 * A transport EndPoint
 *
 * <h3>Asynchronous Methods</h3>
 * <p>The asynchronous scheduling methods of {@link EndPoint}
 * has been influenced by NIO.2 Futures and Completion
 * handlers, but does not use those actual interfaces because they have
 * some inefficiencies.</p>
 * <p>This class will frequently be used in conjunction with some of the utility
 * implementations of {@link Callback}, such as {@link FutureCallback} and
 * {@link IteratingCallback}. Examples are:</p>
 *
 * <h3>Blocking Read</h3>
 * <p>A FutureCallback can be used to block until an endpoint is ready to be filled
 * from:</p>
 * <blockquote><pre>
 * FutureCallback&lt;String&gt; future = new FutureCallback&lt;&gt;();
 * endpoint.fillInterested("ContextObj",future);
 * ...
 * String context = future.get(); // This blocks
 * int filled=endpoint.fill(mybuffer);
 * </pre></blockquote>
 *
 * <h3>Dispatched Read</h3>
 * <p>By using a different callback, the read can be done asynchronously in its own dispatched thread:</p>
 * <blockquote><pre>
 * endpoint.fillInterested("ContextObj",new ExecutorCallback&lt;String&gt;(executor)
 * {
 *   public void onCompleted(String context)
 *   {
 *     int filled=endpoint.fill(mybuffer);
 *     ...
 *   }
 *   public void onFailed(String context,Throwable cause) {...}
 * });
 * </pre></blockquote>
 * <p>The executor callback can also be customized to not dispatch in some circumstances when
 * it knows it can use the callback thread and does not need to dispatch.</p>
 *
 * <h3>Blocking Write</h3>
 * <p>The write contract is that the callback complete is not called until all data has been
 * written or there is a failure.  For blocking this looks like:</p>
 * <blockquote><pre>
 * FutureCallback&lt;String&gt; future = new FutureCallback&lt;&gt;();
 * endpoint.write("ContextObj",future,headerBuffer,contentBuffer);
 * String context = future.get(); // This blocks
 * </pre></blockquote>
 *
 * <h3>Dispatched Write</h3>
 * <p>Note also that multiple buffers may be passed in write so that gather writes
 * can be done:</p>
 * <blockquote><pre>
 * endpoint.write("ContextObj",new ExecutorCallback&lt;String&gt;(executor)
 * {
 *   public void onCompleted(String context)
 *   {
 *     int filled=endpoint.fill(mybuffer);
 *     ...
 *   }
 *   public void onFailed(String context,Throwable cause) {...}
 * },headerBuffer,contentBuffer);
 * </pre></blockquote>
 */
public interface EndPoint extends Closeable
{    
    /* ------------------------------------------------------------ */
    /**
     * @return The local Inet address to which this <code>EndPoint</code> is bound, or <code>null</code>
     * if this <code>EndPoint</code> does not represent a network connection.
     */
    InetSocketAddress getLocalAddress();

    /* ------------------------------------------------------------ */
    /**
     * @return The remote Inet address to which this <code>EndPoint</code> is bound, or <code>null</code>
     * if this <code>EndPoint</code> does not represent a network connection.
     */
    InetSocketAddress getRemoteAddress();

    /* ------------------------------------------------------------ */
    boolean isOpen();

    /* ------------------------------------------------------------ */
    long getCreatedTimeStamp();

    /* ------------------------------------------------------------ */
    /** Shutdown the output.
     * <p>This call indicates that no more data will be sent on this endpoint that
     * that the remote end should read an EOF once all previously sent data has been
     * consumed. Shutdown may be done either at the TCP/IP level, as a protocol exchange (Eg
     * TLS close handshake) or both.
     * <p>
     * If the endpoint has {@link #isInputShutdown()} true, then this call has the same effect
     * as {@link #close()}.
     */
    void shutdownOutput();

    /* ------------------------------------------------------------ */
    /** Test if output is shutdown.
     * The output is shutdown by a call to {@link #shutdownOutput()}
     * or {@link #close()}.
     * @return true if the output is shutdown or the endpoint is closed.
     */
    boolean isOutputShutdown();

    /* ------------------------------------------------------------ */
    /** Test if the input is shutdown.
     * The input is shutdown if an EOF has been read while doing
     * a {@link #fill(ByteBuffer)}.   Once the input is shutdown, all calls to
     * {@link #fill(ByteBuffer)} will  return -1, until such time as the
     * end point is close, when they will return {@link EofException}.
     * @return True if the input is shutdown or the endpoint is closed.
     */
    boolean isInputShutdown();

    /**
     * Close any backing stream associated with the endpoint
     */
    @Override
    void close();

    /**
     * Fill the passed buffer with data from this endpoint.  The bytes are appended to any
     * data already in the buffer by writing from the buffers limit up to it's capacity.
     * The limit is updated to include the filled bytes.
     *
     * @param buffer The buffer to fill. The position and limit are modified during the fill. After the
     * operation, the position is unchanged and the limit is increased to reflect the new data filled.
     * @return an <code>int</code> value indicating the number of bytes
     * filled or -1 if EOF is read or the input is shutdown.
     * @throws IOException if the endpoint is closed.
     */
    int fill(ByteBuffer buffer) throws IOException;


    /**
     * Flush data from the passed header/buffer to this endpoint.  As many bytes as can be consumed
     * are taken from the header/buffer position up until the buffer limit.  The header/buffers position
     * is updated to indicate how many bytes have been consumed.
     * @param buffer the buffers to flush
     * @return True IFF all the buffers have been consumed and the endpoint has flushed the data to its
     * destination (ie is not buffering any data).
     * @throws IOException If the endpoint is closed or output is shutdown.
     */
    boolean flush(ByteBuffer... buffer) throws IOException;

    /* ------------------------------------------------------------ */
    /**
     * @return The underlying transport object (socket, channel, etc.)
     */
    Object getTransport();

    /* ------------------------------------------------------------ */
    /** Get the max idle time in ms.
     * <p>The max idle time is the time the endpoint can be idle before
     * extraordinary handling takes place.
     * @return the max idle time in ms or if ms &lt;= 0 implies an infinite timeout
     */
    long getIdleTimeout();

    /* ------------------------------------------------------------ */
    /** Set the idle timeout.
     * @param idleTimeout the idle timeout in MS. Timeout &lt;= 0 implies an infinite timeout
     */
    void setIdleTimeout(long idleTimeout);


    /**
     * <p>Requests callback methods to be invoked when a call to {@link #fill(ByteBuffer)} would return data or EOF.</p>
     *
     * @param callback the callback to call when an error occurs or we are readable.
     * @throws ReadPendingException if another read operation is concurrent.
     */
    void fillInterested(Callback callback) throws ReadPendingException;

    /**
     * <p>Requests callback methods to be invoked when a call to {@link #fill(ByteBuffer)} would return data or EOF.</p>
     *
     * @param callback the callback to call when an error occurs or we are readable.
     * @return true if set
     */
    boolean tryFillInterested(Callback callback);

    /**
     * @return whether {@link #fillInterested(Callback)} has been called, but {@link #fill(ByteBuffer)} has not yet
     * been called
     */
    boolean isFillInterested();

    /**
     * <p>Writes the given buffers via {@link #flush(ByteBuffer...)} and invokes callback methods when either
     * all the data has been flushed or an error occurs.</p>
     *
     * @param callback the callback to call when an error occurs or the write completed.
     * @param buffers one or more {@link ByteBuffer}s that will be flushed.
     * @throws WritePendingException if another write operation is concurrent.
     */
    void write(Callback callback, ByteBuffer... buffers) throws WritePendingException;

    /**
     * @return the {@link Connection} associated with this {@link EndPoint}
     * @see #setConnection(Connection)
     */
    Connection getConnection();

    /**
     * @param connection the {@link Connection} associated with this {@link EndPoint}
     * @see #getConnection()
     * @see #upgrade(Connection)
     */
    void setConnection(Connection connection);

    /**
     * <p>Callback method invoked when this {@link EndPoint} is opened.</p>
     * @see #onClose()
     */
    void onOpen();

    /**
     * <p>Callback method invoked when this {@link EndPoint} is close.</p>
     * @see #onOpen()
     */
    void onClose();

    /** Is the endpoint optimized for DirectBuffer usage
     * @return True if direct buffers can be used optimally.
     */
    boolean isOptimizedForDirectBuffers();


    /** Upgrade connections.
     * Close the old connection, update the endpoint and open the new connection.
     * If the oldConnection is an instance of {@link Connection.UpgradeFrom} then
     * a prefilled buffer is requested and passed to the newConnection if it is an instance
     * of {@link Connection.UpgradeTo}
     * @param newConnection The connection to upgrade to
     */
    public void upgrade(Connection newConnection);
}
