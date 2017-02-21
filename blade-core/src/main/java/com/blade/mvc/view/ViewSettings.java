/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.mvc.view;

import com.blade.kit.Assert;
import com.blade.mvc.view.resolve.DefaultJSONParser;
import com.blade.mvc.view.resolve.JSONParser;
import com.blade.mvc.view.template.DefaultEngine;
import com.blade.mvc.view.template.TemplateEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ViewSettings
 *
 * @author    <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
public final class ViewSettings {

	private static final Logger LOGGER = LoggerFactory.getLogger(ViewSettings.class);

	private JSONParser jsonParser = new DefaultJSONParser();
	private TemplateEngine templateEngine = new DefaultEngine();
	private String view404 = "404.html";
	private String view500 = "500.html";

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
		LOGGER.debug("Switch JSONParser With [{}]", jsonParser);
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
	 * @param templateEngine Render engine object
	 * @return return blade
	 */
	public ViewSettings templateEngine(TemplateEngine templateEngine) {
		Assert.notNull(templateEngine);
		LOGGER.debug("Switch TemplateEngine With [{}]", templateEngine);
		this.templateEngine = templateEngine;
		return this;
	}

    /**
	 * @return Return Current TemplateEngine
	 */
	public TemplateEngine templateEngine() {
		return this.templateEngine;
	}

	public String getView404() {
		return view404;
	}

	public ViewSettings setView404(String view404) {
		this.view404 = view404;
		return this;
	}

	public String getView500() {
		return view500;
	}

	public ViewSettings setView500(String view500) {
		this.view500 = view500;
		return this;
	}

}
