package com.blade.aop;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.blade.kit.reflect.ReflectKit;

public class Aop {

	private Set<String> packages = new HashSet<String>();

	public Aop() {
		packages.add("com.javachina.controller.api");
	}
	
	public void addPackage(AopConfig aopConfig){
		packages.addAll(aopConfig.getPackages());
	}
	
	public void addPackage(String packageName){
		packages.add(packageName);
	}
	
	private Map<String, Object> maps = new HashMap<String, Object>();
	
	public Object createBean(Class<?> clazz) {
		try {
			String clsasName = clazz.getName();
			if(null == maps.get(clsasName)){
				String packageName = clazz.getPackage().getName();
				if (packages.contains(packageName)) {
					ClassReader cr = new ClassReader(clazz.getName());
					ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
					cr.accept(new LogClassVisitor(cw), ClassReader.SKIP_DEBUG);
					Object value = new MyClassLoader().defineClassForName(clazz.getName(), cw.toByteArray()).newInstance();
					maps.put(clsasName, value);
					return value;
				}
				Object value = ReflectKit.newInstance(clazz);
				maps.put(clsasName, value);
				return value;
			}
			return maps.get(clsasName);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
