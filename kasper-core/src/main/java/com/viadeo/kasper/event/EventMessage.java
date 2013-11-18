// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.viadeo.kasper.context.Context;

/**
 *
 * Decorator for Axon Event messages
 *
 * @param <E> Encapsulated Kasper event class
 * 
 * @see com.viadeo.kasper.event.EventMessage
 * @see com.viadeo.kasper.event.Event
 */
public class EventMessage<E extends IEvent> {
	private static final long serialVersionUID = -214545825521867826L;

	private final org.axonframework.domain.EventMessage<E> axonMessage;

	// ------------------------------------------------------------------------

	public EventMessage(final org.axonframework.domain.EventMessage<E> eventMessage) {
		this.axonMessage = eventMessage;
	}

	// ------------------------------------------------------------------------

	public Context getContext() {
		return (Context) this.axonMessage.getMetaData().get(Context.METANAME);
	}

	public E getEvent() {
		return this.axonMessage.getPayload();
	}

	// ------------------------------------------------------------------------
	
	public org.axonframework.domain.EventMessage<E> getAxonMessage() {
		return this.axonMessage;
	}
	
}
