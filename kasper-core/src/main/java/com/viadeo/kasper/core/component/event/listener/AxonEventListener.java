// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.context.Contexts;
import com.viadeo.kasper.api.response.CoreReasonCode;
import com.viadeo.kasper.api.response.KasperReason;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AxonEventListener<EVENT extends Event> implements org.axonframework.eventhandling.EventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AxonEventListener.class);

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
                    String.format("Unexpected event : '%s' is not a '%s'", eventMessage.getPayloadType(), getEventDescriptors())
            ));
        }

        @SuppressWarnings("unchecked")
        final EventMessage<EVENT> message = new EventMessage<EVENT>(eventMessage);
        final EVENT event = message.getEvent();
        final Context context = Objects.firstNonNull(message.getContext(), Contexts.empty());

        EventResponse response;

        try {
            response = this.handle(message);
        } catch (Exception e) {
            response =  EventResponse.failure(new KasperReason(CoreReasonCode.INTERNAL_COMPONENT_ERROR, e));
        }

        switch (response.getStatus()) {
            case FAILURE:
            case ERROR:
                try {
                    rollback(message);
                } catch (Exception e) {
                    LOGGER.error("Failed to rollback, <context=%s> <event=%s> <response=%s>", context, event, response, e);
                }
                break;
        }

        return response;
    }

    @SuppressWarnings("unchecked")
    public boolean acceptMessage(final org.axonframework.domain.EventMessage eventMessage) {
        checkNotNull(eventMessage);
        final Class payloadType = eventMessage.getPayloadType();
        for (final EventDescriptor eventDescriptor : getEventDescriptors()) {
            if (eventDescriptor != null && eventDescriptor.getEventClass().isAssignableFrom(payloadType)) {
                return Boolean.TRUE;
            }
        }
        return Boolean.FALSE;
    }

    // ------------------------------------------------------------------------

    public abstract EventResponse handle(EventMessage<EVENT> message);

    public abstract void rollback(EventMessage<EVENT> message);

    public abstract Set<EventDescriptor> getEventDescriptors();

}
