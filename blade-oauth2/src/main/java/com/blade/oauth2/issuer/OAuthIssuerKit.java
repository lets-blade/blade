/**
 * 
 */

package com.blade.oauth2.issuer;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class OAuthIssuerKit implements OAuthIssuer {

    private ValueGenerator vg;

    public OAuthIssuerKit(ValueGenerator vg) {
        this.vg = vg;
    }

    public String accessToken() {
        return vg.generateValue();
    }

    public String refreshToken() {
        return vg.generateValue();
    }

    public String authorizationCode() {
        return vg.generateValue();
    }
}
