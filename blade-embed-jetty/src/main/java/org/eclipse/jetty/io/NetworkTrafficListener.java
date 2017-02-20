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

import java.net.Socket;
import java.nio.ByteBuffer;

/**
 * <p>A listener for raw network traffic within Jetty.</p>
 * <p>{@link NetworkTrafficListener}s can be installed in a
 * <code>org.eclipse.jetty.server.nio.NetworkTrafficSelectChannelConnector</code>,
 * and are notified of the following network traffic events:</p>
 * <ul>
 * <li>Connection opened, when the server has accepted the connection from a remote client</li>
 * <li>Incoming bytes, when the server receives bytes sent from a remote client</li>
 * <li>Outgoing bytes, when the server sends bytes to a remote client</li>
 * <li>Connection closed, when the server has closed the connection to a remote client</li>
 * </ul>
 * <p>{@link NetworkTrafficListener}s can be used to log the network traffic viewed by
 * a Jetty server (for example logging to filesystem) for activities such as debugging
 * or request/response cycles or for replaying request/response cycles to other servers.</p>
 */
public interface NetworkTrafficListener
{
    /**
     * <p>Callback method invoked when a connection from a remote client has been accepted.</p>
     * <p>The {@code socket} parameter can be used to extract socket address information of
     * the remote client.</p>
     *
     * @param socket the socket associated with the remote client
     */
    public void opened(Socket socket);

    /**
     * <p>Callback method invoked when bytes sent by a remote client arrived on the server.</p>
     *
     * @param socket the socket associated with the remote client
     * @param bytes  the read-only buffer containing the incoming bytes
     */
    public void incoming(Socket socket, ByteBuffer bytes);

    /**
     * <p>Callback method invoked when bytes are sent to a remote client from the server.</p>
     * <p>This method is invoked after the bytes have been actually written to the remote client.</p>
     *
     * @param socket the socket associated with the remote client
     * @param bytes  the read-only buffer containing the outgoing bytes
     */
    public void outgoing(Socket socket, ByteBuffer bytes);

    /**
     * <p>Callback method invoked when a connection to a remote client has been closed.</p>
     * <p>The {@code socket} parameter is already closed when this method is called, so it
     * cannot be queried for socket address information of the remote client.<br>
     * However, the {@code socket} parameter is the same object passed to {@link #opened(Socket)},
     * so it is possible to map socket information in {@link #opened(Socket)} and retrieve it
     * in this method.
     *
     * @param socket the (closed) socket associated with the remote client
     */
    public void closed(Socket socket);

    /**
     * <p>A commodity class that implements {@link NetworkTrafficListener} with empty methods.</p>
     */
    public static class Adapter implements NetworkTrafficListener
    {
        public void opened(Socket socket)
        {
        }

        public void incoming(Socket socket, ByteBuffer bytes)
        {
        }

        public void outgoing(Socket socket, ByteBuffer bytes)
        {
        }

        public void closed(Socket socket)
        {
        }
    }
}
