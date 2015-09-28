// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.annotation;

import com.viadeo.kasper.api.component.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Kasper Command Handler marker
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperCommandHandler {
	
	/**
	 * @return the command handler's description
	 */
	String description() default "";

    /**
     * @return the domain of this command handler
     */
	Class<? extends Domain> domain();

	/**
	 * @return tags for this handler
	 */
	String[] tags() default {};

    /**
     * @return true if the handler generate an authentication token, false otherwise
     */
    boolean createAuthenticationToken() default false;

}
