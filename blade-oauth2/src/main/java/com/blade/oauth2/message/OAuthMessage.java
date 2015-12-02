/**
 * 
 */
package com.blade.oauth2.message;

import java.util.Map;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public interface OAuthMessage {

    String getLocationUri();

    void setLocationUri(String uri);

    String getBody();

    void setBody(String body);

    String getHeader(String name);

    void addHeader(String name, String header);

    Map<String, String> getHeaders();

    void setHeaders(Map<String, String> headers);

}
