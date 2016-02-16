package com.blade.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.blade.Const;

public class BladeBanner {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(BladeBanner.class);
	
	private static final String[] banner = {
			" __, _,   _, __, __,",
			" |_) |   /_\\ | \\ |_",
			" |_) | , | | |_/ |",
			" ~   ~~~ ~ ~ ~   ~~~"
			};
	
	public void print() {
		for (String s : banner) {
			LOGGER.info('\t' + s);
		}
		LOGGER.info("\t :: Blade :: (v" + Const.BLADE_VERSION + ")\r\n");
	}
}
