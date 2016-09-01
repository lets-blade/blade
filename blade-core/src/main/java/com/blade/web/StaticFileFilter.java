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
package com.blade.web;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StaticFileFilter {

	private Set<String> prefixList;
	private Map<String, Boolean> cache;
	
	public StaticFileFilter(Set<String> prefixList) {
		this.prefixList = prefixList;
		this.cache = new HashMap<String, Boolean>(128);
	}
	
	public boolean isStatic(String path){
		if (cache != null) {
            Boolean found = cache.get(path);
            if (found != null) {
                return found == Boolean.TRUE;
            }
        }
		
		for(String prefix : prefixList){
			if(path.startsWith(prefix)){
				if (cache != null) {
                    cache.put(path, Boolean.TRUE);
                }
				return true;
			}
		}
		
		if (cache != null) {
            cache.put(path, Boolean.FALSE);
        }
		
		return false;
	}
	
}
