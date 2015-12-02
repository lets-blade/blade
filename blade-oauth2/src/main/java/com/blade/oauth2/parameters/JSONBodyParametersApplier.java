/**
 * 
 */

package com.blade.oauth2.parameters;

import java.util.Map;

import blade.kit.json.Json;

import com.blade.oauth2.message.OAuthMessage;

public class JSONBodyParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {
        String json = Json.parse(params).toString();
        message.setBody(json);
        return message;
    }

}
