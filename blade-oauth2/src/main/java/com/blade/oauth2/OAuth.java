package com.blade.oauth2;

import com.blade.oauth2.message.types.ParameterStyle;
import com.blade.oauth2.message.types.TokenType;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public final class OAuth {

    public static final class HttpMethod {
        public static final String POST = "POST";
        public static final String GET = "GET";
        public static final String DELETE = "DELETE";
        public static final String PUT = "PUT";
    }

    public static final class HeaderType {
        public static final String CONTENT_TYPE = "Content-Type";
        public static final String WWW_AUTHENTICATE = "WWW-Authenticate";
        public static final String AUTHORIZATION = "Authorization";
    }

    public static final class WWWAuthHeader {
        public static final String REALM = "realm";
    }

    public static final class ContentType {
        public static final String URL_ENCODED = "application/x-www-form-urlencoded";
        public static final String JSON = "application/json";
    }

    public static final String OAUTH_RESPONSE_TYPE = "response_type";
    public static final String OAUTH_CLIENT_ID = "client_id";
    public static final String OAUTH_CLIENT_SECRET = "client_secret";
    public static final String OAUTH_REDIRECT_URI = "redirect_uri";
    public static final String OAUTH_USERNAME = "username";
    public static final String OAUTH_PASSWORD = "password";
    public static final String OAUTH_ASSERTION_TYPE = "assertion_type";
    public static final String OAUTH_ASSERTION = "assertion";
    public static final String OAUTH_SCOPE = "scope";
    public static final String OAUTH_STATE = "state";
    public static final String OAUTH_GRANT_TYPE = "grant_type";

    public static final String OAUTH_HEADER_NAME = "Bearer";

    //Authorization response params
    public static final String OAUTH_CODE = "code";
    public static final String OAUTH_ACCESS_TOKEN = "access_token";
    public static final String OAUTH_EXPIRES_IN = "expires_in";
    public static final String OAUTH_REFRESH_TOKEN = "refresh_token";
    public static final String OAUTH_EXAMPLE_PARAMETER = "example_parameter";
    
    public static final String OAUTH_TOKEN_TYPE = "token_type";

    public static final String OAUTH_TOKEN = "oauth_token";

    public static final String OAUTH_TOKEN_DRAFT_0 = "access_token";
    public static final String OAUTH_BEARER_TOKEN = "access_token";

    public static final ParameterStyle DEFAULT_PARAMETER_STYLE = ParameterStyle.HEADER;
    public static final TokenType DEFAULT_TOKEN_TYPE = TokenType.BEARER;
    
    public static final String OAUTH_VERSION_DIFFER = "oauth_signature_method";
}
