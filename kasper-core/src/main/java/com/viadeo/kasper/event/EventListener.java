// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.codahale.metrics.Timer;
import com.google.common.base.Optional;
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
    private final String domainMeterErrorsName;
    private final String domainMeterHandlesName;
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
        this.histoHandleTimesName = name(this.getClass(), "handle-times");
        this.meterHandlesName = name(this.getClass(), "handles");
        this.meterErrorsName = name(this.getClass(), "errors");

        this.domainTimerHandleTimesName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "handle-time");
        this.domainMeterHandlesName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "handles");
        this.domainMeterErrorsName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "errors");
	}
	
	// ------------------------------------------------------------------------
	
	public Class<? extends IEvent> getEventClass() {
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
    public void publish(final IEvent event) {
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
	@SuppressWarnings({"unchecked", "rawtypes"}) // Safe
	public void handle(final org.axonframework.domain.EventMessage eventMessage) {
		
		if ( ! this.getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
			return;
		}

		final com.viadeo.kasper.event.EventMessage<E> message = new EventMessage(eventMessage);

        /* Start timer */
        final Timer.Context timer = getMetricRegistry().timer(timerHandleTimeName).time();
        final Timer.Context domainTimer = getMetricRegistry().timer(domainTimerHandleTimesName).time();

        /* Ensure a context is set */
        final Context messageContext = message.getContext();
        if (null != messageContext) {
            CurrentContext.set(messageContext);
        } else {
            /* Reset the current context in thread */
            CurrentContext.set(DefaultContextBuilder.get());
        }

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
            getMetricRegistry().meter(domainMeterErrorsName).mark();
            throw e;

        } finally {
            /* Stop timer and record a tick */
            domainTimer.close();
            final long time = timer.stop();

            getMetricRegistry().histogram(GLOBAL_HISTO_HANDLE_TIMES_NAME).update(time);
            getMetricRegistry().meter(GLOBAL_METER_HANDLES_NAME).mark();

            getMetricRegistry().histogram(histoHandleTimesName).update(time);
            getMetricRegistry().meter(meterHandlesName).mark();
            getMetricRegistry().meter(domainMeterHandlesName).mark();
        }
	}
	
	/**
	 * @param eventMessage the Kasper event message to handle
	 */
	public void handle(final com.viadeo.kasper.event.EventMessage<E> eventMessage){
		throw new UnsupportedOperationException();
	}

	/**
	 * @param event the Kasper event to handle
	 */
	public void handle(final E event) {
		throw new UnsupportedOperationException();
	}

    // ------------------------------------------------------------------------

    public void setEventBus(final EventBus eventBus) {
        this.eventBus = checkNotNull(eventBus);
    }

}
