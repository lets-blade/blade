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

import java.io.IOException;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;
import org.eclipse.jetty.util.security.Constraint;

public class SpnegoAuthenticator extends LoginAuthenticator
{
    private static final Logger LOG = Log.getLogger(SpnegoAuthenticator.class);
    private String _authMethod = Constraint.__SPNEGO_AUTH;

    public SpnegoAuthenticator()
    {
    }

    /**
     * Allow for a custom authMethod value to be set for instances where SPENGO may not be appropriate
     * @param authMethod the auth method
     */
    public SpnegoAuthenticator( String authMethod )
    {
        _authMethod = authMethod;
    }

    @Override
    public String getAuthMethod()
    {
        return _authMethod;
    }

    @Override
    public Authentication validateRequest(ServletRequest request, ServletResponse response, boolean mandatory) throws ServerAuthException
    {
        HttpServletRequest req = (HttpServletRequest)request;
        HttpServletResponse res = (HttpServletResponse)response;

        String header = req.getHeader(HttpHeader.AUTHORIZATION.asString());

        if (!mandatory)
        {
            return new DeferredAuthentication(this);
        }

        // check to see if we have authorization headers required to continue
        if ( header == null )
        {
            try
            {
                 if (DeferredAuthentication.isDeferred(res))
                 {
                     return Authentication.UNAUTHENTICATED;
                 }

                LOG.debug("SpengoAuthenticator: sending challenge");
                res.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), HttpHeader.NEGOTIATE.asString());
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return Authentication.SEND_CONTINUE;
            }
            catch (IOException ioe)
            {
                throw new ServerAuthException(ioe);
            }
        }
        else if (header != null && header.startsWith(HttpHeader.NEGOTIATE.asString()))
        {
            String spnegoToken = header.substring(10);

            UserIdentity user = login(null,spnegoToken, request);

            if ( user != null )
            {
                return new UserAuthentication(getAuthMethod(),user);
            }
        }

        return Authentication.UNAUTHENTICATED;
    }

    @Override
    public boolean secureResponse(ServletRequest request, ServletResponse response, boolean mandatory, User validatedUser) throws ServerAuthException
    {
        return true;
    }

}
