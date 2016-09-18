package com.blade.test;

import org.junit.Assert;
import org.junit.Test;

import com.blade.Blade;
import com.blade.kit.http.HttpRequest;
import com.blade.mvc.http.Request;
import com.blade.mvc.http.Response;
import com.blade.mvc.route.RouteHandler;

public class BladeTest {

	@Test
	public void testCode200() throws Exception {
		Blade blade = Blade.$();
		blade.get("/", new RouteHandler() {
			@Override
			public void handle(Request request, Response response) {
				
			}
		}).startNoJoin(BladeTest.class);
		try {
			Thread.sleep(100);
			int resCode = HttpRequest.get("http://127.0.0.1:9000").code();
			Assert.assertEquals(resCode, 200);
		} finally {
			blade.embedServer().shutdown();
		}
	}
	
	@Test
	public void testCode404() throws Exception {
		Blade blade = Blade.$();
		blade.startNoJoin(BladeTest.class);
		try {
			Thread.sleep(100);
			int resCode = HttpRequest.get("http://127.0.0.1:9000/hello").code();
			Assert.assertEquals(resCode, 404);
		} finally {
			blade.embedServer().shutdown();
		}
	}
	
	@Test
	public void testCode500() throws Exception {
		Blade blade = Blade.$();
		try {
			blade.get("/:id", new RouteHandler() {
				@Override
				public void handle(Request request, Response response) {
					int id = request.paramAsInt("id");
					System.out.println(id);
				}
			}).startNoJoin(BladeTest.class);
			Thread.sleep(100);
			int resCode = HttpRequest.get("http://127.0.0.1:9000/abc").code();
			Assert.assertEquals(resCode, 500);
		} catch (Exception e) {
		} finally {
			blade.embedServer().shutdown();
		}
	}

}
