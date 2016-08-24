package com.blade.kit.reflect;

import java.util.HashSet;
import java.util.Set;

public final class ConvertKit {

	private static final Set<Class<?>> basicTypes;
	
    static {
    	basicTypes = new HashSet<Class<?>>();
    	basicTypes.add(Boolean.class);
    	basicTypes.add(boolean.class);
    	basicTypes.add(Integer.class);
    	basicTypes.add(int.class);
    	basicTypes.add(Short.class);
    	basicTypes.add(short.class);
    	basicTypes.add(Long.class);
    	basicTypes.add(long.class);
    	basicTypes.add(Float.class);
    	basicTypes.add(float.class);
    	basicTypes.add(Double.class);
    	basicTypes.add(double.class);
    	basicTypes.add(Character.class);
    	basicTypes.add(char.class);
    }
    
	public static boolean isBasicType(Class<?> type){
		return basicTypes.contains(type);
	}
	
}
