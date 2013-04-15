// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.google.common.base.Optional;
import com.viadeo.kasper.event.IEvent;
import com.viadeo.kasper.event.IEventListener;
import com.viadeo.kasper.event.IEventMessage;
import com.viadeo.kasper.exception.KasperRuntimeException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class AbstractEventListener<E extends IEvent> 
		implements IEventListener<E> {

	private final Class<? extends IEvent> eventClass;
	
	// ------------------------------------------------------------------------
	
	public AbstractEventListener() {
		@SuppressWarnings("unchecked")
		final Optional<Class<? extends IEvent>> eventClass = 
				(Optional<Class<? extends IEvent>>) 
				ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(), IEventListener.class, IEventListener.EVENT_PARAMETER_POSITION);
		
		if (eventClass.isPresent()) {
			this.eventClass = eventClass.get();
		} else {
			throw new KasperRuntimeException("Unable to identify event class for " + this.getClass());
		}
	}
	
	// ------------------------------------------------------------------------
	
	public Class<? extends IEvent> getEventClass() {
		return this.eventClass;
	}
	
	// ------------------------------------------------------------------------
	
	/**
	 * Wrapper for Axon event messages
	 * 
	 * @see org.axonframework.eventhandling.EventListener#handle(org.axonframework.domain.EventMessage)
	 */
	@SuppressWarnings("unchecked") // Safe
	@Override
	public void handle(final @SuppressWarnings("rawtypes") org.axonframework.domain.EventMessage eventMessage) {
		
		if (!this.getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
			return;
		}
		
		final IEventMessage<E> message = new EventMessage<E>(eventMessage);
		
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
	public void handle(final IEventMessage<E> eventMessage){
		throw new UnsupportedOperationException();
	}

	/**
	 * @param event the Kasper event to handle
	 */
	public void handle(final E event){
		throw new UnsupportedOperationException();
	}
	
}
