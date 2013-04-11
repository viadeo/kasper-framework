// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.event.IEvent;

/**
 *
 * Kasper Event marker
 *
 * @see IEvent
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface XKasperEvent {
	
	/**
	 * @return the event's action
	 */
	String action();
	
	/**
	 * @return the event's domain
	 */
	Class<? extends IDomain> domain();
	
	/**
	 * @return the event's description
	 */
	String description() default "";
	
}
