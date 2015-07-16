// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.annotation;

import com.viadeo.kasper.api.component.Domain;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * Kasper event marker
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperEventListener {
	
	/**
	 * @return the event listener's description
	 */
	String description() default "";

    /**
     * @return the domain of this event listener
     */
    Class<? extends Domain> domain();

    /**
     * @return tags for this listener
     */
    String[] tags() default {};

}
