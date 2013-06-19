// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.ddd.Repository;

/**
 *
 * A repository for relations
 *
 * @param <RELATION> Relation to be stored
 * 
 * @see com.viadeo.kasper.ddd.Repository
 * @see Relation
 */
public interface RelationRepository<RELATION extends RootRelation<?,?>>
		extends Repository<RELATION> {
	
}
