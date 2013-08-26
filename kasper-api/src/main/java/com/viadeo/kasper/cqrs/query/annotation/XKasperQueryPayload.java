// ============================================================================
// KASPER - Kasper is the treasure keeper
// www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
// Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Base marker for Kasper query payloads
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperQueryPayload {

    /**
     * @return the query payload description
     */
    String description() default "";

}
