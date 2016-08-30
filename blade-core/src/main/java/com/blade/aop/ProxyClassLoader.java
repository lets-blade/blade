package com.blade.aop;

import java.io.IOException;

import org.objectweb.asm.ClassWriter;

import com.blade.kit.resource.BladeClassLoader;

public class ProxyClassLoader extends BladeClassLoader {

	public ProxyClassLoader() {
	}
	
	@Override
	public Class<?> defineClassByName(String name) throws ClassNotFoundException {
		org.objectweb.asm.ClassReader cr;
		
		if (name.endsWith("Service")) {
			return Class.forName(name);
		}
		try {
			cr = new org.objectweb.asm.ClassReader(name);
			ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
			cr.accept(new LogClassVisitor(cw), org.objectweb.asm.ClassReader.SKIP_DEBUG);
			byte[] data = cw.toByteArray();
			return this.defineClass(name, data, 0, data.length);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
