package com.blade.oauth2;

import java.util.HashMap;
import java.util.Map;

import blade.kit.StringKit;

import com.blade.oauth2.exception.OAuthProblemException;
import com.blade.oauth2.message.OAuthMessage;
import com.blade.oauth2.parameters.JSONBodyParametersApplier;
import com.blade.web.http.HttpStatus;
import com.blade.web.http.Request;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class OAuthResponse implements OAuthMessage {

    protected int responseStatus;
    protected String uri;
    protected String body;

    protected Map<String, String> headers = new HashMap<String, String>();

    protected OAuthResponse(String uri, int responseStatus) {
        this.uri = uri;
        this.responseStatus = responseStatus;
    }

    public static OAuthResponseBuilder status(int code) {
        return new OAuthResponseBuilder(code);
    }

    public static OAuthErrorResponseBuilder errorResponse(int code, Request request) {
        return new OAuthErrorResponseBuilder(code, request);
    }
    
    public static OAuthErrorResponseBuilder errorUnAuthResponse(Request request) {
    	OAuthErrorResponseBuilder errorRep =  new OAuthErrorResponseBuilder(HttpStatus.UNAUTHORIZED, request);
    	errorRep.setError(OAuthError.TokenResponse.UNAUTHORIZED_CLIENT);
    	return errorRep;
    }
    
    public static OAuthErrorResponseBuilder errorBadReqResponse(Request request) {
        return new OAuthErrorResponseBuilder(HttpStatus.BAD_REQUEST, request);
    }

    @Override
    public String getLocationUri() {
        return uri;
    }

    @Override
    public void setLocationUri(String uri) {
        this.uri = uri;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public void setBody(String body) {
        this.body = body;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public void addHeader(String name, String header) {
        headers.put(name, header);
    }

    public static class OAuthResponseBuilder {

        protected JSONBodyParametersApplier applier;
        protected Map<String, Object> parameters = new HashMap<String, Object>();
        protected int responseCode;
        protected String location;

        public OAuthResponseBuilder(int responseCode) {
            this.responseCode = responseCode;
        }

        public OAuthResponseBuilder location(String location) {
            this.location = location;
            return this;
        }

        public OAuthResponseBuilder setScope(String value) {
            this.parameters.put(OAuth.OAUTH_SCOPE, value);
            return this;
        }

        public OAuthResponseBuilder setParam(String key, String value) {
            this.parameters.put(key, value);
            return this;
        }

        public OAuthResponse buildJSONMessage() {
            OAuthResponse msg = new OAuthResponse(location, responseCode);
            this.applier = new JSONBodyParametersApplier();
            return (OAuthResponse)applier.applyOAuthParameters(msg, parameters);
        }

    }

    public static class OAuthErrorResponseBuilder extends OAuthResponseBuilder {

        public OAuthErrorResponseBuilder(int responseCode, Request request) {
            super(responseCode);
            
            String state = request.query(OAuth.OAUTH_STATE);
            if (StringKit.isNotBlank(state)){
            	this.setState(state);
            }else{
            	this.setState(null);
            }
            
            String errorUri = request.query(OAuthError.OAUTH_ERROR_URI);
            if (StringKit.isNotBlank(errorUri)) {
				this.setErrorUri(errorUri);
			}
        }

        public OAuthErrorResponseBuilder error(OAuthProblemException ex) {
            this.parameters.put(OAuthError.OAUTH_ERROR, ex.getError());
            this.parameters.put(OAuthError.OAUTH_ERROR_DESCRIPTION, ex.getDescription());
            return this;
        }

        public OAuthErrorResponseBuilder setError(String error) {
            this.parameters.put(OAuthError.OAUTH_ERROR, error);
            return this;
        }

        public OAuthErrorResponseBuilder setErrorDescription(String desc) {
            this.parameters.put(OAuthError.OAUTH_ERROR_DESCRIPTION, desc);
            return this;
        }

        public OAuthErrorResponseBuilder setErrorUri(String state) {
            this.parameters.put(OAuthError.OAUTH_ERROR_URI, state);
            return this;
        }

        public OAuthErrorResponseBuilder setState(String state) {
            this.parameters.put(OAuth.OAUTH_STATE, state);
            return this;
        }

        public OAuthErrorResponseBuilder setRealm(String realm) {
            this.parameters.put(OAuth.WWWAuthHeader.REALM, realm);
            return this;
        }

        public OAuthErrorResponseBuilder location(String location) {
            this.location = location;
            return this;
        }
    }

}
