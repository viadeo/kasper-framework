// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event.impl;

import com.codahale.metrics.Histogram;
import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class AbstractEventListener<E extends Event>
		implements EventListener<E> {

    private static final MetricRegistry METRICS = KasperMetrics.getRegistry();
    private static final Histogram METRICLASSHANDLETIMES = METRICS.histogram(name(EventListener.class, "handle-times"));
    private static final Meter METRICLASSHANDLES = METRICS.meter(name(EventListener.class, "handles"));
    private static final Meter METRICLASSERRORS = METRICS.meter(name(EventListener.class, "errors"));

    private Timer metricTimer;
    private Histogram metricHandleTimes;
    private Meter metricHandles;
    private Meter metricErrors;

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
        this.commandGateway = checkNotNull(commandGateway);
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

        if (null == metricTimer) {
            metricTimer = METRICS.timer(name(this.getClass(), "handle-time"));
            metricHandleTimes = METRICS.histogram(name(this.getClass(), "handle-times"));
            metricHandles = METRICS.meter(name(this.getClass(), "handles"));
            metricErrors = METRICS.meter(name(this.getClass(), "errors"));
        }

		final com.viadeo.kasper.event.EventMessage<E> message = new DefaultEventMessage(eventMessage);

        /* Start timer */
        final Timer.Context timer = metricTimer.time();

        /* Handle event */
        try {
            try {
                this.handle(message);
            } catch (final UnsupportedOperationException e) {
                this.handle((E) eventMessage.getPayload());
            }
        } catch (final RuntimeException e) {
            METRICLASSERRORS.mark();
            metricErrors.mark();
            throw e;
        } finally {
            /* Stop timer and record a tick */
            final long time = timer.stop();
            METRICLASSHANDLETIMES.update(time);
            metricHandleTimes.update(time);
            METRICLASSHANDLES.mark();
            metricHandles.mark();
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
