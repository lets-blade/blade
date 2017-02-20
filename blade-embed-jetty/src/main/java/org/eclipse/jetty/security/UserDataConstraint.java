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
 * @version $Rev: 4466 $ $Date: 2009-02-10 23:42:54 +0100 (Tue, 10 Feb 2009) $
 */
public enum UserDataConstraint
{
    None, Integral, Confidential;

    public static UserDataConstraint get(int dataConstraint)
    {
        if (dataConstraint < -1 || dataConstraint > 2) throw new IllegalArgumentException("Expected -1, 0, 1, or 2, not: " + dataConstraint);
        if (dataConstraint == -1) return None;
        return values()[dataConstraint];
    }

    public UserDataConstraint combine(UserDataConstraint other)
    {
        if (this.compareTo(other) < 0) return this;
        return other;
    }
}
