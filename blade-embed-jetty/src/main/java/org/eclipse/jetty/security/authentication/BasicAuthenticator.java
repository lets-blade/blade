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
import java.nio.charset.StandardCharsets;

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
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.security.Constraint;

/**
 * @version $Rev: 4793 $ $Date: 2009-03-19 00:00:01 +0100 (Thu, 19 Mar 2009) $
 */
public class BasicAuthenticator extends LoginAuthenticator
{
    /* ------------------------------------------------------------ */
    public BasicAuthenticator()
    {
    }

    /* ------------------------------------------------------------ */
    /**
     * @see org.eclipse.jetty.security.Authenticator#getAuthMethod()
     */
    @Override
    public String getAuthMethod()
    {
        return Constraint.__BASIC_AUTH;
    }

 

    /* ------------------------------------------------------------ */
    /**
     * @see org.eclipse.jetty.security.Authenticator#validateRequest(ServletRequest, ServletResponse, boolean)
     */
    @Override
    public Authentication validateRequest(ServletRequest req, ServletResponse res, boolean mandatory) throws ServerAuthException
    {
        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        String credentials = request.getHeader(HttpHeader.AUTHORIZATION.asString());

        try
        {
            if (!mandatory)
                return new DeferredAuthentication(this);

            if (credentials != null)
            {
                int space=credentials.indexOf(' ');
                if (space>0)
                {
                    String method=credentials.substring(0,space);
                    if ("basic".equalsIgnoreCase(method))
                    {
                        credentials = credentials.substring(space+1);
                        credentials = B64Code.decode(credentials, StandardCharsets.ISO_8859_1);
                        int i = credentials.indexOf(':');
                        if (i>0)
                        {
                            String username = credentials.substring(0,i);
                            String password = credentials.substring(i+1);

                            UserIdentity user = login (username, password, request);
                            if (user!=null)
                            {
                                return new UserAuthentication(getAuthMethod(),user);
                            }
                        }
                    }
                }
            }

            if (DeferredAuthentication.isDeferred(response))
                return Authentication.UNAUTHENTICATED;

            response.setHeader(HttpHeader.WWW_AUTHENTICATE.asString(), "basic realm=\"" + _loginService.getName() + '"');
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            return Authentication.SEND_CONTINUE;
        }
        catch (IOException e)
        {
            throw new ServerAuthException(e);
        }
    }

    @Override
    public boolean secureResponse(ServletRequest req, ServletResponse res, boolean mandatory, User validatedUser) throws ServerAuthException
    {
        return true;
    }

}
