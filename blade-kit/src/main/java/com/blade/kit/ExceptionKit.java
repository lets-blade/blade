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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 处理异常的工具类。
 *
 * @author	<a href="mailto:biezhi.me@gmail.com" target="_blank">biezhi</a>
 * @since	1.0
 */
public abstract class ExceptionKit {

	public static void makeRunTimeWhen(boolean flag, String message,
			Object... args) {
		if (flag) {
			message = String.format(message, args);
			RuntimeException e = new RuntimeException(message);
			throw correctStackTrace(e);
		}
	}

	public static void makeRunTime() {
		RuntimeException e = new RuntimeException();
		throw correctStackTrace(e);
	}

	public static void makeRunTime(String message, Object... args) {
		message = String.format(message, args);
		RuntimeException e = new RuntimeException(message);
		throw correctStackTrace(e);
	}

	public static void makeRuntime(Throwable cause) {
		RuntimeException e = new RuntimeException(cause);
		throw correctStackTrace(e);
	}

	public static void makeRuntime(String message, Throwable cause,
			Object... args) {
		message = String.format(message, args);
		RuntimeException e = new RuntimeException(message, cause);
		throw correctStackTrace(e);
	}

	/** 移除 Lang层堆栈信息 */
	private static RuntimeException correctStackTrace(RuntimeException e) {
		StackTraceElement[] s = e.getStackTrace();
		if(null != s && s.length > 0){
			e.setStackTrace(Arrays.copyOfRange(s, 1, s.length));
		}
		return e;
	}
	
    /**
     * 检查异常是否由指定类型的异常引起。
     * 
     * @param throwable 受检异常
     * @param causeType 指定类型
     * @return 如果是则返还true
     */
    public static boolean causedBy(Throwable throwable, Class<? extends Throwable> causeType) {
        Assert.notNull(causeType, "causeType");

        Set<Throwable> causes = CollectionKit.createHashSet();

        for (; throwable != null && !causeType.isInstance(throwable) && !causes.contains(throwable); throwable =
                throwable.getCause()) {
            causes.add(throwable);
        }

        return throwable != null && causeType.isInstance(throwable);
    }

    /**
     * 取得最根本的异常。
     * 
     * @param throwable 受检异常
     * @return 最根本的异常。
     */
    public static Throwable getRootCause(Throwable throwable) {
        List<Throwable> causes = getCauses(throwable, true);

        return causes.isEmpty() ? null : causes.get(0);
    }

    /**
     * 取得包括当前异常在内的所有的causes异常，按出现的顺序排列。
     * 
     * @param throwable 受检异常
     * @return 包括当前异常在内的所有的causes异常，按出现的顺序排列。
     */
    public static List<Throwable> getCauses(Throwable throwable) {
        return getCauses(throwable, false);
    }

    /**
     * 取得包括当前异常在内的所有的causes异常，按出现的顺序排列
     * 
     * @param throwable 受检异常
     * @param reversed 是否反向
     * @return 包括当前异常在内的所有的causes异常，按出现的顺序排列。
     */
    public static List<Throwable> getCauses(Throwable throwable, boolean reversed) {
        LinkedList<Throwable> causes = CollectionKit.createLinkedList();

        for (; throwable != null && !causes.contains(throwable); throwable = throwable.getCause()) {
            if (reversed) {
                causes.addFirst(throwable);
            } else {
                causes.addLast(throwable);
            }
        }

        return causes;
    }

    /**
     * 将异常转换成RuntimeException。
     * 
     * @param exception 受检异常
     * @return to RuntimeException</code
     */
    public static RuntimeException toRuntimeException(Exception exception) {
        return toRuntimeException(exception, null);
    }

    /**
     * 将异常转换成RuntimeException。
     * 
     * @param exception 受检异常
     * @param runtimeExceptionClass 转换异常的类型
     * @return to RuntimeException</code
     */
    public static RuntimeException toRuntimeException(Exception exception,
            Class<? extends RuntimeException> runtimeExceptionClass) {
        if (exception == null) {
            return null;
        }

        if (exception instanceof RuntimeException) {
            return (RuntimeException) exception;
        }
        if (runtimeExceptionClass == null) {
            return new RuntimeException(exception);
        }

        RuntimeException runtimeException;

        try {
            runtimeException = runtimeExceptionClass.newInstance();
        } catch (Exception ee) {
            return new RuntimeException(exception);
        }

        runtimeException.initCause(exception);
        return runtimeException;

    }

    /**
     * 抛出Throwable，但不需要声明throws Throwable，区分Exception 或Error。
     * 
     * @param throwable 受检异常
     * @throws Exception
     */
    public static void throwExceptionOrError(Throwable throwable) throws Exception {
        if (throwable instanceof Exception) {
            throw (Exception) throwable;
        }

        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        throw new RuntimeException(throwable);
    }

    /**
     * 抛出Throwable，但不需要声明throws Throwable，区分 RuntimeException、Exception
     * 或Error。
     * 
     * @param throwable 受检异常
     */
    public static void throwRuntimeExceptionOrError(Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error) throwable;
        }

        if (throwable instanceof RuntimeException) {
            throw (RuntimeException) throwable;
        }

        throw new RuntimeException(throwable);
    }

    /**
     * 取得异常的stacktrace字符串。
     * 
     * @param throwable 受检异常
     * @return stacktrace字符串
     */
    public static String getStackTrace(Throwable throwable) {
        StringWriter buffer = new StringWriter();
        PrintWriter out = new PrintWriter(buffer);

        throwable.printStackTrace(out);
        out.flush();

        return buffer.toString();
    }

}