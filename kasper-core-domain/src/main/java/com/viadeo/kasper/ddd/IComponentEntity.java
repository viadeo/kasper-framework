// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.ddd;

import org.axonframework.eventsourcing.EventSourcedEntity;

import com.viadeo.kasper.IDomain;
import com.viadeo.kasper.er.IRootConcept;

/**
 *
 * Using Kasper, a component entity is an entity which is not an aggregate
 * This kind of entity is used as a component of a parent aggregate
 * 
 * @see IEntity
 * @see IDomain
 *
 * @param <R> the parent concept root
 */
public interface IComponentEntity<R extends IRootConcept> 
		extends IEntity, EventSourcedEntity {

	/**
	 * The position of the parent aggregate in generic parameters
	 */
	public static final int PARENT_ARGUMENT_POSITION = 0;
	
}
