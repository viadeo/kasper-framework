// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.ddd.IComponentEntity;
import com.viadeo.kasper.ddd.IEntity;

/**
 *
 * A Kasper Relation which is owned by an upper Relation or an aggregate root
 *
 * @param <D> Domain
 * @param <S> Source concept of the relation
 * @param <T> Target concept of the relation
 * 
 * @see IDomain
 * @see IRelation
 * @see IEntity 
 */
public interface IComponentRelation<S extends IRootConcept, T extends IRootConcept> 
		extends IComponentEntity<S>, IRelation<S, T> {
	
}
