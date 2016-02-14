package com.blade.web;

import java.io.PrintStream;

import com.blade.Const;

public class BladeBanner {
	
	private static final String[] banner = {
			" __, _,   _, __, __,",
			" |_) |   /_\\ | \\ |_",
			" |_) | , | | |_/ |",
			" ~   ~~~ ~ ~ ~   ~~~"
			};
	
	void print(PrintStream printStream){
		for (String s : banner) {
			printStream.println('\t' + s);
		}
		printStream.println("\t :: Blade :: (v" + Const.BLADE_VERSION + ")");
	}
}
