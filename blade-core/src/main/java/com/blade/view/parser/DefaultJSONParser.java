package com.blade.view.parser;

import com.blade.kit.json.JSONKit;

public class DefaultJSONParser implements JSONParser {
	
	@Override
	public String toJSONSting(Object object) {
		return JSONKit.toJSONString(object);
	}

}
