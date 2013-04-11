// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.ddd.IComponentEntity;

/**
 *
 * A Kasper Concept which is owned by an upper Concept or an aggregate root
 *
 * @param <R> The parent root concept
 * 
 * @see IConcept
 */
public interface IComponentConcept<R extends IRootConcept> 
	extends IComponentEntity<R>, IConcept {
	
}
