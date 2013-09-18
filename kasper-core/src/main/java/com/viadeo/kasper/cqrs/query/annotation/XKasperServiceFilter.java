// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.query.annotation;

import com.viadeo.kasper.core.annotation.XKasperUnregistered;
import com.viadeo.kasper.ddd.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query service marker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperServiceFilter {

	/**
	 * @return the name of the service
	 */
	String name() default "";

    /**
     * Sets to true if this filter must be applied to every service in the runtime context
     *
     * @return if this filter is global
     */
    boolean global() default false;

    /**
     * Optional sticky domain for this service (in case of global filter)
     *
     * The filter will only be applied on services of this domain
     */
    Class<? extends Domain> domain() default NullDomain.class;

    /**
     * Static default (null) domain
     */
    @XKasperUnregistered
    static final class NullDomain implements Domain { }

}
