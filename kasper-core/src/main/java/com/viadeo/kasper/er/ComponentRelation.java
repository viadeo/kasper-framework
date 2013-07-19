// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.ddd.ComponentEntity;

/**
 *
 * A Kasper Relation which is owned by an upper Relation or an aggregate root
 *
 * @param <S> Source concept of the relation
 * @param <T> Target concept of the relation
 * 
 * @see com.viadeo.kasper.ddd.Domain
 * @see Relation
 * @see com.viadeo.kasper.ddd.Entity
 */
public interface ComponentRelation<S extends RootConcept, T extends RootConcept>
		extends ComponentEntity<S>, Relation<S, T> {
	
}
