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

package org.eclipse.jetty.security.authentication;

import java.security.Principal;

import javax.security.auth.Subject;

import org.eclipse.jetty.security.IdentityService;

/**
 * This is similar to the jaspi PasswordValidationCallback but includes user
 * principal and group info as well.
 *
 * @version $Rev: 4793 $ $Date: 2009-03-19 00:00:01 +0100 (Thu, 19 Mar 2009) $
 */
public class LoginCallbackImpl implements LoginCallback
{
    // initial data
    private final Subject subject;

    private final String userName;

    private Object credential;

    private boolean success;

    private Principal userPrincipal;

    private String[] roles = IdentityService.NO_ROLES;

    //TODO could use Credential instance instead of Object if Basic/Form create a Password object
    public LoginCallbackImpl (Subject subject, String userName, Object credential)
    {
        this.subject = subject;
        this.userName = userName;
        this.credential = credential;
    }

    public Subject getSubject()
    {
        return subject;
    }

    public String getUserName()
    {
        return userName;
    }

    public Object getCredential()
    {
        return credential;
    }

    public boolean isSuccess()
    {
        return success;
    }

    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    public Principal getUserPrincipal()
    {
        return userPrincipal;
    }

    public void setUserPrincipal(Principal userPrincipal)
    {
        this.userPrincipal = userPrincipal;
    }

    public String[] getRoles()
    {
        return roles;
    }

    public void setRoles(String[] groups)
    {
        this.roles = groups;
    }

    public void clearPassword()
    {
        if (credential != null)
        {
            credential = null;
        }
    }

}
