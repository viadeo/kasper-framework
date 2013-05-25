// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.viadeo.kasper.context.IContext;

import java.io.Serializable;

/**
 *
 * Encapsulate Kasper events during bus traversal
 *
 * @param <E> The payload event
 * 
 */
public interface IEventMessage<E extends IEvent> extends Serializable {

	/**
	 * @return the encapsulated event
	 */
	E getEvent();

	/**
	 * @return the execution context of the event
	 */
	IContext getContext();

}
