package com.blade.oauth2;

public abstract class OAuthError {

    //error response params
    public static final String OAUTH_ERROR = "error";
    public static final String OAUTH_ERROR_DESCRIPTION = "error_description";
    public static final String OAUTH_ERROR_URI = "error_uri";

    public static final class CodeResponse {
        /**
         * The request is missing a required parameter, includes an
        unsupported parameter value, or is otherwise malformed.
         */
        public static final String INVALID_REQUEST = "invalid_request";
        
        /**
         * The client is not authorized to request an authorization
        code using this method.
         */
        public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";

        /**
         * The resource owner or authorization server denied the
        request.
         */
        public static final String ACCESS_DENIED = "access_denied";

        /**
         * The authorization server does not support obtaining an
        authorization code using this method.
         */
        public static final String UNSUPPORTED_RESPONSE_TYPE = "unsupported_response_type";

        /**
         * The requested scope is invalid, unknown, or malformed.
         */
        public static final String INVALID_SCOPE = "invalid_scope";

        /**
         * The authorization server encountered an unexpected
        condition which prevented it from fulfilling the request.
         */
        public static final String SERVER_ERROR = "server_error";

        /**
         *         The authorization server is currently unable to handle
        the request due to a temporary overloading or maintenance
        of the server.
         */
        public static final String TEMPORARILY_UNAVAILABLE = "temporarily_unavailable";

    }

    public static final class TokenResponse {
        /**
        The request is missing a required parameter, includes an
        unsupported parameter value, repeats a parameter,
        includes multiple credentials, utilizes more than one
        mechanism for authenticating the client, or is otherwise
        malformed.
        */
        public static final String INVALID_REQUEST = "invalid_request";
        /**
        Client authentication failed (e.g. unknown client, no
        client authentication included, or unsupported
        authentication method).  The authorization server MAY
        return an HTTP 401 (Unauthorized) status code to indicate
        which HTTP authentication schemes are supported.  If the
        client attempted to authenticate via the "Authorization"
        request header field, the authorization server MUST
        respond with an HTTP 401 (Unauthorized) status code, and
        include the "WWW-Authenticate" response header field
        matching the authentication scheme used by the client.
        */
        public static final String INVALID_CLIENT = "invalid_client";

        /**
        The provided authorization grant (e.g. authorization
        code, resource owner credentials, client credentials) is
        invalid, expired, revoked, does not match the redirection
        URI used in the authorization request, or was issued to
        another client.
        */
        public static final String INVALID_GRANT = "invalid_grant";

        /**
        The authenticated client is not authorized to use this
        authorization grant type.
        */
        public static final String UNAUTHORIZED_CLIENT = "unauthorized_client";

        /**
        The authorization grant type is not supported by the
        authorization server.
        */
        public static final String UNSUPPORTED_GRANT_TYPE = "unsupported_grant_type";

        /**
         * The requested scope is invalid, unknown, malformed, or exceeds the scope granted by the resource owner.
         */
        public static final String INVALID_SCOPE = "invalid_scope";
    }

    public static final class ResourceResponse {
    	/**
        The request is missing a required parameter, includes an
        unsupported parameter value, repeats a parameter,
        includes multiple credentials, utilizes more than one
        mechanism for authenticating the client, or is otherwise
        malformed.
        */
        public static final String INVALID_REQUEST = "invalid_request";
        
        
        public static final String EXPIRED_TOKEN = "expired_token";
        
        /**
         * The request requires higher privileges than provided by the
         * access token.
         */
        public static final String INSUFFICIENT_SCOPE = "insufficient_scope";
        
        /**
         * The access token provided is expired, revoked, malformed, or
         * invalid for other reasons.
         */
        public static final String INVALID_TOKEN = "invalid_token";
    }

}
