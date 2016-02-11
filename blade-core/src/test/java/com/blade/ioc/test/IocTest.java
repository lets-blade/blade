package com.blade.ioc.test;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import com.blade.ioc.Ioc;
import com.blade.ioc.SampleIoc;

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
	
}
