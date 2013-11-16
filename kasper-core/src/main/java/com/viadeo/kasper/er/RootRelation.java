// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.KasperRelationID;
import com.viadeo.kasper.ddd.AggregateRoot;

/**
 *
 * A aggregate root for Kasper relation
 *
 * @param <S> Source of the relation
 * @param <T> Target of the relation
 * 
 * @see Relation
 * @see com.viadeo.kasper.ddd.AggregateRoot
 */
public interface RootRelation<S extends RootConcept, T extends RootConcept>
		extends Relation<S, T>, AggregateRoot<KasperRelationID> {

}
