/**
 * 
 */
package com.blade.oauth2.base.validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import blade.kit.StringKit;

import com.blade.oauth2.OAuth;
import com.blade.oauth2.exception.OAuthProblemException;
import com.blade.oauth2.kit.OAuthKit;
import com.blade.web.http.Request;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 * @param <T>
 */
public abstract class OAuthBaseValidator<T extends Request> implements OAuthValidator<T> {

    protected List<String> requiredParams = new ArrayList<String>();
    protected Map<String, String[]> optionalParams = new HashMap<String, String[]>();
    protected List<String> notAllowedParams = new ArrayList<String>();
    protected boolean enforceClientAuthentication = false;

    @Override
    public void validateMethod(T request) throws OAuthProblemException {
        if (!request.method().equals(OAuth.HttpMethod.POST)) {
            throw OAuthKit.handleOAuthProblemException("Method not set to POST.");
        }
    }

    @Override
    public void validateContentType(T request) throws OAuthProblemException {
        String contentType = request.contentType();
        final String expectedContentType = OAuth.ContentType.URL_ENCODED;
        if (!OAuthKit.hasContentType(contentType, expectedContentType)) {
            throw OAuthKit.handleBadContentTypeException(expectedContentType);
        }
    }

    @Override
    public void validateRequiredParameters(T request) throws OAuthProblemException {
        final Set<String> missingParameters = new HashSet<String>();
        for (String requiredParam : requiredParams) {
            String val = request.query(requiredParam);
            if (StringKit.isNotBlank(val)) {
                missingParameters.add(requiredParam);
            }
        }
        if (!missingParameters.isEmpty()) {
            throw OAuthKit.handleMissingParameters(missingParameters);
        }
    }

    @Override
    public void validateOptionalParameters(T request) throws OAuthProblemException {
        final Set<String> missingParameters = new HashSet<String>();

        for (Map.Entry<String, String[]> requiredParam : optionalParams.entrySet()) {
            final String paramName = requiredParam.getKey();
            String val = request.query(paramName);
            if (StringKit.isNotBlank(val)) {
                String[] dependentParams = requiredParam.getValue();
                if (null!=dependentParams&&dependentParams.length > 0) {
                    for (String dependentParam : dependentParams) {
                        val = request.query(dependentParam);
                        if (StringKit.isBlank(val)) {
                            missingParameters.add(dependentParam);
                        }
                    }
                }
            }
        }

        if (!missingParameters.isEmpty()) {
            throw OAuthKit.handleMissingParameters(missingParameters);
        }
    }

    @Override
    public void validateNotAllowedParameters(T request) throws OAuthProblemException {
        List<String> notAllowedParameters = new ArrayList<String>();
        for (String requiredParam : notAllowedParams) {
            String val = request.query(requiredParam);
            if (StringKit.isNotBlank(val)) {
                notAllowedParameters.add(requiredParam);
            }
        }
        if (!notAllowedParameters.isEmpty()) {
            throw OAuthKit.handleNotAllowedParametersOAuthException(notAllowedParameters);
        }
    }

    @Override
    public void validateClientAuthenticationCredentials(T request) throws OAuthProblemException {
        if (enforceClientAuthentication) {
            Set<String> missingParameters = new HashSet<String>();
            String clientAuthHeader = request.header(OAuth.HeaderType.AUTHORIZATION);
            String[] clientCreds = OAuthKit.decodeClientAuthenticationHeader(clientAuthHeader);

            // Only fallback to params if the auth header is not correct. Don't allow a mix of auth header vs params
            if (clientCreds == null || StringKit.isBlank(clientCreds[0]) || StringKit.isBlank(clientCreds[1])) {

                if (StringKit.isBlank(request.query(OAuth.OAUTH_CLIENT_ID))) {
                    missingParameters.add(OAuth.OAUTH_CLIENT_ID);
                }
                if (StringKit.isBlank(request.query(OAuth.OAUTH_CLIENT_SECRET))) {
                    missingParameters.add(OAuth.OAUTH_CLIENT_SECRET);
                }
            }

            if (!missingParameters.isEmpty()) {
                throw OAuthKit.handleMissingParameters(missingParameters);
            }
        }
    }

    @Override
    public void performAllValidations(T request) throws OAuthProblemException {
        this.validateContentType(request);
        this.validateMethod(request);
        this.validateRequiredParameters(request);
        this.validateOptionalParameters(request);
        this.validateNotAllowedParameters(request);
        this.validateClientAuthenticationCredentials(request);
    }
}
