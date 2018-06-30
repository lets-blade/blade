package com.blade.mvc;

import com.blade.BaseTestCase;
import com.blade.mvc.hook.Signature;
import com.blade.mvc.hook.WebHook;
import com.blade.security.web.auth.BasicAuthMiddleware;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Middleware Test
 *
 * @author biezhi
 * 2017/6/5
 */
public class MiddlewareTest extends BaseTestCase {

    @Test
    public void testMiddleware() throws Exception {
        WebHook middleware = mock(WebHook.class);
        Signature signature = mock(Signature.class);

        middleware.before(signature.routeContext());
        middleware.after(signature.routeContext());

        verify(middleware).before(signature.routeContext());
        verify(middleware).after(signature.routeContext());
    }

    @Test
    public void testAuthMiddleware() throws Exception {

        BasicAuthMiddleware basicAuthMiddleware = mock(BasicAuthMiddleware.class);

        Signature signature = mock(Signature.class);

        basicAuthMiddleware.before(signature.routeContext());
        basicAuthMiddleware.after(signature.routeContext());

        verify(basicAuthMiddleware).before(signature.routeContext());
        verify(basicAuthMiddleware).after(signature.routeContext());
    }
}
