// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.annotation;

import com.viadeo.kasper.cqrs.query.QueryHandlerAdapter;
import com.viadeo.kasper.ddd.Domain;

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
     * @return the associated query handler adapters
     */
    Class<? extends QueryHandlerAdapter>[] adapters() default {};

    /**
     * Whether the response of this query handler should be cached.
     * False by default.
     */
    XKasperQueryCache cache() default @XKasperQueryCache(enabled = false);

}
