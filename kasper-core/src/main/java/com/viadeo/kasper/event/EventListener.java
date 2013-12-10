// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class EventListener<E extends IEvent> implements org.axonframework.eventhandling.EventListener {

    /**
     * Generic parameter position for the listened event
     */
    public static final int EVENT_PARAMETER_POSITION = 0;

    private static final String GLOBAL_HISTO_HANDLE_TIMES_NAME = name(EventListener.class, "handle-times");
    private static final String GLOBAL_METER_HANDLES_NAME = name(EventListener.class, "handles");
    private static final String GLOBAL_METER_ERRORS_NAME = name(EventListener.class, "errors");

	private final Class<? extends IEvent> eventClass;
    private final String timerHandleTimeName;
    private final String meterErrorsName;
    private final String meterHandlesName;
    private final String histoHandleTimesName;

    private CommandGateway commandGateway;

	// ------------------------------------------------------------------------
	
	public EventListener() {
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

        this.timerHandleTimeName = name(this.getClass(), "handle-time");
        this.histoHandleTimesName = name(this.getClass(), "handle-times");
        this.meterHandlesName = name(this.getClass(), "handles");
        this.meterErrorsName = name(this.getClass(), "errors");
	}
	
	// ------------------------------------------------------------------------
	
	public Class<? extends IEvent> getEventClass() {
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
	public void handle(final org.axonframework.domain.EventMessage eventMessage) {
		
		if (!this.getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
			return;
		}

		final com.viadeo.kasper.event.EventMessage<E> message = new EventMessage(eventMessage);

        /* Start timer */
        final Timer.Context timer = getMetricRegistry().timer(timerHandleTimeName).time();

        /* Handle event */
        try {
            try {
                this.handle(message);
            } catch (final UnsupportedOperationException e) {
                this.handle((E) eventMessage.getPayload());
            }
        } catch (final RuntimeException e) {
            getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
            getMetricRegistry().meter(meterErrorsName).mark();
            throw e;
        } finally {
            /* Stop timer and record a tick */
            final long time = timer.stop();

            getMetricRegistry().histogram(GLOBAL_HISTO_HANDLE_TIMES_NAME).update(time);
            getMetricRegistry().meter(GLOBAL_METER_HANDLES_NAME).mark();

            getMetricRegistry().histogram(histoHandleTimesName).update(time);
            getMetricRegistry().meter(meterHandlesName).mark();
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
