// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.google.common.base.Optional;
import com.viadeo.kasper.CoreReasonCode;
import com.viadeo.kasper.KasperReason;
import org.axonframework.domain.EventMessage;

import java.util.Set;

public abstract class AxonEventListener<EVENT extends Event> implements org.axonframework.eventhandling.EventListener {

    @Override
    public final void handle(final EventMessage eventMessage) {
        if ( ! acceptMessage(eventMessage)) {
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

    public final EventResponse handleWithResponse(final EventMessage eventMessage) {
        if ( ! acceptMessage(eventMessage)) {
            return EventResponse.error(new KasperReason(
                    CoreReasonCode.INVALID_INPUT,
                    String.format("Unexpected event : '%s' is not a '%s'", eventMessage.getPayloadType(), getEventClasses())
            ));
        }
        return this.handle(new com.viadeo.kasper.event.EventMessage<EVENT>(eventMessage));
    }

    public boolean acceptMessage(final EventMessage eventMessage) {
        final Class payloadType = eventMessage.getPayloadType();
        for (final Class<?> eventClass : getEventClasses()) {
            if (eventClass != null && eventClass.isAssignableFrom(payloadType)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    public abstract EventResponse handle(final com.viadeo.kasper.event.EventMessage<EVENT> message);

    public abstract Set<Class<?>> getEventClasses();
}
