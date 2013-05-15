// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.viadeo.kasper.ddd.impl.AbstractComponentEntity;
import com.viadeo.kasper.er.IComponentConcept;
import com.viadeo.kasper.er.IRootConcept;

/**
 * A base implementation for a component concept
 *
 * @param <R> the parent's concept root
 * 
 * @see IConcept
 * @see IComponentConcept
 */
public abstract class AbstractComponentConcept<R extends IRootConcept> 
		extends AbstractComponentEntity<R> 
		implements IComponentConcept<R> {

	private static final long serialVersionUID = -5237849445883458840L;
	
}
