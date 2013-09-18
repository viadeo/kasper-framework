package com.viadeo.kasper.core.boot.test;

import com.viadeo.kasper.core.boot.AnnotationProcessor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class TestProcessor implements AnnotationProcessor<TestProcessor.XTestAnnotation, TestProcessor.TestInterface> {

    @Override
    public boolean isAnnotationMandatory() {
        return true;
    }

    @Override
    public void process(final Class<?> clazz) {
        // Do nothing, we are just interested by execution
    }

    public static interface TestInterface { }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface XTestAnnotation { }

    @XTestAnnotation
    public static class TestClass implements TestInterface {}

    @XTestAnnotation
    public static final class TestChildClass extends TestClass { }
}
