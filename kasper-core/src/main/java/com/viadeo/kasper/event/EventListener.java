// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

/**
 *
 * Kasper event listener
 *
 * @param <E> Event
 * 
 * @see Event
 */
public interface EventListener<E extends Event> extends org.axonframework.eventhandling.EventListener {

	/**
	 * Generic parameter position for the listened event
	 */
	int EVENT_PARAMETER_POSITION = 0;
	
	/**
	 * handle an event
	 * 
	 * @param eventMessage
	 */
	void handle(EventMessage<E> eventMessage);
	
}
