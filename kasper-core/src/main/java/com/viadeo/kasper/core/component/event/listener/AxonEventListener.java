// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import com.viadeo.kasper.core.context.CurrentContext;
import com.viadeo.kasper.core.metrics.MetricNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AxonEventListener<EVENT extends Event> implements org.axonframework.eventhandling.EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxonEventListener.class);

    private final MetricRegistry metricRegistry;
    private final MetricNames globalMetricNames;
    private final MetricNames inputMetricNames;
    private final MetricNames domainMetricNames;

    public AxonEventListener(final MetricRegistry metricRegistry) {
        this.metricRegistry = checkNotNull(metricRegistry);
        this.globalMetricNames = MetricNames.of(EventListener.class, "errors", "handles", "handle-time");
        this.inputMetricNames = MetricNames.of(getClass(), "errors", "handles", "handle-time");
        this.domainMetricNames = MetricNames.byDomainOf(getClass(), "errors", "handles", "handle-time");
    }

    @Override
    public final void handle(final org.axonframework.domain.EventMessage eventMessage) {
        if ( ! acceptMessage(eventMessage)) {
            return;
        }

        final EventResponse response = handleWithResponse(eventMessage);

        final String message = String.format(
                "Failed to handle event %s, <event=%s> <response=%s>",
                eventMessage.getPayloadType(), eventMessage.getPayload(), response
        );


        if ( (response.isAnError() || response.isAFailure()) && response.hasReason()) {
            final Optional<Exception> optionalException = response.getReason().getException();

            RuntimeException exception = new RuntimeException(message);

            if (optionalException.isPresent()) {
                exception = new RuntimeException(message, optionalException.get());
            }

            throw exception;
        }

    }

    public final EventResponse handleWithResponse(final org.axonframework.domain.EventMessage eventMessage) {
        if ( ! acceptMessage(eventMessage)) {
            return EventResponse.error(new KasperReason(
                    CoreReasonCode.INVALID_INPUT,
                    String.format("Unexpected event : '%s' is not a '%s'", eventMessage.getPayloadType(), getEventClasses())
            ));
        }

        metricRegistry.meter(globalMetricNames.requests).mark();
        metricRegistry.meter(inputMetricNames.requests).mark();
        metricRegistry.meter(domainMetricNames.requests).mark();

        final Timer.Context inputTimer = metricRegistry.timer(inputMetricNames.requestsTime).time();
        final Timer.Context domainTimer = metricRegistry.timer(domainMetricNames.requestsTime).time();
        final Timer.Context globalTimer = metricRegistry.timer(globalMetricNames.requestsTime).time();

        final EventMessage<EVENT> kmessage = new EventMessage<EVENT>(eventMessage);
        final EVENT event = kmessage.getEvent();
        final Context context = Objects.firstNonNull(kmessage.getContext(), Contexts.empty());

        CurrentContext.set(context);

        EventResponse response;

        try {
            response = this.handle(context, event);
        } catch (Exception e) {
            response =  EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        } finally {
            inputTimer.stop();
            domainTimer.stop();
            globalTimer.stop();
        }

        switch (response.getStatus()) {
            case FAILURE:
                metricRegistry.meter(globalMetricNames.errors).mark();
                metricRegistry.meter(inputMetricNames.errors).mark();
                metricRegistry.meter(domainMetricNames.errors).mark();
            case ERROR:
                try {
                    rollback(context, event);
                } catch (Exception e) {
                    LOGGER.error("Failed to rollback, <context=%s> <event=%s> <response=%s>", context, event, response, e);
                }
                break;
        }

        return response;
    }

    public boolean acceptMessage(final org.axonframework.domain.EventMessage eventMessage) {
        checkNotNull(eventMessage);
        final Class payloadType = eventMessage.getPayloadType();
        for (final Class<?> eventClass : getEventClasses()) {
            if (eventClass != null && eventClass.isAssignableFrom(payloadType)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    // ------------------------------------------------------------------------

    public abstract EventResponse handle(Context context, EVENT event);

    public abstract void rollback(Context context, Event event);

    public abstract Set<Class<?>> getEventClasses();

}
