/**
 * 
 */

package com.blade.oauth2.request;

import com.blade.oauth2.base.request.OAuthTokenBaseRequest;
import com.blade.oauth2.base.validator.OAuthValidator;
import com.blade.oauth2.exception.OAuthProblemException;
import com.blade.oauth2.message.types.GrantType;
import com.blade.oauth2.validator.AuthorizationCodeValidator;
import com.blade.oauth2.validator.ClientCredentialValidator;
import com.blade.oauth2.validator.PasswordCredentialValidator;
import com.blade.oauth2.validator.RefreshTokenValidator;
import com.blade.web.http.Request;


/**
 * The Default OAuth Authorization Server class that validates whether a given HttpServletRequest is a valid
 * OAuth Token request.
 * <p/>
 * IMPORTANT: This OAuthTokenRequest assumes that a token request requires client authentication.
 * Please see section 3.2.1 of the OAuth Specification: http://tools.ietf.org/html/rfc6749#section-3.2.1
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class OAuthGrantRequest extends OAuthTokenBaseRequest {

    /**
     * Create an OAuth Token request from a given HttpSerlvetRequest
     *
     * @param request the HttpServletRequest that is validated and transformed into the OAuth Token Request
     * @throws OAuthProblemException if the request was not a valid Token request this exception is thrown.
     */
    public OAuthGrantRequest(Request request) throws OAuthProblemException {
        super(request);
    }

    @Override
    protected OAuthValidator<Request> initValidator() throws OAuthProblemException {
        validators.put(GrantType.PASSWORD.toString(), PasswordCredentialValidator.class);
        validators.put(GrantType.CLIENT_CREDENTIALS.toString(), ClientCredentialValidator.class);
        validators.put(GrantType.AUTHORIZATION_CODE.toString(), AuthorizationCodeValidator.class);
        validators.put(GrantType.REFRESH_TOKEN.toString(), RefreshTokenValidator.class);
        return super.initValidator();
    }
}
