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

import java.security.Principal;

import javax.security.auth.Subject;

import org.eclipse.jetty.server.UserIdentity;


/* ------------------------------------------------------------ */
/**
 * The default implementation of UserIdentity.
 *
 */
public class DefaultUserIdentity implements UserIdentity
{
    private final Subject _subject;
    private final Principal _userPrincipal;
    private final String[] _roles;

    public DefaultUserIdentity(Subject subject, Principal userPrincipal, String[] roles)
    {
        _subject=subject;
        _userPrincipal=userPrincipal;
        _roles=roles;
    }

    public Subject getSubject()
    {
        return _subject;
    }

    public Principal getUserPrincipal()
    {
        return _userPrincipal;
    }

    public boolean isUserInRole(String role, Scope scope)
    {
        //Servlet Spec 3.1, pg 125
        if ("*".equals(role))
            return false;
        
        String roleToTest = null;
        if (scope!=null && scope.getRoleRefMap()!=null)
            roleToTest=scope.getRoleRefMap().get(role);

        //Servlet Spec 3.1, pg 125
        if (roleToTest == null)
            roleToTest = role;
       
        for (String r :_roles)
            if (r.equals(roleToTest))
                return true;
        return false;
    }

    @Override
    public String toString()
    {
        return DefaultUserIdentity.class.getSimpleName()+"('"+_userPrincipal+"')";
    }
}
