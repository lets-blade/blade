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

import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.Server;

/**
 * Authenticator Interface
 * <p>
 * An Authenticator is responsible for checking requests and sending
 * response challenges in order to authenticate a request.
 * Various types of {@link Authentication} are returned in order to
 * signal the next step in authentication.
 *
 * @version $Rev: 4793 $ $Date: 2009-03-19 00:00:01 +0100 (Thu, 19 Mar 2009) $
 */
public interface Authenticator
{
    /* ------------------------------------------------------------ */
    /**
     * Configure the Authenticator
     * 
     * @param configuration the configuration
     */
    void setConfiguration(AuthConfiguration configuration);

    /* ------------------------------------------------------------ */
    /**
     * @return The name of the authentication method
     */
    String getAuthMethod();
    
    
    /* ------------------------------------------------------------ */
    /**
     * Called prior to validateRequest. The authenticator can
     * manipulate the request to update it with information that
     * can be inspected prior to validateRequest being called.
     * The primary purpose of this method is to satisfy the Servlet
     * Spec 3.1 section 13.6.3 on handling Form authentication
     * where the http method of the original request causing authentication
     * is not the same as the http method resulting from the redirect
     * after authentication.
     * 
     * @param request the request to manipulate
     */
    void prepareRequest(ServletRequest request);
    

    /* ------------------------------------------------------------ */
    /** 
     * Validate a request
     * 
     * @param request The request
     * @param response The response
     * @param mandatory True if authentication is mandatory.
     * @return An Authentication.  If Authentication is successful, this will be a {@link User}. If a response has
     * been sent by the Authenticator (which can be done for both successful and unsuccessful authentications), then the result will
     * implement {@link Authentication.ResponseSent}.  If Authentication is not manditory, then a
     * {@link Authentication.Deferred} may be returned.
     *
     * @throws ServerAuthException if unable to validate request
     */
    Authentication validateRequest(ServletRequest request, ServletResponse response, boolean mandatory) throws ServerAuthException;

    /* ------------------------------------------------------------ */
    /**
     * is response secure
     * 
     * @param request the request 
     * @param response the response
     * @param mandatory if security is mandator
     * @param validatedUser the user that was validated
     * @return true if response is secure
     * @throws ServerAuthException if unable to test response
     */
    boolean secureResponse(ServletRequest request, ServletResponse response, boolean mandatory, User validatedUser) throws ServerAuthException;


    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /**
     * Authenticator Configuration
     */
    interface AuthConfiguration
    {
        String getAuthMethod();
        String getRealmName();

        /** 
         * Get a SecurityHandler init parameter
         * @see SecurityHandler#getInitParameter(String)
         * @param param parameter name
         * @return Parameter value or null
         */
        String getInitParameter(String param);

        /* ------------------------------------------------------------ */
        /** Get a SecurityHandler init parameter names
         * @see SecurityHandler#getInitParameterNames()
         * @return Set of parameter names
         */
        Set<String> getInitParameterNames();

        LoginService getLoginService();
        IdentityService getIdentityService();
        boolean isSessionRenewedOnAuthentication();
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /**
     * Authenticator Factory
     */
    interface Factory
    {
        Authenticator getAuthenticator(Server server, ServletContext context, AuthConfiguration configuration, IdentityService identityService, LoginService loginService);
    }
}
