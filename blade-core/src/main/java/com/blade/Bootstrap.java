/**
 * Copyright (c) 2015, biezhi 王爵 (biezhi.me@gmail.com)
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
package com.blade;

/**
 * Blade global initialization class, you can do some operations at the start of the application
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class Bootstrap {
	
	/**
	 * Initialization method, do some initialization operation when the application starts
	 * @param blade	blade global object
	 */
	public abstract void init(Blade blade);
	
	/**
	 * After initialization configuration
	 * @param blade	blade global object
	 */
	public void contextInitialized(Blade blade){
		//
	}
}
