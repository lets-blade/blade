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

import org.eclipse.jetty.util.security.Constraint;

public class ConstraintMapping
{
    String _method;
    String[] _methodOmissions;

    String _pathSpec;

    Constraint _constraint;

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the constraint.
     */
    public Constraint getConstraint()
    {
        return _constraint;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param constraint The constraint to set.
     */
    public void setConstraint(Constraint constraint)
    {
        this._constraint = constraint;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the method.
     */
    public String getMethod()
    {
        return _method;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param method The method to set.
     */
    public void setMethod(String method)
    {
        this._method = method;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Returns the pathSpec.
     */
    public String getPathSpec()
    {
        return _pathSpec;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param pathSpec The pathSpec to set.
     */
    public void setPathSpec(String pathSpec)
    {
        this._pathSpec = pathSpec;
    }
    
    /* ------------------------------------------------------------ */
    /**
     * @param omissions The http-method-omission
     */
    public void setMethodOmissions(String[] omissions)
    {
        _methodOmissions = omissions;
    }
    
    /* ------------------------------------------------------------ */
    public String[] getMethodOmissions()
    {
        return _methodOmissions;
    }
}
