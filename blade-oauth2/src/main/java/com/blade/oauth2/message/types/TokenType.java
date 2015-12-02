/**
 * 
 */
package com.blade.oauth2.message.types;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public enum TokenType {
	
    BEARER("Bearer"),
    MAC("MAC");

    private String tokenType;

    TokenType(String grantType) {
        this.tokenType = grantType;
    }

    @Override
    public String toString() {
        return tokenType;
    }
}
