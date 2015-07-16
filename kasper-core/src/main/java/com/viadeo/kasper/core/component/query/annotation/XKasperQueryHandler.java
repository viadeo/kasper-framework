// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.annotation;

import com.viadeo.kasper.api.component.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query handler marker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperQueryHandler {

    /**
     * @return the name of the handler
     */
    String name() default "";

    /**
     * @return the description of the handler
     */
    String description() default "";

    /**
     * @return the associated domain
     */
    Class<? extends Domain> domain();

    /**
     * @return tags for this handler
     */
    String[] tags() default {};

    /**
     * Whether the response of this query handler should be cached.
     * False by default.
     * @return the cache rules
     */
    XKasperQueryCache cache() default @XKasperQueryCache(enabled = false);

}
