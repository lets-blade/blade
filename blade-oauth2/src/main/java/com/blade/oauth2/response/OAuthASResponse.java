/**
 * 
 */

package com.blade.oauth2.response;


import com.blade.oauth2.OAuth;
import com.blade.oauth2.OAuthResponse;
import com.blade.oauth2.message.types.TokenType;
import com.blade.web.http.HttpStatus;
import com.blade.web.http.Request;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class OAuthASResponse extends OAuthResponse {

    protected OAuthASResponse(String uri, int responseStatus) {
        super(uri, responseStatus);
    }

    public static OAuthAuthorizationResponseBuilder authorizationResponse(Request request) {
        return new OAuthAuthorizationResponseBuilder(request, HttpStatus.OK);
    }
    
    public static OAuthAuthorizationResponseBuilder authorizationResponse(Request request,int code) {
        return new OAuthAuthorizationResponseBuilder(request,code);
    }

    public static OAuthTokenResponseBuilder tokenResponse() {
        return new OAuthTokenResponseBuilder(HttpStatus.OK);
    }
    
    public static OAuthTokenResponseBuilder tokenResponse(int code) {
        return new OAuthTokenResponseBuilder(code);
    }

    public static class OAuthAuthorizationResponseBuilder extends OAuthResponseBuilder {

        public OAuthAuthorizationResponseBuilder(Request request,int responseCode) {
            super(responseCode);
            //AMBER-45
			String state = request.query(OAuth.OAUTH_STATE);
            if (state!=null){
            	this.setState(state);
            }
        }

        OAuthAuthorizationResponseBuilder setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
            return this;
        }

        public OAuthAuthorizationResponseBuilder setCode(String code) {
            this.parameters.put(OAuth.OAUTH_CODE, code);
            return this;
        }

        public OAuthAuthorizationResponseBuilder setAccessToken(String token) {
            this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, token);
            return this;
        }

        public OAuthAuthorizationResponseBuilder setExpiresIn(String expiresIn) {
            this.parameters.put(OAuth.OAUTH_EXPIRES_IN, expiresIn == null ? null : Long.valueOf(expiresIn));
            return this;
        }
        
        public OAuthAuthorizationResponseBuilder setExpiresIn(Long expiresIn) {
            this.parameters.put(OAuth.OAUTH_EXPIRES_IN, expiresIn);
            return this;
        }        
        
        public OAuthAuthorizationResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public OAuthAuthorizationResponseBuilder setParam(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }
    }


    public static class OAuthTokenResponseBuilder extends OAuthResponseBuilder {

        public OAuthTokenResponseBuilder(int responseCode) {
            super(responseCode);
            this.setTokenType(TokenType.BEARER);
        }

        public OAuthTokenResponseBuilder setAccessToken(String token) {
            this.parameters.put(OAuth.OAUTH_ACCESS_TOKEN, token);
            return this;
        }

        public OAuthTokenResponseBuilder setExpiresIn(String expiresIn) {
            this.parameters.put(OAuth.OAUTH_EXPIRES_IN, expiresIn == null ? null : Long.valueOf(expiresIn));
            return this;
        }

        public OAuthTokenResponseBuilder setRefreshToken(String refreshToken) {
            this.parameters.put(OAuth.OAUTH_REFRESH_TOKEN, refreshToken);
            return this;
        }
        
        public OAuthTokenResponseBuilder setTokenType(String tokenType) {
            this.parameters.put(OAuth.OAUTH_TOKEN_TYPE, tokenType);
            return this;
        }
        
        public OAuthTokenResponseBuilder setTokenType(TokenType tokenType) {
        	return this.setTokenType(String.valueOf(tokenType));
        }
        
        public OAuthTokenResponseBuilder setExampleParamter(String exampleParamter) {
        	this.parameters.put(OAuth.OAUTH_EXAMPLE_PARAMETER, exampleParamter);
        	return this;
        }

        public OAuthTokenResponseBuilder setParam(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public OAuthTokenResponseBuilder location(String location) {
            this.location = location;
            return this;
        }
    }

}
