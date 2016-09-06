package com.blade.config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.blade.kit.StringKit;

public class Packages {
	
	private Map<String, Set<String>> pool = new HashMap<String, Set<String>>(8);
	
	public Packages() {
	}
	
	public void put(String name, String pkg) {
		if(null != name && StringKit.isNotBlank(pkg)){
			Set<String> values = new HashSet<String>(1);
			values.add(pkg);
			pool.put(name, values);
		}
	}
	
	public void add(String name, String...pkgs) {
		if(null != name && null != pkgs && pkgs.length > 0){
			Set<String> values = pool.get(name);
			if(null == values){
				values = new HashSet<String>(pkgs.length);
			} else {
				pool.remove(name);
			}
			values.addAll(Arrays.asList(pkgs));
			pool.put(name, values);
		}
	}
	
	public String[] array(String name){
		Set<String> values = pool.get(name);
		if(null != values){
			return values.toArray(new String[values.size()]);
		}
		return null;
	}
	
	public Set<String> values(String name){
		return pool.get(name);
	}
	
	public String first(String name){
		Set<String> values = pool.get(name);
		if(null != values && !values.isEmpty()){
			return values.iterator().next();
		}
		return null;
	}
	
}
