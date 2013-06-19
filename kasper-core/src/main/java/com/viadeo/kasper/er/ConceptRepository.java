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
 * A repository for concepts
 *
 * @param <CROOT> Concept root aggregate
 * 
 * @see Concept
 * @see com.viadeo.kasper.ddd.Repository
 */
public interface ConceptRepository<CROOT extends RootConcept>
	extends Repository<CROOT> {

}
