// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.ddd.IRepository;

/**
 *
 * A repository for relations
 *
 * @param <RELATION> Relation to be stored
 * 
 * @see IRepository
 * @see IRelation
 */
public interface IRelationRepository<RELATION extends IRootRelation<?,?>> 
		extends IRepository<RELATION> {
	
}
