package com.blade.kit;

import com.blade.exception.BladeException;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author darren
 * @date 2019/3/18 11:47
 */
public class UncheckedFnKit {

    @FunctionalInterface
    public interface Consumer_WithExceptions<T, E extends Throwable> {
        void accept(T t) throws E;
    }

    @FunctionalInterface
    public interface BiConsumer_WithExceptions<T, U, E extends Throwable> {
        void accept(T t, U u) throws E;
    }

    @FunctionalInterface
    public interface Function_WithExceptions<T, R, E extends Throwable> {
        R apply(T t) throws E;
    }

    @FunctionalInterface
    public interface Supplier_WithExceptions<T, E extends Throwable> {
        T get() throws E;
    }

    @FunctionalInterface
    public interface Runnable_WithExceptions<E extends Throwable> {
        void run() throws E;
    }

    public static <T> Consumer<T> consumer(Consumer_WithExceptions<T, Throwable> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Throwable throwable) {
                throw new BladeException(throwable);
            }
        };
    }

    public static <T, U> BiConsumer<T, U> biConsumer(BiConsumer_WithExceptions<T, U, Throwable> biConsumer) {
        return (t, u) -> {
            try {
                biConsumer.accept(t, u);
            } catch (Throwable throwable) {
                throw new BladeException(throwable);
            }
        };
    }

    public static <T, R> Function<T, R> function(Function_WithExceptions<T, R, Throwable> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Throwable throwable) {
                throw new BladeException(throwable);
            }
        };
    }

    public static <T> Supplier<T> supplier(Supplier_WithExceptions<T, Throwable> function) {
        return () -> {
            try {
                return function.get();
            } catch (Throwable throwable) {
                throw new BladeException(throwable);
            }
        };
    }

    public static Runnable runnable(Runnable_WithExceptions t) {
        return ()-> {
            try {
                t.run();
            } catch (Throwable throwable) {
                throw new BladeException(throwable);
            }
        };
    }
}
