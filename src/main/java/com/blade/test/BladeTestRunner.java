package com.blade.test;

import com.blade.Blade;
import com.blade.ioc.annotation.Inject;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;

import java.lang.reflect.Field;

@SuppressWarnings("deprecation")
public class BladeTestRunner extends BlockJUnit4ClassRunner {

    private Class<?> clazz;
    private Class<?> mainCls;
    private Blade    blade;

    public BladeTestRunner(Class<?> clazz) throws InitializationError {
        super(clazz);
        this.clazz = clazz;
        BladeApplication bladeApplication = clazz.getAnnotation(BladeApplication.class);
        if(null == bladeApplication){
            throw new RuntimeException("Please use @BladeApplication configuration main class type :)");
        }
        mainCls = bladeApplication.value();
    }

    @Override
    protected Statement withBeforeClasses(final Statement statement) {
        final Statement junitStatement = super.withBeforeClasses(statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                blade = Blade.me().start(mainCls).await();
                junitStatement.evaluate();
            }
        };
    }

    @Override
    protected Statement withBefores(final FrameworkMethod method, Object target, final Statement statement) {
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Inject inject = declaredField.getAnnotation(Inject.class);
            if (null != inject) {
                Object bean = blade.getBean(declaredField.getType());
                try {
                    declaredField.setAccessible(true);
                    declaredField.set(target, bean);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }

        final Statement junitStatement = super.withBefores(method, target, statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                junitStatement.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfters(final FrameworkMethod method, Object target, final Statement statement) {
        final Statement junitStatement = super.withAfters(method, target, statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                junitStatement.evaluate();
            }
        };
    }

    @Override
    protected Statement withAfterClasses(final Statement statement) {
        final Statement junitStatement = super.withAfterClasses(statement);
        return new Statement() {
            @Override
            public void evaluate() throws Throwable {
                junitStatement.evaluate();
                blade.stop();
            }
        };
    }
}