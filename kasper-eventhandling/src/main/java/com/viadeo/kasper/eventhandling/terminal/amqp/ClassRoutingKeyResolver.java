// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.eventhandling.terminal.amqp;

import org.axonframework.domain.EventMessage;
import org.axonframework.eventhandling.amqp.RoutingKeyResolver;

import static com.google.common.base.Preconditions.checkNotNull;

public class ClassRoutingKeyResolver implements RoutingKeyResolver {

    @Override
    public String resolveRoutingKey(final EventMessage eventMessage) {
        checkNotNull(eventMessage);
        return eventMessage.getPayloadType().getName();
    }
}
