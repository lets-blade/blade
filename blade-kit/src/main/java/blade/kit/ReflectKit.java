package blade.kit;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import blade.kit.log.Logger;

/**
 * 有关 <code>Reflection</code> 处理的工具类。
 * 
 * <p>
 * 这个类中的每个方法都可以“安全”地处理 <code>null</code> ，而不会抛出 <code>NullPointerException</code>。
 * </p>
 * 
 * @author	<a href="mailto:biezhi.me@gmail.com">biezhi</a>
 * @since	1.0
 */
public abstract class ReflectKit {

	private static final Logger LOGGER = Logger.getLogger(ReflectKit.class);
	
	// ------------------------------------------------------
	/** 新建对象 */
	public static Object newInstance(String className) {
		Object obj = null;
		try {
			Class<?> clazz = Class.forName(className);
			obj = clazz.newInstance();
			LOGGER.debug("new %s", className);
		} catch (Exception e) {
			// quiet
		}
		return obj;
	}
	
	/**
     * 创建一个实例对象
     * @param clazz class对象
     * @return
     */
    public static Object newInstance(Class<?> clazz){
    	try {
    		return clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
    	return null;
    }

	/** 用setter设置bean属性 */
	public static void setProperty(Object bean, String name, Object value) {
		String methodName = "set" + StringKit.firstUpperCase(name);
		invokeMehodByName(bean, methodName, value);
	}

	/** 用getter获取bean属性 */
	public static Object getProperty(Object bean, String name) {
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

	/**
	 * 
	 * @param bean 类实例
	 * @param methodName 方法名称
	 * @param args 方法参数
	 * @return
	 */
	public static Object invokeMehodByName(Object bean, String methodName,
			Object... args) {
		try {
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
		} catch (Exception e) {
			ExceptionKit.makeRuntime(e);
		}
		return null;
	}

	/**
	 * 
	 * @param bean 类实例
	 * @param methodName 方法名称
	 * @param args 方法参数
	 * @return
	 */
	public static Object invokeMehod(Object bean, Method method,
			Object... args) {
		try {
			Class<?>[] types = method.getParameterTypes();

			int argCount = args == null ? 0 : args.length;

			// 参数个数对不上
			ExceptionKit.makeRunTimeWhen(argCount != types.length, "%s in %s", method.getName(), bean);
			
			// 转参数类型
			for (int i = 0; i < argCount; i++) {
				args[i] = cast(args[i], types[i]);
			}

			return method.invoke(bean, args);
		} catch (Exception e) {
			ExceptionKit.makeRuntime(e);
		}
		return null;
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
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL url = loader.getResource(rootPackageName.replace('.', '/'));

			ExceptionKit.makeRunTimeWhen(url == null,
					"package[%s] not found!", rootPackageName);

			String protocol = url.getProtocol();
			if ("file".equals(protocol)) {
				LOGGER.debug("scan in file");
				File[] files = new File(url.toURI()).listFiles();
				for (File f : files) {
					scanPackageClassInFile(rootPackageName, f, classNames);
				}
			} else if ("jar".equals(protocol)) {
				LOGGER.debug("scan in jar");
				JarFile jar = ((JarURLConnection) url.openConnection())
						.getJarFile();
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
     */
    public static Object invokeMethod(Method method, Object target, Object...args) {
        if (method == null) {
            return null;
        }

        method.setAccessible(true);
        try {
            return method.invoke(target, args);
        } catch (Exception ex) {
            throw ExceptionKit.toRuntimeException(ex);
        }

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
     * 
     */
    public static Object invokeMethod(Object object, String methodName, Object[] args, Class<?>...parameterTypes) {
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
     * 
     */
    public static Object invokeStaticMethod(Class<?> clazz, String methodName, Object[] args, Class<?>...parameterTypes) {
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

}