/*
 * Copyright (C) 2007 The Guava Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blade.kit.base;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;

import com.blade.kit.Assert;

/**
 * Static utility methods pertaining to instances of {@link Throwable}.
 *
 * <p>
 * See the Guava User Guide entry on
 * <a href= "http://code.google.com/p/guava-libraries/wiki/ThrowablesExplained">
 * Throwables</a>.
 *
 * @author Kevin Bourrillion
 * @author Ben Yu
 * @since 1.0
 */
public final class ThrowableKit {
	private ThrowableKit() {
	}

	/**
	 * Propagates {@code throwable} exactly as-is, if and only if it is an
	 * instance of {@code declaredType}. Example usage:
	 * 
	 * <pre>
	 * try {
	 * 	someMethodThatCouldThrowAnything();
	 * } catch (IKnowWhatToDoWithThisException e) {
	 * 	handle(e);
	 * } catch (Throwable t) {
	 * 	Throwables.propagateIfInstanceOf(t, IOException.class);
	 * 	Throwables.propagateIfInstanceOf(t, SQLException.class);
	 * 	throw Throwables.propagate(t);
	 * }
	 * </pre>
	 */
	public static <X extends Throwable> void propagateIfInstanceOf(Throwable throwable, Class<X> declaredType)
			throws X {
		// Check for null is needed to avoid frequent JNI calls to isInstance().
		if (throwable != null && declaredType.isInstance(throwable)) {
			throw declaredType.cast(throwable);
		}
	}

	/**
	 * Propagates {@code throwable} exactly as-is, if and only if it is an
	 * instance of {@link RuntimeException} or {@link Error}. Example usage:
	 * 
	 * <pre>
	 * try {
	 * 	someMethodThatCouldThrowAnything();
	 * } catch (IKnowWhatToDoWithThisException e) {
	 * 	handle(e);
	 * } catch (Throwable t) {
	 * 	Throwables.propagateIfPossible(t);
	 * 	throw new RuntimeException("unexpected", t);
	 * }
	 * </pre>
	 */
	public static void propagateIfPossible(Throwable throwable) {
		propagateIfInstanceOf(throwable, Error.class);
		propagateIfInstanceOf(throwable, RuntimeException.class);
	}

	/**
	 * Propagates {@code throwable} exactly as-is, if and only if it is an
	 * instance of {@link RuntimeException}, {@link Error}, or
	 * {@code declaredType}. Example usage:
	 * 
	 * <pre>
	 * try {
	 * 	someMethodThatCouldThrowAnything();
	 * } catch (IKnowWhatToDoWithThisException e) {
	 * 	handle(e);
	 * } catch (Throwable t) {
	 * 	Throwables.propagateIfPossible(t, OtherException.class);
	 * 	throw new RuntimeException("unexpected", t);
	 * }
	 * </pre>
	 *
	 * @param throwable
	 *            the Throwable to possibly propagate
	 * @param declaredType
	 *            the single checked exception type declared by the calling
	 *            method
	 */
	public static <X extends Throwable> void propagateIfPossible(Throwable throwable, Class<X> declaredType) throws X {
		propagateIfInstanceOf(throwable, declaredType);
		propagateIfPossible(throwable);
	}

	/**
	 * Propagates {@code throwable} exactly as-is, if and only if it is an
	 * instance of {@link RuntimeException}, {@link Error},
	 * {@code declaredType1}, or {@code declaredType2}. In the unlikely case
	 * that you have three or more declared checked exception types, you can
	 * handle them all by invoking these methods repeatedly. See usage example
	 * in {@link #propagateIfPossible(Throwable, Class)}.
	 *
	 * @param throwable
	 *            the Throwable to possibly propagate
	 * @param declaredType1
	 *            any checked exception type declared by the calling method
	 * @param declaredType2
	 *            any other checked exception type declared by the calling
	 *            method
	 */
	public static <X1 extends Throwable, X2 extends Throwable> void propagateIfPossible(Throwable throwable,
			Class<X1> declaredType1, Class<X2> declaredType2) throws X1, X2 {
		Assert.notNull(declaredType2);
		propagateIfInstanceOf(throwable, declaredType1);
		propagateIfPossible(throwable, declaredType2);
	}

	/**
	 * Propagates {@code throwable} as-is if it is an instance of
	 * {@link RuntimeException} or {@link Error}, or else as a last resort,
	 * wraps it in a {@code RuntimeException} then propagates.
	 * <p>
	 * This method always throws an exception. The {@code RuntimeException}
	 * return type is only for client code to make Java type system happy in
	 * case a return value is required by the enclosing method. Example usage:
	 * 
	 * <pre>
	 * T doSomething() {
	 * 	try {
	 * 		return someMethodThatCouldThrowAnything();
	 * 	} catch (IKnowWhatToDoWithThisException e) {
	 * 		return handle(e);
	 * 	} catch (Throwable t) {
	 * 		throw Throwables.propagate(t);
	 * 	}
	 * }
	 * </pre>
	 *
	 * @param throwable
	 *            the Throwable to propagate
	 * @return nothing will ever be returned; this return type is only for your
	 *         convenience, as illustrated in the example above
	 */
	public static RuntimeException propagate(Throwable throwable) {
		propagateIfPossible(Assert.checkNotNull(throwable));
		throw new RuntimeException(throwable);
	}

	/**
	 * Returns the innermost cause of {@code throwable}. The first throwable in
	 * a chain provides context from when the error or exception was initially
	 * detected. Example usage:
	 * 
	 * <pre>
	 * assertEquals("Unable to assign a customer id", Throwables.getRootCause(e).getMessage());
	 * </pre>
	 */
	public static Throwable getRootCause(Throwable throwable) {
		Throwable cause;
		while ((cause = throwable.getCause()) != null) {
			throwable = cause;
		}
		return throwable;
	}

	/**
	 * Returns a string containing the result of {@link Throwable#toString()
	 * toString()}, followed by the full, recursive stack trace of
	 * {@code throwable}. Note that you probably should not be parsing the
	 * resulting string; if you need programmatic access to the stack frames,
	 * you can call {@link Throwable#getStackTrace()}.
	 */
	public static String getStackTraceAsString(Throwable throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		return stringWriter.toString();
	}

	public static RuntimeException unchecked(Throwable e) {
        if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        if (e instanceof InvocationTargetException) {
            return unchecked(((InvocationTargetException) e).getTargetException());
        }
        return new RuntimeException(e);
    }
}
