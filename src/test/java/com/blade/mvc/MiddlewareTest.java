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

        middleware.before(signature);
        middleware.after(signature);

        verify(middleware).before(signature);
        verify(middleware).after(signature);
    }

    @Test
    public void testAuthMiddleware() throws Exception {

        BasicAuthMiddleware basicAuthMiddleware = mock(BasicAuthMiddleware.class);

        Signature signature = mock(Signature.class);

        basicAuthMiddleware.before(signature);
        basicAuthMiddleware.after(signature);

        verify(basicAuthMiddleware).before(signature);
        verify(basicAuthMiddleware).after(signature);
    }
}
