/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.servlet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;

import com.blade.http.HttpStatus;
import com.blade.http.Response;
import com.blade.render.ModelAndView;
import com.blade.render.Render;

public class ServletResponseTest {

	@Test
	public void testRetrieveStatus() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getStatus()).thenReturn(400);
		
		Response response = new ServletResponse(servletResponse, mock(Render.class));
		Assert.assertEquals(response.status(), 400);
	}

	@Test
	public void testSetStatus() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.status(400);

		verify(servletResponse).setStatus(400);
	}

	@Test
	public void testSetBadRequest() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.badRequest();

		verify(servletResponse).setStatus(HttpStatus.BAD_REQUEST);
	}

	@Test
	public void testSetUnauthorized() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.unauthorized();

		verify(servletResponse).setStatus(HttpStatus.UNAUTHORIZED);
	}

	@Test
	public void testRetrieveContentType() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getContentType()).thenReturn("application/json");

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		Assert.assertEquals(response.contentType(), "application/json");
	}

	@Test
	public void testSetContentType() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.contentType("application/json");

		verify(servletResponse).setContentType("application/json");
	}

	@Test
	public void testRetrieveHeader() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getHeader("Authorization")).thenReturn("Basic");

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		Assert.assertEquals(response.header("Authorization"), "Basic");
	}

	@Test
	public void testSetHeader() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.header("Authorization", "Basic");

		verify(servletResponse).setHeader("Authorization", "Basic");
	}

	@Test
	public void testSetCookie() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.cookie(new Cookie("test-1", "1"));

		verify(servletResponse).addCookie(any(javax.servlet.http.Cookie.class));
	}

	@Test
	public void testRemoveCookie() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.removeCookie(new Cookie("test-1", "1"));

		verify(servletResponse).addCookie(any(javax.servlet.http.Cookie.class));
	}

	@Test
	public void testSetAndGetAttributes() throws Exception {
		Response response = new ServletResponse(mock(HttpServletResponse.class), mock(Render.class));
		response.attribute("test-1", "1");

		Map<String,Object> atts = response.attributes();

		Assert.assertNotNull(atts);
		Assert.assertEquals(atts.size(), 1);
		Assert.assertEquals(atts.get("test-1"), "1");
	}

	@Test
	public void testFailWhenTryingToSetNullAttributeName() throws Exception {
		Response response = new ServletResponse(mock(HttpServletResponse.class), mock(Render.class));
		response.attribute(null, "1");
	}

	@Test
	public void testFailWhenTryingToSetNullAttributeValue() throws Exception {
		Response response = new ServletResponse(mock(HttpServletResponse.class), mock(Render.class));
		response.attribute("test-1", null);
		
	}

	@Test
	public void testWriteOutput() throws Exception {
		PrintWriter writer = mock(PrintWriter.class);
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);
		when(servletResponse.getWriter()).thenReturn(writer);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.text("test");

		verify(writer).print("test");
	}

	@Test
	public void testRenderTemplate() throws Exception {
		Render render = mock(Render.class);

		Response response = new ServletResponse(mock(HttpServletResponse.class), render);
		response.render("template");
		
		verify(render).render( any(ModelAndView.class), any(Writer.class));
	}

	@Test
	public void testRedirect() throws Exception {
		HttpServletResponse servletResponse = mock(HttpServletResponse.class);

		Response response = new ServletResponse(servletResponse, mock(Render.class));
		response.redirect("/");

		verify(servletResponse).sendRedirect("/");
	}
}