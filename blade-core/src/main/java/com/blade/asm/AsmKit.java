package com.blade.asm;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class AsmKit {

	/**
	 * 
	 * <p>
	 * 比较参数类型是否一致
	 * </p>
	 * 
	 * @param types
	 *            asm的类型({@link Type})
	 * @param clazzes
	 *            java 类型({@link Class})
	 * @return
	 */
	private static boolean sameType(Type[] types, Class<?>[] clazzes) {
		// 个数不同
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

	/**
	 * 
	 * <p>
	 * 获取方法的参数名
	 * </p>
	 * 
	 * @param m
	 * @return
	 */
	public static String[] getMethodParamNames(final Method m) {
		final String[] paramNames = new String[m.getParameterTypes().length];
		final String n = m.getDeclaringClass().getName();
		ClassReader cr = null;
		try {
			cr = new ClassReader(n);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
					public void visitLocalVariable(String name, String desc, String signature, Label start, Label end,
							int index) {
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
	}

}
