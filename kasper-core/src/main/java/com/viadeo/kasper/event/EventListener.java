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
import com.google.common.base.Preconditions;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import com.viadeo.kasper.context.Context;
import com.viadeo.kasper.context.impl.DefaultContextBuilder;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(EventListener.class);

    /**
     * Generic parameter position for the listened event
     */
    public static final int EVENT_PARAMETER_POSITION = 0;

    private static final String GLOBAL_METER_HANDLES_NAME = name(EventListener.class, "handles");
    private static final String GLOBAL_METER_ERRORS_NAME = name(EventListener.class, "errors");

	private final Class<E> eventClass;
    private final String timerHandleTimeName;
    private final String meterErrorsName;
    private final String domainMeterErrorsName;
    private final String domainTimerHandleTimesName;

    private EventBus eventBus;

	// ------------------------------------------------------------------------
	
	public EventListener() {
		@SuppressWarnings("unchecked")
		final Optional<Class<E>> eventClassOpt =
				(Optional<Class<E>>)
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
	
	public Class<E> getEventClass() {
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
        if ( ! getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
            return;
        }

        final EventResponse response = handleWithResponse(eventMessage);

        if (response.isAnError() || response.isAFailure()) {
            final Optional<Exception> optionalException = response.getReason().getException();
            final RuntimeException exception;
            final String message = String.format(
                    "Failed to handle event %s, <event=%s> <response=%s>",
                    eventMessage.getPayloadType(), eventMessage.getPayload(), response
            );

            if (optionalException.isPresent()) {
                exception = new RuntimeException(message, optionalException.get());
            } else {
                exception = new RuntimeException(message);
            }

            throw exception;
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings({"unchecked", "rawtypes"}) // Safe
    public final EventResponse handleWithResponse(final org.axonframework.domain.EventMessage eventMessage) {
        Preconditions.checkNotNull(eventMessage);

		if ( ! getEventClass().isAssignableFrom(eventMessage.getPayloadType())) {
			return EventResponse.error(new KasperReason(
                    CoreReasonCode.INVALID_INPUT,
                    String.format("Unexpected event : '%s' is not a '%s'", eventMessage.getPayloadType(), getEventClass())
            ));
		}

        /* Start timer */
        final Timer.Context timer = getMetricRegistry().timer(timerHandleTimeName).time();
        final Timer.Context domainTimer = getMetricRegistry().timer(domainTimerHandleTimesName).time();

        final EventMessage<E> message = new EventMessage(eventMessage);

        /* Ensure a context is set */
        CurrentContext.set(Objects.firstNonNull(message.getContext(), DefaultContextBuilder.get()));

        EventResponse response;

        try {
            response = this.handle(message);
        } catch (Exception e) {
            response =  EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }

        switch (response.getStatus()) {
            case FAILURE:
                getMetricRegistry().meter(GLOBAL_METER_ERRORS_NAME).mark();
                getMetricRegistry().meter(meterErrorsName).mark();
                getMetricRegistry().meter(domainMeterErrorsName).mark();
            case ERROR:
                try {
                    rollback(message);
                } catch (Exception e) {
                    LOGGER.error("Failed to rollback, <message=%s> <response=%s>", message, response, e);
                }
                break;
        }

        domainTimer.close();
        timer.stop();
        getMetricRegistry().meter(GLOBAL_METER_HANDLES_NAME).mark();

        return response;
	}

    // ------------------------------------------------------------------------

    public EventResponse handle(final EventMessage<E> message) {
        return handle(message.getContext(), message.getEvent());
    }

    // ------------------------------------------------------------------------

    public EventResponse handle(final Context context, final E event) {
        return EventResponse.success();
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
