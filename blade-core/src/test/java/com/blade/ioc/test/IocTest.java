package com.blade.ioc.test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.blade.ioc.Ioc;
import com.blade.ioc.SampleIoc;
import com.blade.ioc.loader.IocAnnotationLoader;
import com.blade.ioc.service.test.UserService;
import com.blade.ioc.service.test.UserServiceImpl;

public class IocTest {
	
	private Ioc ioc;
	
	@Before
	public void before(){
		ioc = new SampleIoc();
	}
	
	@Test
	public void testAddBean() {
		ioc.addBean(Hello.class);
		ioc.addBean(User.class);
		Hello hello = ioc.getBean(Hello.class);
		assertNotNull(hello);
		hello.says();
	}
	
	@Test
	public void testGetInterface() {
		ioc.load(new IocAnnotationLoader("com.blade.ioc.service.test"));
		
		UserService userService = ioc.getBean(UserService.class);
		assertNotNull(userService);
		assertEquals("blade", userService.getName());
		
		UserServiceImpl userServiceImpl = ioc.getBean(UserServiceImpl.class);
		
		assertEquals(userService, userServiceImpl);
	}
	
	@Test
	public void testGetInterface2() {
		
		ioc.addBean(UserServiceImpl.class);
		
		UserService userService = ioc.getBean(UserService.class);
		assertNotNull(userService);
		assertEquals("blade", userService.getName());
		
		UserServiceImpl userServiceImpl = ioc.getBean(UserServiceImpl.class);
		
		assertEquals(userService, userServiceImpl);
	}
	
	@Test
	public void testGetInterface3() {
		
		ioc.addBean(new UserServiceImpl());
		
		UserService userService = ioc.getBean(UserService.class);
		assertNotNull(userService);
		assertEquals("blade", userService.getName());
		
		UserServiceImpl userServiceImpl = ioc.getBean(UserServiceImpl.class);
		
		assertEquals(userService, userServiceImpl);
	}
	
}
