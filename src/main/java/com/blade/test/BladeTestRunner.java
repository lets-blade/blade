package com.blade.test;

import com.blade.ioc.annotation.Inject;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;

@SuppressWarnings("deprecation")
public class BladeTestRunner extends BlockJUnit4ClassRunner {

    private Class<?> clazz;

    public BladeTestRunner(Class<?> klass) throws InitializationError {
        super(klass);
        this.clazz = klass;
    }

    // 拦截 BeforeClass 事件
    protected Statement withBeforeClasses(final Statement statement) {
        final Statement junitStatement = super.withBeforeClasses(statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("Before Class: " + clazz.getName());
                junitStatement.evaluate();
            }
        };
    }

    // 拦截每一个方法的 Before 事件
    protected Statement withBefores(final FrameworkMethod method, Object target, final Statement statement) {

        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Inject inject = declaredField.getAnnotation(Inject.class);
//            if () {
//            }
        }

        final Statement junitStatement = super.withBefores(method, target, statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("Before before method: " + method.getName());
                junitStatement.evaluate();
//                System.out.println("After before method: " + method.getName());
            }
        };
    }

    // 截获每一个测试方法的 after 事件
    protected Statement withAfters(final FrameworkMethod method, Object target, final Statement statement) {
        final Statement junitStatement = super.withAfters(method, target, statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                junitStatement.evaluate();
                System.out.println("After method: " + method.getName());
            }

        };
    }

    // 截获测试类的 after 事件
    protected Statement withAfterClasses(final Statement statement) {
        final Statement junitStatement = super.withAfterClasses(statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                junitStatement.evaluate();
                System.out.println("After Class: " + clazz.getName());
            }
        };
    }
}