package com.blade.view.parser;

public final class JSONView {

	private JSONView() {
	}
	
	private static JSONParser JSON_PARSER = new DefaultJSONParser();
	
	public static String toJSONString(Object object){
		return JSON_PARSER.toJSONSting(object);
	}
	
	public static void setJSONParser(JSONParser jsonParser){
		JSON_PARSER = jsonParser;
	}
	
}
