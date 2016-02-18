// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.query.annotation;

import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.core.component.annotation.XKasperUnregistered;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Query handler marker
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperQueryHandlerAdapter {

	/**
	 * @return the name of the handler
	 */
	String name() default "";

    /**
     * Sets to true if this adapter must be applied to every handler in the runtime context
     *
     * @return if this adapter is global
     */
    boolean global() default false;

    /**
     * Optional sticky domain for this handler (in case of global adapter)
     *
     * The adapter will only be applied on handlers of this domain
     *
     * @return the domain
     */
    Class<? extends Domain> domain() default NullDomain.class;

    /**
     * Static default (null) domain
     */
    @XKasperUnregistered
    final class NullDomain implements Domain { }

}
