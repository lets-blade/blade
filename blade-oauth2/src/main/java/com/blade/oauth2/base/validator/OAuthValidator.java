/**
 */
package com.blade.oauth2.base.validator;

import com.blade.oauth2.exception.OAuthProblemException;
import com.blade.web.http.Request;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 * @param <T>
 */
public interface OAuthValidator<T extends Request> {

    public void validateMethod(T request) throws OAuthProblemException;

    public void validateContentType(T request) throws OAuthProblemException;

    public void validateRequiredParameters(T request) throws OAuthProblemException;

    public void validateOptionalParameters(T request) throws OAuthProblemException;

    public void validateNotAllowedParameters(T request) throws OAuthProblemException;

    public void validateClientAuthenticationCredentials(T request) throws OAuthProblemException;

    public void performAllValidations(T request) throws OAuthProblemException;

}
