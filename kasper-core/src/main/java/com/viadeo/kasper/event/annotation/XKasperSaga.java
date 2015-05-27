package com.viadeo.kasper.event.annotation;

import com.viadeo.kasper.ddd.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Kasper Saga marker
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperSaga {

    /**
     * @return the event listener's description
     */
    String description() default "";

    /**
     * @return the domain of this command handler
     */
    Class<? extends Domain> domain();
}
