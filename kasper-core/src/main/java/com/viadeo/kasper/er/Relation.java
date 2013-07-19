// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.KasperID;
import com.viadeo.kasper.ddd.Entity;

/**
 * 
 * A relation is an entity used to link to Concept Aggregate roots
 *
 * @param <S> Source entity of the relation
 * @param <T> Target entity of the relation
 * 
 * @see com.viadeo.kasper.ddd.Domain
 * @see com.viadeo.kasper.ddd.Entity
 */
public interface Relation<S extends RootConcept, T extends RootConcept> extends Entity {

	/**
	 * The position of the source concept parameter
	 */
	Integer SOURCE_PARAMETER_POSITION = 0;
	
	/**
	 * The position of the target concept parameter
	 */
	Integer TARGET_PARAMETER_POSITION = 1;
	
	/**
	 * @return the identifier of the relation's source entity
	 */
	KasperID getSourceIdentifier();
	
	/**
	 * @return the identifier of the relation's target entity
	 */
	KasperID getTargetIdentifier();
	
	/**
	 * @return true if the relation is marked has being bidirectional
	 */
	boolean isBidirectional();
	
}
