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

import java.io.InputStream;
import java.security.KeyStore;
import java.security.Principal;
import java.security.cert.CRL;
import java.security.cert.X509Certificate;
import java.util.Collection;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.security.ServerAuthException;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Authentication.User;
import org.eclipse.jetty.server.UserIdentity;
import org.eclipse.jetty.util.B64Code;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.security.CertificateUtils;
import org.eclipse.jetty.util.security.CertificateValidator;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.security.Password;

public class ClientCertAuthenticator extends LoginAuthenticator
{
    /** String name of keystore password property. */
    private static final String PASSWORD_PROPERTY = "org.eclipse.jetty.ssl.password";

    /** Truststore path */
    private String _trustStorePath;
    /** Truststore provider name */
    private String _trustStoreProvider;
    /** Truststore type */
    private String _trustStoreType = "JKS";
    /** Truststore password */
    private transient Password _trustStorePassword;

    /** Set to true if SSL certificate validation is required */
    private boolean _validateCerts;
    /** Path to file that contains Certificate Revocation List */
    private String _crlPath;
    /** Maximum certification path length (n - number of intermediate certs, -1 for unlimited) */
    private int _maxCertPathLength = -1;
    /** CRL Distribution Points (CRLDP) support */
    private boolean _enableCRLDP = false;
    /** On-Line Certificate Status Protocol (OCSP) support */
    private boolean _enableOCSP = false;
    /** Location of OCSP Responder */
    private String _ocspResponderURL;

    public ClientCertAuthenticator()
    {
        super();
    }

    @Override
    public String getAuthMethod()
    {
        return Constraint.__CERT_AUTH;
    }

    @Override
    public Authentication validateRequest(ServletRequest req, ServletResponse res, boolean mandatory) throws ServerAuthException
    {
        if (!mandatory)
            return new DeferredAuthentication(this);

        HttpServletRequest request = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)res;
        X509Certificate[] certs = (X509Certificate[]) request.getAttribute("javax.servlet.request.X509Certificate");

