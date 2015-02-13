// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.codahale.metrics.Timer;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.KasperResponse;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.viadeo.kasper.core.metrics.KasperMetrics.getMetricRegistry;
import static com.viadeo.kasper.core.metrics.KasperMetrics.name;

/**
 *
 * Base implementation for Kasper event listeners
 *
 * @param <E> Event
 */
public abstract class EventListener<E extends Event> implements org.axonframework.eventhandling.EventListener {

    /**
     * Generic parameter position for the listened event
     */
    public static final int EVENT_PARAMETER_POSITION = 0;

    private static final String GLOBAL_METER_HANDLES_NAME = name(EventListener.class, "handles");
    private static final String GLOBAL_METER_ERRORS_NAME = name(EventListener.class, "errors");

	private final Class<? extends Event> eventClass;
    private final String timerHandleTimeName;
    private final String meterErrorsName;
    private final String domainMeterErrorsName;
    private final String domainTimerHandleTimesName;

    private EventBus eventBus;

	// ------------------------------------------------------------------------
	
	public EventListener() {
		@SuppressWarnings("unchecked")
		final Optional<Class<? extends Event>> eventClassOpt =
				(Optional<Class<? extends Event>>)
				ReflectionGenericsResolver.getParameterTypeFromClass(
						this.getClass(),
                        EventListener.class,
                        EventListener.EVENT_PARAMETER_POSITION
                );
		
		if (eventClassOpt.isPresent()) {
			this.eventClass = eventClassOpt.get();
		} else {
			throw new KasperException("Unable to identify event class for " + this.getClass());
		}

        this.timerHandleTimeName = name(this.getClass(), "handle-time");
        this.meterErrorsName = name(this.getClass(), "errors");

        this.domainTimerHandleTimesName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "handle-time");
        this.domainMeterErrorsName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "errors");
	}
	
	// ------------------------------------------------------------------------
	
	public Class<? extends Event> getEventClass() {
		return this.eventClass;
	}

    public Context getContext() {
        if (CurrentContext.value().isPresent()) {
            return CurrentContext.value().get();
        }
        throw new KasperException("Unexpected condition : no context was set during event handling");
    }

	// ------------------------------------------------------------------------

    /**
     * Publish an event on the event bus
     *
     * @param event The event
     */
    public void publish(final Event event) {
        checkNotNull(event, "The specified event must be non null");
        checkState(null != eventBus, "Unable to publish the specified event : the event bus is null");

        org.axonframework.domain.EventMessage eventMessage = GenericEventMessage.asEventMessage(event);
        this.eventBus.publish(eventMessage);
    }

    // ------------------------------------------------------------------------
	
	/**
	 * Wrapper for Axon event messages
	 * 
	 * @see org.axonframework.eventhandling.EventListener#handle(org.axonframework.domain.EventMessage)
	 */
    @Override
	public final void handle(final org.axonframework.domain.EventMessage eventMessage) {
        handleWithResponse(eventMessage);
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"}) // Safe
    public final EventResponse handleWithResponse(final org.axonframework.domain.EventMessage eventMessage) {
		if ( ! this.getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
			return EventResponse.ignored();
		}

        final EventMessage message = new EventMessage(eventMessage);

        /* Start timer */
        final Timer.Context timer = getMetricRegistry().timer(timerHandleTimeName).time();
        final Timer.Context domainTimer = getMetricRegistry().timer(domainTimerHandleTimesName).time();

        /* Ensure a context is set */
        CurrentContext.set(Objects.firstNonNull(message.getContext(), DefaultContextBuilder.get()));

        /* Handle event */
        try {
            final EventResponse response = this.handle(message);
            if (response.getStatus() == KasperResponse.Status.ROLLBACK) {
                rollback(message);
            }
            return response;

        } catch (final RuntimeException e) {
            getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
            getMetricRegistry().meter(meterErrorsName).mark();
            getMetricRegistry().meter(domainMeterErrorsName).mark();
            throw e;

        } finally {
            /* Stop timer and record a tick */
            domainTimer.close();
            timer.stop();

            getMetricRegistry().meter(GLOBAL_METER_HANDLES_NAME).mark();
        }
	}

    // ------------------------------------------------------------------------

    public EventResponse handle(final EventMessage<E> message) {
        return handle(message.getContext(), message.getEvent());
    }

    // ------------------------------------------------------------------------

    public EventResponse handle(final Context context, final E event) {
        return EventResponse.ignored();
    }

    // ------------------------------------------------------------------------

    public void rollback(final EventMessage<E> message) {
        rollback(message.getContext(), message.getEvent());
    }

    // ------------------------------------------------------------------------

    public void rollback(final Context context, final E event) { }

    // ------------------------------------------------------------------------

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

}
