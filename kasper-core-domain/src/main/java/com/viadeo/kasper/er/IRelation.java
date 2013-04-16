// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.er;

import com.viadeo.kasper.IKasperID;
import com.viadeo.kasper.ddd.IEntity;

/**
 * 
 * A relation is an entity used to link to Concept Aggregate roots
 *
 * @param <D> Domain
 * @param <S> Source entity of the relation
 * @param <T> Target entity of the relation
 * 
 * @see IDomain
 * @see IEntity
 */
public interface IRelation<S extends IRootConcept, T extends IRootConcept> extends IEntity {

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
	IKasperID getSourceIdentifier();
	
	/**
	 * @return the identifier of the relation's target entity
	 */
	IKasperID getTargetIdentifier();
	
	/**
	 * @return true if the relation is marked has being bidirectional
	 */
	boolean isBidirectional();
	
}