        try
        {
            // Need certificates.
            if (certs != null && certs.length > 0)
            {

                if (_validateCerts)
                {
                    KeyStore trustStore = getKeyStore(
                            _trustStorePath, _trustStoreType, _trustStoreProvider,
                            _trustStorePassword == null ? null :_trustStorePassword.toString());
                    Collection<? extends CRL> crls = loadCRL(_crlPath);
                    CertificateValidator validator = new CertificateValidator(trustStore, crls);
                    validator.validate(certs);
                }

                for (X509Certificate cert: certs)
                {
                    if (cert==null)
                        continue;

                    Principal principal = cert.getSubjectDN();
                    if (principal == null) principal = cert.getIssuerDN();
                    final String username = principal == null ? "clientcert" : principal.getName();

                    final char[] credential = B64Code.encode(cert.getSignature());

                    UserIdentity user = login(username, credential, req);
                    if (user!=null)
                    {
                        return new UserAuthentication(getAuthMethod(),user);
                    }
                }
            }

            if (!DeferredAuthentication.isDeferred(response))
            {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return Authentication.SEND_FAILURE;
            }

            return Authentication.UNAUTHENTICATED;
        }
        catch (Exception e)
        {
            throw new ServerAuthException(e.getMessage());
        }
    }

    @Deprecated
    protected KeyStore getKeyStore(InputStream storeStream, String storePath, String storeType, String storeProvider, String storePassword) throws Exception
    {
        return getKeyStore(storePath, storeType, storeProvider, storePassword);
    }
    /* ------------------------------------------------------------ */
    /**
     * Loads keystore using an input stream or a file path in the same
     * order of precedence.
     *
     * Required for integrations to be able to override the mechanism
     * used to load a keystore in order to provide their own implementation.
     *
     * @param storePath path of keystore file
     * @param storeType keystore type
     * @param storeProvider keystore provider
     * @param storePassword keystore password
     * @return created keystore
     * @throws Exception if unable to get keystore
     */
    protected KeyStore getKeyStore(String storePath, String storeType, String storeProvider, String storePassword) throws Exception
    {
        return CertificateUtils.getKeyStore(Resource.newResource(storePath), storeType, storeProvider, storePassword);
    }

    /* ------------------------------------------------------------ */
    /**
     * Loads certificate revocation list (CRL) from a file.
     *
     * Required for integrations to be able to override the mechanism used to
     * load CRL in order to provide their own implementation.
     *
     * @param crlPath path of certificate revocation list file
     * @return a (possibly empty) collection view of java.security.cert.CRL objects initialized with the data from the
     *         input stream.
     * @throws Exception if unable to load CRL
     */
    protected Collection<? extends CRL> loadCRL(String crlPath) throws Exception
    {
        return CertificateUtils.loadCRL(crlPath);
    }

    @Override
    public boolean secureResponse(ServletRequest req, ServletResponse res, boolean mandatory, User validatedUser) throws ServerAuthException
    {
        return true;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return true if SSL certificate has to be validated
     */
    public boolean isValidateCerts()
    {
        return _validateCerts;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param validateCerts
     *            true if SSL certificates have to be validated
     */
    public void setValidateCerts(boolean validateCerts)
    {
        _validateCerts = validateCerts;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The file name or URL of the trust store location
     */
    public String getTrustStore()
    {
        return _trustStorePath;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param trustStorePath
     *            The file name or URL of the trust store location
     */
    public void setTrustStore(String trustStorePath)
    {
        _trustStorePath = trustStorePath;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The provider of the trust store
     */
    public String getTrustStoreProvider()
    {
        return _trustStoreProvider;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param trustStoreProvider
     *            The provider of the trust store
     */
    public void setTrustStoreProvider(String trustStoreProvider)
    {
        _trustStoreProvider = trustStoreProvider;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return The type of the trust store (default "JKS")
     */
    public String getTrustStoreType()
    {
        return _trustStoreType;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param trustStoreType
     *            The type of the trust store (default "JKS")
     */
    public void setTrustStoreType(String trustStoreType)
    {
        _trustStoreType = trustStoreType;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param password
     *            The password for the trust store
     */
    public void setTrustStorePassword(String password)
    {
        _trustStorePassword = Password.getPassword(PASSWORD_PROPERTY,password,null);
    }

    /* ------------------------------------------------------------ */
    /** Get the crlPath.
     * @return the crlPath
     */
    public String getCrlPath()
    {
        return _crlPath;
    }

    /* ------------------------------------------------------------ */
    /** Set the crlPath.
     * @param crlPath the crlPath to set
     */
    public void setCrlPath(String crlPath)
    {
        _crlPath = crlPath;
    }

    /**
     * @return Maximum number of intermediate certificates in
     * the certification path (-1 for unlimited)
     */
    public int getMaxCertPathLength()
    {
        return _maxCertPathLength;
    }

    /* ------------------------------------------------------------ */
    /**
     * @param maxCertPathLength
     *            maximum number of intermediate certificates in
     *            the certification path (-1 for unlimited)
     */
    public void setMaxCertPathLength(int maxCertPathLength)
    {
        _maxCertPathLength = maxCertPathLength;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return true if CRL Distribution Points support is enabled
     */
    public boolean isEnableCRLDP()
    {
        return _enableCRLDP;
    }

    /* ------------------------------------------------------------ */
    /** Enables CRL Distribution Points Support
     * @param enableCRLDP true - turn on, false - turns off
     */
    public void setEnableCRLDP(boolean enableCRLDP)
    {
        _enableCRLDP = enableCRLDP;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return true if On-Line Certificate Status Protocol support is enabled
     */
    public boolean isEnableOCSP()
    {
        return _enableOCSP;
    }

    /* ------------------------------------------------------------ */
    /** Enables On-Line Certificate Status Protocol support
     * @param enableOCSP true - turn on, false - turn off
     */
    public void setEnableOCSP(boolean enableOCSP)
    {
        _enableOCSP = enableOCSP;
    }

    /* ------------------------------------------------------------ */
    /**
     * @return Location of the OCSP Responder
     */
    public String getOcspResponderURL()
    {
        return _ocspResponderURL;
    }

    /* ------------------------------------------------------------ */
    /** Set the location of the OCSP Responder.
     * @param ocspResponderURL location of the OCSP Responder
     */
    public void setOcspResponderURL(String ocspResponderURL)
    {
        _ocspResponderURL = ocspResponderURL;
    }
}
