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


/* ------------------------------------------------------------ */
/** A Jetty specialization of EOFException.
 * <p> This is thrown by Jetty to distinguish between EOF received from 
 * the connection, vs and EOF thrown by some application talking to some other file/socket etc.
 * The only difference in handling is that Jetty EOFs are logged less verbosely.
 */
public class EofException extends EOFException
{
    public EofException()
    {
    }
    
    public EofException(String reason)
    {
        super(reason);
    }
    
    public EofException(Throwable th)
    {
        if (th!=null)
            initCause(th);
    }
}
