package com.viadeo.kasper.core.component.eventbus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Timeout {

    /**
     * Indicates the amount of time the dispatching thread may wait for a result.
     * @return value
     */
    int value();

    /**
     * Indicates the unit in which the timeout is declared. Defaults to milliseconds.
     * @return unit
     */
    TimeUnit unit() default TimeUnit.MILLISECONDS;
}
