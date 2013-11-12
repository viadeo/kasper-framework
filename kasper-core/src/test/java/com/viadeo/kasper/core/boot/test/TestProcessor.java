// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
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
    public void beforeProcess() {
        // Do nothing
    }

    @Override
    public void process(final Class clazz) {
        // Do nothing, we are just interested by execution
    }

    @Override
    public void afterProcess() {
        // Do nothing
    }

    public static interface TestInterface { }

    @Retention(RetentionPolicy.RUNTIME)
    public static @interface XTestAnnotation { }

    @XTestAnnotation
    public static class TestClass implements TestInterface {}

    @XTestAnnotation
    public static final class TestChildClass extends TestClass { }
}
