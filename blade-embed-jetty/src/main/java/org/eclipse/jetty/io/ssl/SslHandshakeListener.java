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

package org.eclipse.jetty.io.ssl;

import java.util.EventListener;
import java.util.EventObject;

import javax.net.ssl.SSLEngine;

/**
 * <p>Implementations of this interface are notified of TLS handshake events.</p>
 * <p>Similar to {@link javax.net.ssl.HandshakeCompletedListener}, but for {@link SSLEngine}.</p>
 * <p>Typical usage if to add instances of this class as beans to a server connector, or
 * to a client connector.</p>
 */
public interface SslHandshakeListener extends EventListener
{
    /**
     * <p>Callback method invoked when the TLS handshake succeeds.</p>
     *
     * @param event the event object carrying information about the TLS handshake event
     */
    default void handshakeSucceeded(Event event)
    {
    }

    /**
     * <p>Callback method invoked when the TLS handshake fails.</p>
     *
     * @param event the event object carrying information about the TLS handshake event
     * @param failure the failure that caused the TLS handshake to fail
     */
    default void handshakeFailed(Event event, Throwable failure)
    {
    }

    /**
     * <p>The event object carrying information about TLS handshake events.</p>
     */
    public static class Event extends EventObject
    {
        public Event(Object source)
        {
            super(source);
        }

        /**
         * @return the SSLEngine associated to the TLS handshake event
         */
        public SSLEngine getSSLEngine()
        {
            return (SSLEngine)getSource();
        }
    }
}
