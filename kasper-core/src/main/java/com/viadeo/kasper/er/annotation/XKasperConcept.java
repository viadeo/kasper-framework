// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.annotation;

import com.viadeo.kasper.ddd.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Kasper Concept marker
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperConcept {
	
	/**
	 * @return the concept's label
	 */
	String label();
	
	/**
	 * @return the concept's description
	 */
	String description() default "";
	
	/**
	 * @return the concept's domain
	 */
	Class<? extends Domain> domain();
	
}
