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

import javax.servlet.ServletRequest;

import org.eclipse.jetty.server.UserIdentity;


/* ------------------------------------------------------------ */
/**
 * Login Service Interface.
 * <p>
 * The Login service provides an abstract mechanism for an {@link Authenticator}
 * to check credentials and to create a {@link UserIdentity} using the 
 * set {@link IdentityService}.
 */
public interface LoginService
{
    
    /* ------------------------------------------------------------ */
    /**
     * @return Get the name of the login service (aka Realm name)
     */
    String getName();
    
    /* ------------------------------------------------------------ */
    /** Login a user.
     * @param username The user name
     * @param credentials The users credentials
     * @param request TODO
     * @return A UserIdentity if the credentials matched, otherwise null
     */
    UserIdentity login(String username, Object credentials, ServletRequest request);
    
    /* ------------------------------------------------------------ */
    /** Validate a user identity.
     * Validate that a UserIdentity previously created by a call 
     * to {@link #login(String, Object, ServletRequest)} is still valid.
     * @param user The user to validate
     * @return true if authentication has not been revoked for the user.
     */
    boolean validate(UserIdentity user);
    
    /* ------------------------------------------------------------ */
    /** Get the IdentityService associated with this Login Service.
     * @return the IdentityService associated with this Login Service.
     */
    IdentityService getIdentityService();
    
    /* ------------------------------------------------------------ */
    /** Set the IdentityService associated with this Login Service.
     * @param service the IdentityService associated with this Login Service.
     */
    void setIdentityService(IdentityService service);
    
    void logout(UserIdentity user);

}
