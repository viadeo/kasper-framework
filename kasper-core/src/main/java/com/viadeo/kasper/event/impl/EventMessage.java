// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================

package com.viadeo.kasper.event.impl;

import com.viadeo.kasper.context.IContext;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.IEventMessage;

/**
 *
 * Decorator for Axon Event messages
 *
 * @param <E> Encapsulated Kasper event class
 * 
 * @see IEventMessage
 * @see IEvent
 */
public class EventMessage<E extends IEvent> implements IEventMessage<E> {

	private static final long serialVersionUID = -214545825521867826L;

	private final org.axonframework.domain.EventMessage<E> axonMessage;

	// ------------------------------------------------------------------------

	public EventMessage(final org.axonframework.domain.EventMessage<E> eventMessage) {
		this.axonMessage = eventMessage;
	}

	// ------------------------------------------------------------------------

	/**
	 * @see com.viadeo.kasper.event.IEventMessage#getContext()
	 */
	@Override
	public IContext getContext() {
		return (IContext) this.axonMessage.getMetaData().get(IContext.METANAME);
	}

	/**
	 * @see com.viadeo.kasper.event.IEventMessage#getEvent()
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
