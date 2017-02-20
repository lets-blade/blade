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



/**
 * @version $Rev: 4701 $ $Date: 2009-03-03 13:01:26 +0100 (Tue, 03 Mar 2009) $
 */
public class RoleRunAsToken implements RunAsToken
{
    private final String _runAsRole;

    public RoleRunAsToken(String runAsRole)
    {
        this._runAsRole = runAsRole;
    }

    public String getRunAsRole()
    {
        return _runAsRole;
    }

    public String toString()
    {
        return "RoleRunAsToken("+_runAsRole+")";
    }
}
