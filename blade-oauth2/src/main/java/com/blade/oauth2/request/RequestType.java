/**
 * 
 */
package com.blade.oauth2.request;

/**
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public enum RequestType {

	// link OAuthGrantRequst
	GRANT_REQUEST("grant_request"),
	// link OAuthRequest
	CODE_TOKEN_REQUEST("code_token_request");

    private String parameterStyle;

	RequestType(String parameterStyle) {
        this.parameterStyle = parameterStyle;
    }

    @Override
    public String toString() {
        return parameterStyle;
    }
}
