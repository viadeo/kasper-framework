// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import static com.codahale.metrics.MetricRegistry.name;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class AbstractEventListener<E extends Event>
		implements EventListener<E> {

    private static final MetricRegistry metrics = KasperMetrics.getRegistry();

	private final Class<? extends Event> eventClass;
	private CommandGateway commandGateway;

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

    public void setCommandGateway(final CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
    }

    protected Optional<CommandGateway> getCommandGateway() {
        return Optional.of(this.commandGateway);
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

        /* Start timer */
        final Timer.Context timer = metrics.timer(name(this.getClass(), "handle-time")).time();

        try {
			this.handle(message);
		} catch (final UnsupportedOperationException e) {
			this.handle((E) eventMessage.getPayload());
		}

        /* Stop timer and record a tick */
        final long time = timer.stop();
        metrics.histogram(name(EventListener.class, "handle-times")).update(time);
        metrics.histogram(name(this.getClass(), "handle-times")).update(time);

        metrics.meter(name(EventListener.class, "handles")).mark();
        metrics.meter(name(this.getClass(), "handles")).mark();
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
