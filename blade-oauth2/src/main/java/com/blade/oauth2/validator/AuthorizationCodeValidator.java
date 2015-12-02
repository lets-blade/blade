/**
 * 
 */
package com.blade.oauth2.validator;

import com.blade.http.Request;
import com.blade.oauth2.OAuth;
import com.blade.oauth2.base.validator.OAuthBaseValidator;

/**
 * Validator that checks for the required fields in an OAuth Token request with the Authorization Code grant type.
 * This validator enforces client authentication either through basic authentication or body parameters.
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class AuthorizationCodeValidator extends OAuthBaseValidator<Request> {

    public AuthorizationCodeValidator() {
    	//Value MUST be set to "authorization_code".
        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
        requiredParams.add(OAuth.OAUTH_CODE);
        requiredParams.add(OAuth.OAUTH_REDIRECT_URI);
        requiredParams.add(OAuth.OAUTH_CLIENT_ID);
        enforceClientAuthentication = true;
    }

}
