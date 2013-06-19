// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class AbstractEventListener<E extends Event>
		implements EventListener<E> {

	private final Class<? extends Event> eventClass;
	
	// ------------------------------------------------------------------------
	
	public AbstractEventListener() {
		@SuppressWarnings("unchecked")
		final Optional<Class<? extends Event>> eventClassOpt =
				(Optional<Class<? extends Event>>)
				ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(), EventListener.class, EventListener.EVENT_PARAMETER_POSITION);
		
		if (eventClassOpt.isPresent()) {
			this.eventClass = eventClassOpt.get();
		} else {
			throw new KasperException("Unable to identify event class for " + this.getClass());
		}
	}
	
	// ------------------------------------------------------------------------
	
	public Class<? extends Event> getEventClass() {
		return this.eventClass;
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Wrapper for Axon event messages
	 * 
	 * @see org.axonframework.eventhandling.EventListener#handle(org.axonframework.domain.EventMessage)
	 */
	@SuppressWarnings({"unchecked", "rawtypes"}) // Safe
	@Override
	public void handle(final org.axonframework.domain.EventMessage eventMessage) {
		
		if (!this.getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
			return;
		}
		
		final com.viadeo.kasper.event.EventMessage<E> message = new DefaultEventMessage(eventMessage);
		
		try {
			this.handle(message);
		} catch (final UnsupportedOperationException e) {
			this.handle((E) eventMessage.getPayload());
		}
	}

	// ------------------------------------------------------------------------
	
	/**
	 * @param eventMessage the Kasper event message to handle
	 */
	public void handle(final com.viadeo.kasper.event.EventMessage<E> eventMessage){
		throw new UnsupportedOperationException();
	}

	/**
	 * @param event the Kasper event to handle
	 */
	public void handle(final E event){
		throw new UnsupportedOperationException();
	}
	
}
