/**
 * 
 */

package com.blade.oauth2.issuer;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public interface OAuthIssuer {
	
    public String accessToken();

    public String authorizationCode();

    public String refreshToken();
}
