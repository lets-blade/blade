package com.hellokaton.blade.mvc.handler;

import com.hellokaton.blade.Blade;
import com.hellokaton.blade.exception.BladeException;
import com.hellokaton.blade.exception.NotFoundException;
import com.hellokaton.blade.mvc.WebContext;
import com.hellokaton.blade.mvc.http.Request;
import com.hellokaton.blade.mvc.http.Response;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author biezhi
 * @date 2017/9/19
 */
public class ExceptionHandlerTest {

    private Request  request;
    private Response response;

    @Before
    public void before() {
        request = mock(Request.class);
        when(request.header("Accept")).thenReturn("text/html");
        response = mock(Response.class);

        WebContext.init(Blade.of(), "/");
        WebContext.set(new WebContext(request, response, null));
    }

    @Test
    public void testInternalErrorException() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler();
        try {
            throw new Exception();
        } catch (Exception e) {
            handler.handle(e);
        }
        verify(response).status(500);
        verify(response).html(any(String.class));
    }

    @Test
    public void testNotFoundException() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler();
        try {
            throw new NotFoundException("/hello");
        } catch (BladeException e) {
            handler.handle(e);
        }
        verify(response).status(404);
        verify(response).html(any(String.class));
    }

    @Test
    public void testNotWriteBodyIfNotHtmlRequest() throws Exception {
        DefaultExceptionHandler handler = new DefaultExceptionHandler();
        try {
            throw new NotFoundException("/hello");
        } catch (BladeException e) {
            handler.handle(e);
        }
        verify(response).status(404);
        verify(response).html(any(String.class));
    }

}
