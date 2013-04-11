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
 * A repository for concepts
 *
 * @param <CROOT> Concept root aggregate
 * 
 * @see IConcept
 * @see IRepository
 */
public interface IConceptRepository<CROOT extends IRootConcept> 
	extends IRepository<CROOT> {

}
