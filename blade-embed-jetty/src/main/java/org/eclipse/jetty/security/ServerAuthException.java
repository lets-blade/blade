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

package org.eclipse.jetty.security;

import java.security.GeneralSecurityException;

/**
 * @version $Rev: 4466 $ $Date: 2009-02-10 23:42:54 +0100 (Tue, 10 Feb 2009) $
 */
public class ServerAuthException extends GeneralSecurityException
{

    public ServerAuthException()
    {
    }

    public ServerAuthException(String s)
    {
        super(s);
    }

    public ServerAuthException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public ServerAuthException(Throwable throwable)
    {
        super(throwable);
    }
}
