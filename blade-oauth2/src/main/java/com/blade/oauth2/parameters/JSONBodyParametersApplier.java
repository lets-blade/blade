/**
 * 
 */

package com.blade.oauth2.parameters;

import java.util.Map;

import com.blade.oauth2.message.OAuthMessage;

import blade.kit.json.JSONHelper;

public class JSONBodyParametersApplier {

    public OAuthMessage applyOAuthParameters(OAuthMessage message, Map<String, Object> params) {
        String json = JSONHelper.mapAsJsonObject(params).toString();
        message.setBody(json);
        return message;
    }

}
