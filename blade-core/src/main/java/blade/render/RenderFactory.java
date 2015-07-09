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
package blade.render;

/**
 * 渲染引擎工厂
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public final class RenderFactory {
	
	// 默认JSP引擎
	private static Render render = JspRender.single();
	
	private RenderFactory() {
	}
	
	/**
	 * 设置一个渲染对象
	 * @param render	渲染器引擎
	 */
	public static void init(Render render){
		RenderFactory.render = render;
	}
	
	/**
	 * @return	返回渲染器引擎
	 */
	public static Render getRender(){
		return RenderFactory.render;
	}
	
}
