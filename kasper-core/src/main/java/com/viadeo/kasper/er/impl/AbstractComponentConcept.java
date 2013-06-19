// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er.impl;

import com.viadeo.kasper.ddd.impl.AbstractComponentEntity;
import com.viadeo.kasper.er.ComponentConcept;
import com.viadeo.kasper.er.RootConcept;

/**
 * A base implementation for a component concept
 *
 * @param <R> the parent's concept root
 * 
 * @see com.viadeo.kasper.er.Concept
 * @see com.viadeo.kasper.er.ComponentConcept
 */
public abstract class AbstractComponentConcept<R extends RootConcept>
		extends AbstractComponentEntity<R> 
		implements ComponentConcept<R> {

	private static final long serialVersionUID = -5237849445883458840L;
	
}
