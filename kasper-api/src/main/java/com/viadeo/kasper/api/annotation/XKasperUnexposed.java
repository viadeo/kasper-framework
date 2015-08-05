package com.viadeo.kasper.api.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares an <code>Handler</code> as unexposed
 *
 * <p>When used on the handler class the handler will not be exposed
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperUnexposed { }
