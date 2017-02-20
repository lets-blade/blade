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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.eclipse.jetty.security.Authenticator;
import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Response;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.server.session.Session;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public abstract class LoginAuthenticator implements Authenticator
{
    private static final Logger LOG = Log.getLogger(LoginAuthenticator.class);

    protected LoginService _loginService;
    protected IdentityService _identityService;
    private boolean _renewSession;
    
    
    /* ------------------------------------------------------------ */
    protected LoginAuthenticator()
    {
    }

    /* ------------------------------------------------------------ */
    @Override
    public void prepareRequest(ServletRequest request)
    {
        //empty implementation as the default
    }


    /* ------------------------------------------------------------ */
    public UserIdentity login(String username, Object password, ServletRequest request)
    {
        UserIdentity user = _loginService.login(username,password, request);
        if (user!=null)
        {
            renewSession((HttpServletRequest)request, (request instanceof Request? ((Request)request).getResponse() : null));
            return user;
        }
        return null;
    }

    /* ------------------------------------------------------------ */
    @Override
    public void setConfiguration(AuthConfiguration configuration)
    {
        _loginService=configuration.getLoginService();
        if (_loginService==null)
            throw new IllegalStateException("No LoginService for "+this+" in "+configuration);
        _identityService=configuration.getIdentityService();
        if (_identityService==null)
            throw new IllegalStateException("No IdentityService for "+this+" in "+configuration);
        _renewSession=configuration.isSessionRenewedOnAuthentication();
    }
    
    
    /* ------------------------------------------------------------ */
    public LoginService getLoginService()
    {
        return _loginService;
    }
    
    
    /* ------------------------------------------------------------ */
    /** Change the session id.
     * The session is changed to a new instance with a new ID if and only if:<ul>
     * <li>A session exists.
     * <li>The {@link AuthConfiguration#isSessionRenewedOnAuthentication()} returns true.
     * <li>The session ID has been given to unauthenticated responses
     * </ul>
     * @param request the request
     * @param response the response
     * @return The new session.
     */
    protected HttpSession renewSession(HttpServletRequest request, HttpServletResponse response)
    {
        HttpSession httpSession = request.getSession(false);

        if (_renewSession && httpSession!=null)
        {
            synchronized (httpSession)
            {
                //if we should renew sessions, and there is an existing session that may have been seen by non-authenticated users
                //(indicated by SESSION_SECURED not being set on the session) then we should change id
                if (httpSession.getAttribute(Session.SESSION_CREATED_SECURE)!=Boolean.TRUE)
                {
                    if (httpSession instanceof Session)
                    {
                        Session s = (Session)httpSession;
                        String oldId = s.getId();
                        s.renewId(request);
                        s.setAttribute(Session.SESSION_CREATED_SECURE, Boolean.TRUE);
                        if (s.isIdChanged() && response != null && (response instanceof Response))
                            ((Response)response).addCookie(s.getSessionHandler().getSessionCookie(s, request.getContextPath(), request.isSecure()));
                        LOG.debug("renew {}->{}",oldId,s.getId());
                    }
                    else
                        LOG.warn("Unable to renew session "+httpSession);
                    
                    return httpSession;
                }
            }
        }
        return httpSession;
    }
}
