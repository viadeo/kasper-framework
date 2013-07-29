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
 * Kasper Relation marker
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperRelation {
	 
	/**
	 * @return the relation's label
	 */
	String label();
	
	/**
	 * @return the relation's description
	 */
	String description() default "";
	
	/**
	 * @return the relation's domain
	 */
	Class<? extends Domain> domain();

    /**
     * @return the relation verb from ontology
     */
    String verb() default "";
	
}
