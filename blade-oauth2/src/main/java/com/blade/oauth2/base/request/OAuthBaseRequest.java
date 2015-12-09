/**
 * 
 */

package com.blade.oauth2.base.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.blade.oauth2.OAuth;
import com.blade.oauth2.base.validator.OAuthValidator;
import com.blade.oauth2.exception.OAuthProblemException;
import com.blade.oauth2.kit.OAuthKit;
import com.blade.web.http.Request;

/**
 * The Abstract OAuth request for the Authorization server.
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public abstract class OAuthBaseRequest {

    protected Request request;
    protected OAuthValidator<Request> validator;
    
    protected Map<String, Class<? extends OAuthValidator<Request>>> validators =
        new HashMap<String, Class<? extends OAuthValidator<Request>>>();

    public OAuthBaseRequest(Request request) throws OAuthProblemException {
        this.request = request;
        validate();
    }

    public OAuthBaseRequest() {
    }

	protected void validate() throws OAuthProblemException {
		validator = initValidator();
		validator.validateMethod(request);
		validator.validateContentType(request);
		validator.validateRequiredParameters(request);
		validator.validateClientAuthenticationCredentials(request);
	}

    protected abstract OAuthValidator<Request> initValidator() throws OAuthProblemException;

    public String getParam(String name) {
        return request.query(name);
    }

    public String getClientId() {
        String[] creds = OAuthKit.decodeClientAuthenticationHeader(request.header(OAuth.HeaderType.AUTHORIZATION));
        if (creds != null) {
            return creds[0];
        }
        return getParam(OAuth.OAUTH_CLIENT_ID);
    }

    public String getRedirectURI() {
        return getParam(OAuth.OAUTH_REDIRECT_URI);
    }

    public String getClientSecret() {
        String[] creds = OAuthKit.decodeClientAuthenticationHeader(request.header(OAuth.HeaderType.AUTHORIZATION));
        if (creds != null) {
            return creds[1];
        }
        return getParam(OAuth.OAUTH_CLIENT_SECRET);
    }

    /**
     *
     * @return
     */
    public boolean isClientAuthHeaderUsed() {
        return OAuthKit.decodeClientAuthenticationHeader(request.header(OAuth.HeaderType.AUTHORIZATION)) != null;
    }

	public String getState() {
		return getParam(OAuth.OAUTH_STATE);
	}

    public Set<String> getScopes() {
        String scopes = getParam(OAuth.OAUTH_SCOPE);
        return OAuthKit.decodeScopes(scopes);
    }

}
