package com.viadeo.kasper.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Kasper Saga start marker
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XKasperSagaStep {

    /**
     * @return the kasper event's identifier getter (in order to find the saga instance)
     */
    String getter() default "";

}
