/**
 * 
 */
package com.blade.oauth2.issuer;

import java.util.UUID;


/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public abstract class ValueGenerator {
	
    public String generateValue() {
        return generateValue(UUID.randomUUID().toString());
    }

    public abstract String generateValue(String param);
}
