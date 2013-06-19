// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.event.impl;

import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.event.Event;

/**
 *
 * Decorator for Axon Event messages
 *
 * @param <E> Encapsulated Kasper event class
 * 
 * @see com.viadeo.kasper.event.EventMessage
 * @see com.viadeo.kasper.event.Event
 */
public class DefaultEventMessage<E extends Event> implements com.viadeo.kasper.event.EventMessage<E> {

	private static final long serialVersionUID = -214545825521867826L;

	private final org.axonframework.domain.EventMessage<E> axonMessage;

	// ------------------------------------------------------------------------

	public DefaultEventMessage(final org.axonframework.domain.EventMessage<E> eventMessage) {
		this.axonMessage = eventMessage;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.EventMessage#getContext()
	 */
	@Override
	public Context getContext() {
		return (Context) this.axonMessage.getMetaData().get(Context.METANAME);
	}

	/**
	 * @see com.viadeo.kasper.event.EventMessage#getEvent()
	 */
	@Override
	public E getEvent() {
		return this.axonMessage.getPayload();
	}

	// ------------------------------------------------------------------------
	
	public org.axonframework.domain.EventMessage<E> getAxonMessage() {
		return this.axonMessage;
	}
	
}
