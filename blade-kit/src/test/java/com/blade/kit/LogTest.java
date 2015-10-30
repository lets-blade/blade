package com.blade.kit;
import blade.kit.log.Logger;


public class LogTest {

	private static final Logger LOGGER = Logger.getLogger(LogTest.class);
	
	public static void main(String[] args) {
//		LOGGER.setLevel(Level.INFO);
		LOGGER.debug("debug hello");
		LOGGER.info("info hello %s", "aaa");
		LOGGER.warn("warn hello");
		
		System.out.println(LOGGER.isDebugEnabled());
		System.out.println(LOGGER.isInfoEnabled());
		System.out.println(LOGGER.isErrorEnabled());
	}
}
