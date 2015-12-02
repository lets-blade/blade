/**
 * 
 */
package com.blade.oauth2;

import blade.kit.ReflectKit;
import blade.kit.StringKit;

import com.blade.http.Request;
import com.blade.oauth2.base.request.OAuthBaseRequest;
import com.blade.oauth2.base.validator.OAuthValidator;
import com.blade.oauth2.exception.OAuthProblemException;
import com.blade.oauth2.kit.OAuthKit;
import com.blade.oauth2.message.types.ResponseType;
import com.blade.oauth2.validator.AuthorizationValidator;
import com.blade.oauth2.validator.ImplicitCodeValidator;

/**
 * 
 * @author BruceZCQ [zcq@zhucongqi.cn]
 * @version
 */
public class OAuthRequest extends OAuthBaseRequest {
	
	public OAuthRequest(Request request)
			throws OAuthProblemException {
		super(request);
	}

	@Override
	protected OAuthValidator<Request> initValidator() throws OAuthProblemException {
		// end user authorization validators
		validators.put(ResponseType.CODE.toString(), AuthorizationValidator.class);
		validators.put(ResponseType.TOKEN.toString(), ImplicitCodeValidator.class);
		final String requestTypeValue = getParam(OAuth.OAUTH_RESPONSE_TYPE);
		if (StringKit.isBlank(requestTypeValue)) {
			throw OAuthKit.handleOAuthProblemException("Missing response_type parameter value");
		}
		final Class<? extends OAuthValidator<Request>> clazz = validators
				.get(requestTypeValue);
		if (clazz == null) {
			throw OAuthKit.handleOAuthProblemException("Invalid response_type parameter value");
		}
		return (OAuthValidator<Request>) ReflectKit.newInstance(clazz);
	}

	public String getResponseType() {
		return getParam(OAuth.OAUTH_RESPONSE_TYPE);
	}

}
