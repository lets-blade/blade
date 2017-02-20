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

/**
 * Associates UserIdentities from with threads and UserIdentity.Contexts.
 */
public interface IdentityService
{
    final static String[] NO_ROLES = new String[]{};

    /* ------------------------------------------------------------ */
    /**
     * Associate a user identity with the current thread.
     * This is called with as a thread enters the
     * {@link SecurityHandler#handle(String, org.eclipse.jetty.server.Request, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)}
     * method and then again with a null argument as that call exits.
     * @param user The current user or null for no user to associated.
     * @return an object representing the previous associated state
     */
    Object associate(UserIdentity user);

    /* ------------------------------------------------------------ */
    /**
     * Disassociate the user identity from the current thread
     * and restore previous identity.
     * @param previous The opaque object returned from a call to {@link IdentityService#associate(UserIdentity)}
     */
    void disassociate(Object previous);

    /* ------------------------------------------------------------ */
    /**
     * Associate a runas Token with the current user and thread.
     * @param user The UserIdentity
     * @param token The runAsToken to associate.
     * @return The previous runAsToken or null.
     */
    Object setRunAs(UserIdentity user, RunAsToken token);

    /* ------------------------------------------------------------ */
    /**
     * Disassociate the current runAsToken from the thread
     * and reassociate the previous token.
     * @param token RUNAS returned from previous associateRunAs call
     */
    void unsetRunAs(Object token);

    /* ------------------------------------------------------------ */
    /**
     * Create a new UserIdentity for use with this identity service.
     * The UserIdentity should be immutable and able to be cached.
     *
     * @param subject Subject to include in UserIdentity
     * @param userPrincipal Principal to include in UserIdentity.  This will be returned from getUserPrincipal calls
     * @param roles set of roles to include in UserIdentity.
     * @return A new immutable UserIdententity
     */
    UserIdentity newUserIdentity(Subject subject, Principal userPrincipal, String[] roles);

    /* ------------------------------------------------------------ */
    /**
     * Create a new RunAsToken from a runAsName (normally a role).
     * @param runAsName Normally a role name
     * @return A new immutable RunAsToken
     */
    RunAsToken newRunAsToken(String runAsName);

    /* ------------------------------------------------------------ */
    UserIdentity getSystemUserIdentity();
}
