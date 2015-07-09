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
package blade.exception;

import java.util.HashMap;
import java.util.Map;

/**
 * 异常栈
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public class ExceptionStack {
    
    private static ExceptionStack defaultInstance;

    
    public static ExceptionStack getInstance() {
        if (defaultInstance == null) {
            defaultInstance = new ExceptionStack();
        }
        return defaultInstance;
    }

    private Map<Class<? extends Exception>, ExceptionHandlerImpl> exceptionMap;

    public ExceptionStack() {
        this.exceptionMap = new HashMap<Class<? extends Exception>, ExceptionHandlerImpl>();
    }

    public void map(Class<? extends Exception> exceptionClass, ExceptionHandlerImpl handler) {
        this.exceptionMap.put(exceptionClass, handler);
    }

    public ExceptionHandlerImpl getHandler(Class<? extends Exception> exceptionClass) {
        if (!this.exceptionMap.containsKey(exceptionClass)) {

            Class<?> superclass = exceptionClass.getSuperclass();
            do {
                if (this.exceptionMap.containsKey(superclass)) {
                    ExceptionHandlerImpl handler = this.exceptionMap.get(superclass);
                    this.exceptionMap.put(exceptionClass, handler);
                    return handler;
                }

                superclass = superclass.getSuperclass();
            } while (superclass != null);

            this.exceptionMap.put(exceptionClass, null);
            return null;
        }

        return this.exceptionMap.get(exceptionClass);
    }

    public ExceptionHandlerImpl getHandler(Exception exception) {
        return this.getHandler(exception.getClass());
    }
    
    public Throwable fillInStackTrace() {
        return null;
    }
}
