/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade.view;

import com.blade.kit.Assert;
import com.blade.view.parser.DefaultJSONParser;
import com.blade.view.parser.JSONParser;
import com.blade.view.template.DefaultEngine;
import com.blade.view.template.TemplateEngine;

/**
 * ViewSettings
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.6.6
 */
public final class ViewSettings {
	
	private JSONParser jsonParser = new DefaultJSONParser();
	private TemplateEngine templateEngine = new DefaultEngine();

	private ViewSettings() {
	}

	static final class ViewSettingsHolder {
		private static final ViewSettings $ = new ViewSettings();
	}

	public static ViewSettings $() {
		return ViewSettingsHolder.$;
	}

	public ViewSettings JSONParser(JSONParser jsonParser) {
		Assert.notNull(jsonParser);
		this.jsonParser = jsonParser;
		return this;
	}

	public JSONParser JSONParser() {
		return this.jsonParser;
	}

	public String toJSONString(Object object) {
		return jsonParser.toJSONSting(object);
	}

	/**
	 * Setting Render Engin, Default is static file render
	 * 
	 * @param templateEngine
	 *            Render engine object
	 * @return return blade
	 */
	public ViewSettings templateEngine(TemplateEngine templateEngine) {
		Assert.notNull(templateEngine);
		this.templateEngine = templateEngine;
		return this;
	}

	/**
	 * @return Return Current TemplateEngine
	 */
	public TemplateEngine templateEngine() {
		return this.templateEngine;
	}

}
