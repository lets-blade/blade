/**
 * http://www.rfcreader.com/#rfc6749
 * 4.4.2.  Access Token Request
 * 
 * The client can request an access token using only its client
   credentials (or other supported means of authentication) when the
   client is requesting access to the protected resources under its
   control, or those of another resource owner that have been previously
   arranged with the authorization server (the method of which is beyond
   the scope of this specification).

   The client credentials grant type MUST only be used by confidential
   clients.

     +---------+                                  +---------------+
     |         |                                  |               |
     |         |>--(A)- Client Authentication --->| Authorization |
     | Client  |                                  |     Server    |
     |         |<--(B)---- Access Token ---------<|               |
     |         |                                  |               |
     +---------+                                  +---------------+

                     Figure 6: Client Credentials Flow

   The flow illustrated in Figure 6 includes the following steps:

   (A)  The client authenticates with the authorization server and
        requests an access token from the token endpoint.

   (B)  The authorization server authenticates the client, and if valid,
        issues an access token.
 */
package com.blade.oauth2.validator;

import com.blade.oauth2.OAuth;
import com.blade.oauth2.base.validator.OAuthBaseValidator;
import com.blade.web.http.Request;

/**
 * Client Credentials Grant
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class ClientCredentialValidator extends OAuthBaseValidator<Request> {
   
	public ClientCredentialValidator() {
    	//Value MUST be set to "client_credentials".
        requiredParams.add(OAuth.OAUTH_GRANT_TYPE);
        enforceClientAuthentication = true;
    }
}
