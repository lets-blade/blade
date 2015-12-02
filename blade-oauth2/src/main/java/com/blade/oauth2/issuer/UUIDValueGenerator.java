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
public class UUIDValueGenerator extends ValueGenerator {

    @Override
    public String generateValue(String param) {
        return UUID.fromString(UUID.nameUUIDFromBytes(param.getBytes()).toString()).toString();
    }
}
