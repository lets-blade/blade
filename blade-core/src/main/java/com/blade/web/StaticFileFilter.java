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
