package com.viadeo.kasper.event.annotation;

import com.viadeo.kasper.ddd.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

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


    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Start {

        /**
         * @return the kasper event's identifier getter (in order to find the saga instance)
         */
        String getter();

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface End {

        /**
         * @return the kasper event's identifier getter (in order to find the saga instance)
         */
        String getter();

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface BasicStep {

        /**
         * @return the kasper event's identifier getter (in order to find the saga instance)
         */
        String getter();

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public @interface Schedule {

        /**
         * @return the kasper event's identifier getter (in order to find the saga instance)
         */
        String getter();

        long delay();

        TimeUnit unit();
    }
}
