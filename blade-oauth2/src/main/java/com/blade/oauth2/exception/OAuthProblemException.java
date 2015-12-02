
package com.blade.oauth2.exception;

import java.util.HashMap;
import java.util.Map;

import blade.kit.StringKit;

public class OAuthProblemException extends IllegalArgumentException {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1536483571040822380L;
	private String error;
    private String description;

    private int responseStatus;

    private Map<String, String> parameters = new HashMap<String, String>();

    protected OAuthProblemException(String error) {
        this(error, "");
    }

    protected OAuthProblemException(String error, String description) {
        super(error + " " + description);
        this.description = description;
        this.error = error;
    }

    public static OAuthProblemException error(String error) {
        return new OAuthProblemException(error);
    }

    public static OAuthProblemException error(String error, String description) {
        return new OAuthProblemException(error, description);
    }

    public OAuthProblemException description(String description) {
        this.description = description;
        return this;
    }

    public OAuthProblemException responseStatus(int responseStatus) {
        this.responseStatus = responseStatus;
        return this;
    }

    public OAuthProblemException setParameter(String name, String value) {
        parameters.put(name, value);
        return this;
    }

    public String getError() {
        return error;
    }

    public String getDescription() {
        return description;
    }

    public int getResponseStatus() {
        return responseStatus == 0 ? 400 : responseStatus;
    }

    public String get(String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public String getMessage() {
        StringBuilder b = new StringBuilder();
        if (StringKit.isNotBlank(error)) {
            b.append(error);
        }

        if (StringKit.isNotBlank(description)) {
            b.append(", ").append(description);
        }
        return b.toString();
    }

    @Override
    public String toString() {
        return "OAuthProblemException{" +
                "error='" + error + '\'' +
                ", description='" + description + '\'' +
                ", responseStatus=" + responseStatus +
                ", parameters=" + parameters +
                '}';
    }
}
