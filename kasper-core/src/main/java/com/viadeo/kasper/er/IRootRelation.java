// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.ddd.IAggregateRoot;

/**
 *
 * A aggregate root for Kasper relation
 *
 * @param <S> Source of the relation
 * @param <T> Target of the relation
 * 
 * @see IRelation
 * @see IAggregateRoot
 */
public interface IRootRelation<S extends IRootConcept, T extends IRootConcept> 
		extends IRelation<S, T>, IAggregateRoot {

}
