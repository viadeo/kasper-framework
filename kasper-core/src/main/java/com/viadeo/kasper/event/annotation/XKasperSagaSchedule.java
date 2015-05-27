package com.viadeo.kasper.event.annotation;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormat;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface XKasperSagaSchedule {

    int duration() default 0;
    TimeUnit unit();
}
