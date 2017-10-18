/**
 * Copyright (c) 2016, biezhi 王爵 (biezhi.me@gmail.com)
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.blade.kit;

import org.objectweb.asm.*;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ASM Tools
 *
 * @author <a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since 1.6.6
 */
public final class AsmKit {

    /**
     * Cached method names
     */
    private static final Map<Method, String[]> METHOD_NAMES_POOL = new ConcurrentHashMap<>(64);

    /**
     * Compare whether the parameter type is consistent
     *
     * @param types   the type of the asm({@link Type})
     * @param classes java type({@link Class})
     * @return return param type equals
     */
    private static boolean sameType(Type[] types, Class<?>[] classes) {
        if (types.length != classes.length) return false;
        for (int i = 0; i < types.length; i++) {
            if (!Type.getType(classes[i]).equals(types[i])) return false;
        }
        return true;
    }

    /**
     * get method param names
     *
     * @param m method
     * @return return method param names
     */
    public static String[] getMethodParamNames(final Method m) throws IOException {
        if (METHOD_NAMES_POOL.containsKey(m)) return METHOD_NAMES_POOL.get(m);

        final String[] paramNames = new String[m.getParameterTypes().length];
        final String   n          = m.getDeclaringClass().getName();
        ClassReader    cr;
        try {
            cr = new ClassReader(n);
        } catch (IOException e) {
            return null;
        }
        cr.accept(new ClassVisitor(Opcodes.ASM5) {
            @Override
            public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
                final Type[] args = Type.getArgumentTypes(desc);
                // The method name is the same and the number of parameters is the same
                if (!name.equals(m.getName()) || !sameType(args, m.getParameterTypes())) {
                    return super.visitMethod(access, name, desc, signature, exceptions);
                }
                MethodVisitor v = super.visitMethod(access, name, desc, signature, exceptions);
                return new MethodVisitor(Opcodes.ASM5, v) {
                    @Override
                    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                        int i = index - 1;
                        // if it is a static method, the first is the parameter
                        // if it's not a static method, the first one is "this" and then the parameter of the method
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
        METHOD_NAMES_POOL.put(m, paramNames);
        return paramNames;
    }

}
