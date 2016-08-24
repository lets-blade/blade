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
package com.blade.kit;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
  
/**
 * 类克隆工具 
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
@SuppressWarnings("unchecked")
public class CloneKit {  
	/** 
	 * 无需进行复制的特殊类型数组 
	 */  
	static Class<?>[] needlessCloneClasses = new Class[]{String.class,Boolean.class,Character.class,Byte.class,Short.class,  
		Integer.class,Long.class,Float.class,Double.class,Void.class,Object.class,Class.class  
	};  
	/** 
	 * 判断该类型对象是否无需复制 
	 * @param c 指定类型 
	 * @return 如果不需要复制则返回真，否则返回假 
	 */  
	private static boolean isNeedlessClone(Class<?> c){  
		if(c.isPrimitive()){//基本类型  
			return true;  
		}  
		for(Class<?> tmp:needlessCloneClasses){//是否在无需复制类型数组里  
			if(c.equals(tmp)){  
				return true;  
			}  
		}  
		return false;  
	}  
	  
	/** 
	 * 尝试创建新对象 
	 * @param c 原始对象 
	 * @return 新的对象 
	 * @throws IllegalAccessException 
	 */  
	private static Object createObject(Object value) throws IllegalAccessException{  
			try {  
				return value.getClass().newInstance();  
			} catch (InstantiationException e) {  
				return null;  
			} catch (IllegalAccessException e) {  
				throw e;  
			}  
	}  
	  
	/** 
	 * 复制对象数据 
	 * @param value 原始对象 
	 * @param level 复制深度。小于0为无限深度，即将深入到最基本类型和Object类级别的数据复制； 
	 * 大于0则按照其值复制到指定深度的数据，等于0则直接返回对象本身而不进行任何复制行为。 
	 * @return 返回复制后的对象 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 */  
	public static Object clone(Object value,int level) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{  
		if(value==null){  
			return null;  
		}  
		if(level==0){  
			return value;  
		}  
		Class<?> c = value.getClass();  
		if(isNeedlessClone(c)){  
			return value;  
		}  
		level--;  
		if(value instanceof Collection){//复制新的集合  
			Collection<Object> tmp = (Collection<Object>)c.newInstance();  
			for(Object v : (Collection<Object>)value){  
				tmp.add(clone(v,level));//深度复制  
			}  
			value = tmp;  
		}  
		else if(c.isArray()){//复制新的Array  
			//首先判断是否为基本数据类型  
			if(c.equals(int[].class)){  
				int[] old = (int[])value;  
				value = (int[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(short[].class)){  
				short[] old = (short[])value;  
				value = (short[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(char[].class)){  
				char[] old = (char[])value;  
				value = (char[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(float[].class)){  
				float[] old = (float[])value;  
				value = (float[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(double[].class)){  
				double[] old = (double[])value;  
				value = (double[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(long[].class)){  
				long[] old = (long[])value;  
				value = (long[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(boolean[].class)){  
				boolean[] old = (boolean[])value;  
				value = (boolean[])Arrays.copyOf(old, old.length);  
			} else if(c.equals(byte[].class)){  
				byte[] old = (byte[])value;  
				value = (byte[])Arrays.copyOf(old, old.length);  
			} else {  
				Object[] old = (Object[])value;  
				Object[] tmp = (Object[])Arrays.copyOf(old, old.length, old.getClass());  
				for(int i = 0;i<old.length;i++){  
					tmp[i] = clone(old[i],level);  
				}  
				value = tmp;  
			}  
		} else if(value instanceof Map){//复制新的MAP  
			Map<Object, Object> tmp = (Map<Object, Object>) c.newInstance();  
			Map<Object, Object> org = (Map<Object, Object>)value;  
			for(Object key:org.keySet()){  
				tmp.put(key, clone(org.get(key),level));//深度复制  
			}  
			value = tmp;  
		} else {  
			Object tmp = createObject(value);  
			if(tmp==null){//无法创建新实例则返回对象本身，没有克隆  
				return value;  
			}  
			Set<Field> fields = new HashSet<Field>();  
			while(c!=null&&!c.equals(Object.class)){  
				fields.addAll(Arrays.asList(c.getDeclaredFields()));  
				c = c.getSuperclass();  
			}  
			for(Field field:fields){  
				if(!Modifier.isFinal(field.getModifiers())){//仅复制非final字段  
					field.setAccessible(true);  
					field.set(tmp, clone(field.get(value),level));//深度复制  
				}  
			}  
			value = tmp;  
		}  
		return value;  
	}  
	  
	/** 
	 * 浅表复制对象 
	 * @param value 原始对象 
	 * @return 复制后的对象，只复制一层 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 */  
	public static Object clone(Object value) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{  
		return clone(value,1);  
	}  
	  
	/** 
	 * 深度复制对象 
	 * @param value 原始对象 
	 * @return 复制后的对象 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws NoSuchMethodException 
	 */  
	public static Object deepClone(Object value) throws IllegalAccessException, InstantiationException, InvocationTargetException, NoSuchMethodException{  
		return clone(value,-1);  
	}
}