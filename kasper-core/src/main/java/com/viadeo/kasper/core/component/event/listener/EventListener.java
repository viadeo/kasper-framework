// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.codahale.metrics.Timer;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.metrics.MetricNameStyle;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;
import org.axonframework.domain.GenericEventMessage;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

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
public abstract class EventListener<E extends Event>
    extends AxonEventListener<E> implements IEventListener<E> {

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
    private final String meterHandlesName;
    private final String domainMeterErrorsName;
    private final String domainTimerHandleTimesName;
    private final String domainMeterHandlesName;

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
        this.meterHandlesName = name(this.getClass(), "handles");

        this.domainTimerHandleTimesName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "handle-time");
        this.domainMeterErrorsName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "errors");
        this.domainMeterHandlesName = name(MetricNameStyle.DOMAIN_TYPE, this.getClass(), "handles");
	}

    // ------------------------------------------------------------------------

    @Override
    public String getName() {
        return getClass().getName();
    }

    // ------------------------------------------------------------------------

    @Override
    public Set<Class<?>> getEventClasses() {
        return Sets.<Class<?>>newHashSet(this.eventClass);
    }

    // ------------------------------------------------------------------------

	public Class<E> getEventClass() {
		return this.eventClass;
	}

    // ------------------------------------------------------------------------

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

    @SuppressWarnings({"unchecked", "rawtypes"}) // Safe
    @Override
    public EventResponse handle(final EventMessage<E> message) {
        Preconditions.checkNotNull(message);

        /* Start timer */
        final Timer.Context timer = getMetricRegistry().timer(timerHandleTimeName).time();
        final Timer.Context domainTimer = getMetricRegistry().timer(domainTimerHandleTimesName).time();

        /* Ensure a context is set */
        CurrentContext.set(Objects.firstNonNull(message.getContext(), Contexts.empty()));

        EventResponse response;

        try {
            response = this.handle(message.getContext(), message.getEvent());
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
        getMetricRegistry().meter(meterHandlesName).mark();
        getMetricRegistry().meter(domainMeterHandlesName).mark();

        return response;
	}

    // ------------------------------------------------------------------------

    @Override
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

    @Override
    public String toString() {
        return getName();
    }
}
