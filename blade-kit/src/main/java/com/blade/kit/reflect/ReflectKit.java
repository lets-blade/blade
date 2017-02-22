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
package com.blade.kit.reflect;

import com.blade.kit.Emptys;
import com.blade.kit.ExceptionKit;
import com.blade.kit.StringKit;
import com.blade.kit.SystemKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 有关 Reflection处理的工具类。
 * 
 * 这个类中的每个方法都可以“安全”地处理 <code>null</code> ，而不会抛出 <code>NullPointerException</code>。
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com">biezhi</a>
 * @since	1.0
 */
public abstract class ReflectKit {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReflectKit.class);
	
	/** 新建对象 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ClassNotFoundException */
	public static Object newInstance(String className) {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(className);
			obj = clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		LOGGER.debug("New {}", className);
		return obj;
	}
	
	/**
     * 创建一个实例对象
     * @param clazz class对象
     * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
     */
	public static Object newInstance(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
     * 根据类名获取Class对象
     * 
     * @param className	类名称
     * @return			返回Class对象
     */
    public static Class<?> newClass(String className){
    	try {
    		return Class.forName(className);
		} catch (ClassNotFoundException e) {
		}
    	return null;
    }
    
    /**
     * 获取包是否存在
     * 
     * @param packageName	包名称
     * @return				返回包是否存在
     */
    public static boolean isPackage(String packageName){
    	if(StringKit.isNotBlank(packageName)){
    		Package temp = Package.getPackage(packageName);
    		return null != temp;
    	}
    	return false;
    }
    
    public static boolean isClass(String className){
    	if(StringKit.isNotBlank(className)){
    		try {
				Class.forName(className);
				return true;
			} catch (ClassNotFoundException e) {
			}
    	}
    	return false;
    }
    
    
    /**
     * 创建一个实例对象
     * @param clazz class对象
     * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
     */
    public static <T> T newBean(Class<T> clazz) {
    	try {
			Object object = clazz.newInstance();
			return clazz.cast(object);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	return null;
    }

	/** 用setter设置bean属性 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException */
	public static void setProperty(Object bean, String name, Object value) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String methodName = "set" + StringKit.firstUpperCase(name);
		invokeMehodByName(bean, methodName, value);
	}

	/** 用getter获取bean属性 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException */
	public static Object getProperty(Object bean, String name) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		String methodName = "get" + StringKit.firstUpperCase(name);
		return invokeMehodByName(bean, methodName);
	}

	/** 类型转换 */
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object value, Class<T> type) {
		if (value != null && !type.isAssignableFrom(value.getClass())) {
			if (is(type, int.class, Integer.class)) {
				value = Integer.parseInt(String.valueOf(value));
			} else if (is(type, long.class, Long.class)) {
				value = Long.parseLong(String.valueOf(value));
			} else if (is(type, float.class, Float.class)) {
				value = Float.parseFloat(String.valueOf(value));
			} else if (is(type, double.class, Double.class)) {
				value = Double.parseDouble(String.valueOf(value));
			} else if (is(type, boolean.class, Boolean.class)) {
				value = Boolean.parseBoolean(String.valueOf(value));
			} else if (is(type, String.class)) {
				value = String.valueOf(value);
			}
		}
		return (T) value;
	}

	/** 查找方法 */
	public static Method getMethodByName(Object classOrBean, String methodName) {
		Method ret = null;
		if (classOrBean != null) {
			Class<?> clazz = null;
			if (classOrBean instanceof Class<?>) {
				clazz = (Class<?>) classOrBean;
			} else {
				clazz = classOrBean.getClass();
			}
			for (Method method : clazz.getMethods()) {
				if (method.getName().equals(methodName)) {
					ret = method;
					break;
				}
			}
		}
		return ret;
	}
	
	public static Method getMethodByName(Class<?> clazz, String methodName) {
		Method ret = null;
		for (Method method : clazz.getMethods()) {
			if (method.getName().equals(methodName)) {
				ret = method;
				break;
			}
		}
		return ret;
	}
	
	/*private static boolean sameType(Type[] types, Class<?>[] clazzes) {
		if (types.length != clazzes.length) {
			return false;
		}
		for (int i = 0; i < types.length; i++) {
			if (!Type.getType(clazzes[i]).equals(types[i])) {
				return false;
			}
		}
		return true;
	}
	
	public static String[] getMethodParamsNames(final Method m) {
		try {
			final String[] paramNames = new String[m.getParameterTypes().length];
			Class<?> declaringClass = m.getDeclaringClass();
			String className = declaringClass.getName();
			int lastDotIndex = className.lastIndexOf(".");
			InputStream is = declaringClass.getResourceAsStream(className.substring(lastDotIndex + 1) + ".class");
			ClassReader cr = new ClassReader(is);
			cr.accept(new ClassVisitor(Opcodes.ASM4) {
				@Override
				public MethodVisitor visitMethod(final int access, final String name, final String desc,
						final String signature, final String[] exceptions) {

					final Type[] args = Type.getArgumentTypes(desc);
					// 方法名相同并且参数个数相同
					if (!name.equals(m.getName()) || !sameType(args, m.getParameterTypes())) {
						return super.visitMethod(access, name, desc, signature, exceptions);
					}
					MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
					return new MethodVisitor(Opcodes.ASM4, v) {
						@Override
						public void visitLocalVariable(String name, String desc, String signature, Label start,
								Label end, int index) {
							int i = index - 1;
							// 如果是静态方法，则第一就是参数
							// 如果不是静态方法，则第一个是"this"，然后才是方法的参数
							if (Modifier.isStatic(m.getModifiers())) {
								i = index;
							}
							if (i >= 0 && i < paramNames.length) {
								paramNames[i] = name;
							}
							super.visitLocalVariable(name, desc, signature, start, end, index);
						}
					};
				}
			}, 0);
			return paramNames;
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}*/
	
	/**
	 * 
	 * @param bean 类实例
	 * @param methodName 方法名称
	 * @param args 方法参数
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static Object invokeMehodByName(Object bean, String methodName, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method method = getMethodByName(bean, methodName);
		Class<?>[] types = method.getParameterTypes();

		int argCount = args == null ? 0 : args.length;

		// 参数个数对不上
		ExceptionKit.makeRunTimeWhen(argCount != types.length,
				"%s in %s", methodName, bean);

		// 转参数类型
		for (int i = 0; i < argCount; i++) {
			args[i] = cast(args[i], types[i]);
		}
		return method.invoke(bean, args);
	}

	/**
	 * 
	 * @param bean 类实例
	 * @param method 方法名称
	 * @param args 方法参数
	 * @return
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	public static Object invokeMehod(Object bean, Method method, Object... args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Class<?>[] types = method.getParameterTypes();

		int argCount = args == null ? 0 : args.length;

		// 参数个数对不上
		ExceptionKit.makeRunTimeWhen(argCount != types.length, "%s in %s", method.getName(), bean);
		
		// 转参数类型
		for (int i = 0; i < argCount; i++) {
			args[i] = cast(args[i], types[i]);
		}
		return method.invoke(bean, args);
	}
	
	// ------------------------------------------------------

	/** 对象是否其中一个 */
	public static boolean is(Object obj, Object... mybe) {
		if (obj != null && mybe != null) {
			for (Object mb : mybe)
				if (obj.equals(mb))
					return true;
		}
		return false;
	}

	public static boolean isNot(Object obj, Object... mybe) {
		return !is(obj, mybe);
	}
	
	// ------------------------------------------------------

	/** 扫描包下面所有的类 */
	public static List<String> scanPackageClass(String rootPackageName) {
		List<String> classNames = new ArrayList<String>();
		try {
			ClassLoader loader = ReflectKit.class.getClassLoader();
			URL url = loader.getResource(rootPackageName.replace('.', '/'));

			ExceptionKit.makeRunTimeWhen(url == null, "package[%s] not found!", rootPackageName);

			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				LOGGER.debug("Scan in file ...");
				File[] files = new File(url.toURI()).listFiles();
				for (File f : files) {
					scanPackageClassInFile(rootPackageName, f, classNames);
				}
			} else if ("jar".equals(protocol)) {
				LOGGER.debug("Scan in jar ...");
				JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
				scanPackageClassInJar(jar, rootPackageName, classNames);
			}

		} catch (URISyntaxException e) {
		} catch (IOException e) {
		}
		return classNames;
	}

	/** 扫描文件夹下所有class文件 */
	private static void scanPackageClassInFile(String rootPackageName,
			File rootFile, List<String> classNames) {
		String absFileName = rootPackageName + "." + rootFile.getName();
		if (rootFile.isFile() && absFileName.endsWith(".class")) {
			classNames.add(absFileName.substring(0, absFileName.length() - 6));
		} else if (rootFile.isDirectory()) {
			String tmPackageName = rootPackageName + "." + rootFile.getName();
			for (File f : rootFile.listFiles()) {
				scanPackageClassInFile(tmPackageName, f, classNames);
			}
		}
	}

	/**
	 * 扫描jar里面的类
	 * @param jar jar包文件
	 * @param packageDirName 包目录 
	 * @param classNames class名称列表
	 */
	private static void scanPackageClassInJar(JarFile jar, String packageDirName, List<String> classNames) {
		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements()) {
			JarEntry entry = entries.nextElement();
			String name = entry.getName().replace('/', '.');
			if (name.startsWith(packageDirName) && name.endsWith(".class")) {
				classNames.add(name.substring(0, name.length() - 6));
			}
		}
	}
		

    /**
     * 方法调用，如果<code>clazz</code>为<code>null</code>，返回<code>null</code>；
     * <p>
     * 如果<code>method</code>为<code>null</code>，返回<code>null</code>
     * <p>
     * 如果<code>target</code>为<code>null</code>，则为静态方法
     * 
     * @param method 调用的方法
     * @param target 目标对象
     * @param args 方法的参数值
     * @return 调用结果
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     */
    public static Object invokeMethod(Method method, Object target, Object...args) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (method == null) {
            return null;
        }
        method.setAccessible(true);
        return method.invoke(target, args);
    }

    /**
     * <p>
     * 调用一个命名的方法，其参数类型相匹配的对象类型。
     * </p>
     * 
     * 
     * @param object 调用方法作用的对象
     * @param methodName 方法名
     * @param args 参数值
     * @param parameterTypes 参数类型
     * @return 调用的方法的返回值
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * 
     */
    public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>...parameterTypes) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (object == null || StringKit.isEmpty(methodName)) {
            return null;
        }

        if (parameterTypes == null) {
            parameterTypes = Emptys.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = Emptys.EMPTY_OBJECT_ARRAY;
        }
        Method method;
        try {
            method = object.getClass().getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            throw ExceptionKit.toRuntimeException(ex);
        }
        if (method == null) {
            return null;
        }
        return invokeMethod(method, object, args);
    }

    /**
     * <p>
     * 调用一个命名的静态方法，其参数类型相匹配的对象类型。
     * </p>
     * 
     * 
     * @param clazz 调用方法作用的类
     * @param methodName 方法名
     * @param args 参数值
     * @param parameterTypes 参数类型
     * @return 调用的方法的返回值
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * 
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object[] args, Class<?>...parameterTypes) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (parameterTypes == null) {
            parameterTypes = Emptys.EMPTY_CLASS_ARRAY;
        }
        if (args == null) {
            args = Emptys.EMPTY_OBJECT_ARRAY;
        }
        Method method;
        try {
            method = clazz.getDeclaredMethod(methodName, parameterTypes);
        } catch (Exception ex) {
            throw ExceptionKit.toRuntimeException(ex);
        }
        if (method == null) {
            return null;
        }
        return invokeMethod(method, null, args);
    }

    // ==========================================================================
    // 辅助方法。
    // ==========================================================================

    private static final Method IS_SYNTHETIC;
    static {
        Method isSynthetic = null;
        if (SystemKit.getJavaInfo().isJavaVersionAtLeast(1.5f)) {
            // cannot call synthetic methods:
            try {
                isSynthetic = Member.class.getMethod("isSynthetic", Emptys.EMPTY_CLASS_ARRAY);
            } catch (Exception e) {
                // ignore
            }
        }
        IS_SYNTHETIC = isSynthetic;
    }

    public static boolean isAccessible(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers()) && !isSynthetic(m);
    }

    static boolean isSynthetic(Member m) {
        if (IS_SYNTHETIC != null) {
            try {
                return ((Boolean) IS_SYNTHETIC.invoke(m, (Object[]) null)).booleanValue();
            } catch (Exception e) {
            }
        }
        return false;
    }
    
    /**
     * Check whether the {@link Class} identified by the supplied name is present.
     *
     * @param className the name of the class to check
     * @return true if class is present, false otherwise
     */
    public static boolean isPresent(String className) {
        try {
            // what's wrong with old plain Class.forName
            // this code supposed to work everywhere including containers
            Class.forName(className);
            // getClassLoader().loadClass(className);
            return true;
        }
        catch (Throwable ex) {
            return false;
        }
    }

    public static boolean isPublic(Member m) {
        return m != null && Modifier.isPublic(m.getModifiers());
    }

    public static void forceAccess(AccessibleObject object) {
        if (object == null || object.isAccessible()) {
            return;
        }
        try {
            object.setAccessible(true);
        } catch (SecurityException e) {
            throw ExceptionKit.toRuntimeException(e);
        }
    }

	public static boolean hasInterface(Class<?> type, Class<?> interfaceType) {
		if(null != type && null != interfaceType){
			Class<?>[] interfaces = type.getInterfaces();
			if(null != interfaces && interfaces.length > 0){
				for(Class<?> inte : interfaces){
					if(inte == interfaceType){
						return true;
					}
				}
			}
		}
		return false;
	}

}