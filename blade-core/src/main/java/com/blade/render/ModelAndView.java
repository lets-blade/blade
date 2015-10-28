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
package com.blade.render;

import java.util.Map;

import blade.kit.CollectionKit;

/**
 * ModelAndView使用模型和视图来渲染
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ModelAndView {
	
	/**
	 * 数据对象，该对象被放在httprequest的attribute中
	 */
	private Map<String, Object> model;
    
	/**
	 * 视图
	 */
    private String view;

    /**
     * 创建一个空视图
     * 
     * @param view		视图
     */
    public ModelAndView(String view) {
        super();
        this.model = CollectionKit.newHashMap();
        this.view = view;
    }
    
    /**
     * 创建一个带数据的模型视图对象
     * 
     * @param model		数据模型
     * @param view		视图
     */
    public ModelAndView(Map<String, Object> model, String view) {
        super();
        this.model = model;
        this.view = view;
    }

    /**
     * 添加数据
     * 
     * @param key		数据键
     * @param value 	数据值
     */
    public void add(String key, Object value){
    	this.model.put(key, value);
    }
    
    /**
     * 移除一个数据
     * 
     * @param key 		模型的键
     */
    public void remove(String key){
    	this.model.remove(key);
    }
    
    /**
     * 
     * @return 获取视图
     */
	public String getView() {
		return view;
	}
	
	/**
	 * 设置视图
	 * @param view
	 */
	public void setView(String view) {
		this.view = view;
	}

	/**
	 * @return 获取模型
	 */
	public Map<String, Object> getModel() {
		return model;
	}
	
	/**
	 * 设置model
	 * @param model
	 */
	public void setModel(Map<String, Object> model) {
		this.model = model;
	}
	
	@Override
	public String toString() {
		return "view = " + view + ", model = " + model;
	}
}