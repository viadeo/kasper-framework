// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import org.axonframework.eventhandling.EventListener;

/**
 *
 * Kasper event listener
 *
 * @param <E> Event
 * 
 * @see IEvent
 */
public interface IEventListener<E extends IEvent> extends EventListener {

	/**
	 * Generic parameter position for the listened event
	 */
	int EVENT_PARAMETER_POSITION = 0;
	
	/**
	 * handle an event
	 * 
	 * @param eventMessage
	 */
	void handle(IEventMessage<E> eventMessage);
	
}
