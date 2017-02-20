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
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.IdentityService;
import org.eclipse.jetty.security.LoginService;
import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.IO;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Logger;

public class DeferredAuthentication implements Authentication.Deferred
{
    private static final Logger LOG = Log.getLogger(DeferredAuthentication.class);
    protected final LoginAuthenticator _authenticator;
    private Object _previousAssociation;

    /* ------------------------------------------------------------ */
    public DeferredAuthentication(LoginAuthenticator authenticator)
    {
        if (authenticator == null)
            throw new NullPointerException("No Authenticator");
        this._authenticator = authenticator;
    }

    /* ------------------------------------------------------------ */
    /**
     * @see Deferred#authenticate(ServletRequest)
     */
    @Override
    public Authentication authenticate(ServletRequest request)
    {
        try
        {
            Authentication authentication = _authenticator.validateRequest(request,__deferredResponse,true);

            if (authentication!=null && (authentication instanceof User) && !(authentication instanceof ResponseSent))
            {
                LoginService login_service= _authenticator.getLoginService();
                IdentityService identity_service=login_service.getIdentityService();
                
                if (identity_service!=null)
                    _previousAssociation=identity_service.associate(((User)authentication).getUserIdentity());
                
                return authentication;
            }
        }
        catch (ServerAuthException e)
        {
            LOG.debug(e);
        }

        return this;
    }

    /* ------------------------------------------------------------ */
    /**
     * @see Deferred#authenticate(ServletRequest, ServletResponse)
     */
    @Override
    public Authentication authenticate(ServletRequest request, ServletResponse response)
    {
        try
        {
            LoginService login_service= _authenticator.getLoginService();
            IdentityService identity_service=login_service.getIdentityService();
            
            Authentication authentication = _authenticator.validateRequest(request,response,true);
            if (authentication instanceof User && identity_service!=null)
                _previousAssociation=identity_service.associate(((User)authentication).getUserIdentity());
            return authentication;
        }
        catch (ServerAuthException e)
        {
            LOG.debug(e);
        }
        return this;
    }

    /* ------------------------------------------------------------ */
    /**
     * @see Deferred#login(String, Object, ServletRequest)
     */
    @Override
    public Authentication login(String username, Object password, ServletRequest request)
    {
        if (username == null)
            return null;
        
        UserIdentity identity = _authenticator.login(username, password, request);
        if (identity != null)
        {
            IdentityService identity_service = _authenticator.getLoginService().getIdentityService();
            UserAuthentication authentication = new UserAuthentication("API",identity);
            if (identity_service != null)
                _previousAssociation=identity_service.associate(identity);
            return authentication;
        }
        return null;
    }

    /* ------------------------------------------------------------ */
    public Object getPreviousAssociation()
    {
        return _previousAssociation;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param response the response
     * @return true if this response is from a deferred call to {@link #authenticate(ServletRequest)}
     */
    public static boolean isDeferred(HttpServletResponse response)
    {
        return response==__deferredResponse;
    }

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    final static HttpServletResponse __deferredResponse = new HttpServletResponse()
    {
        @Override
        public void addCookie(Cookie cookie)
        {
        }

        @Override
        public void addDateHeader(String name, long date)
        {
        }

        @Override
        public void addHeader(String name, String value)
        {
        }

        @Override
        public void addIntHeader(String name, int value)
        {
        }

        @Override
        public boolean containsHeader(String name)
        {
            return false;
        }

        @Override
        public String encodeRedirectURL(String url)
        {
            return null;
        }

        @Override
        public String encodeRedirectUrl(String url)
        {
            return null;
        }

        @Override
        public String encodeURL(String url)
        {
            return null;
        }

        @Override
        public String encodeUrl(String url)
        {
            return null;
        }

        @Override
        public void sendError(int sc) throws IOException
        {
        }

        @Override
        public void sendError(int sc, String msg) throws IOException
        {
        }

        @Override
        public void sendRedirect(String location) throws IOException
        {
        }

        @Override
        public void setDateHeader(String name, long date)
        {
        }

        @Override
        public void setHeader(String name, String value)
        {
        }

        @Override
        public void setIntHeader(String name, int value)
        {
        }

        @Override
        public void setStatus(int sc)
        {
        }

        @Override
        public void setStatus(int sc, String sm)
        {
        }

        @Override
        public void flushBuffer() throws IOException
        {
        }

        @Override
        public int getBufferSize()
        {
            return 1024;
        }

        @Override
        public String getCharacterEncoding()
        {
            return null;
        }

        @Override
        public String getContentType()
        {
            return null;
        }

        @Override
        public Locale getLocale()
        {
            return null;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException
        {
            return __nullOut;
        }

        @Override
        public PrintWriter getWriter() throws IOException
        {
            return IO.getNullPrintWriter();
        }

        @Override
        public boolean isCommitted()
        {
            return true;
        }

        @Override
        public void reset()
        {
        }

        @Override
        public void resetBuffer()
        {
        }

        @Override
        public void setBufferSize(int size)
        {
        }

        @Override
        public void setCharacterEncoding(String charset)
        {
        }

        @Override
        public void setContentLength(int len)
        {
        }
        
        public void setContentLengthLong(long len)
        {
           
        }

        @Override
        public void setContentType(String type)
        {
        }

        @Override
        public void setLocale(Locale loc)
        {
        }

        @Override
        public Collection<String> getHeaderNames()
        {
            return Collections.emptyList();
        }

        @Override
        public String getHeader(String arg0)
        {
            return null;
        }

        @Override
        public Collection<String> getHeaders(String arg0)
        {
            return Collections.emptyList();
        }

        @Override
        public int getStatus()
        {
            return 0;
        }


    };

    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    /* ------------------------------------------------------------ */
    private static ServletOutputStream __nullOut = new ServletOutputStream()
    {
        @Override
        public void write(int b) throws IOException
        {
        }
        
        @Override
        public void print(String s) throws IOException
        {
        }
        
        @Override
        public void println(String s) throws IOException
        {
        }

     
        @Override
        public void setWriteListener(WriteListener writeListener)
        {
            
        }

        @Override
        public boolean isReady()
        {
            return false;
        }
    };


}
