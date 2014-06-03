package org.axonframework.eventhandling.amqp;

import org.axonframework.domain.EventMessage;

/**
 * RoutingKeyResolver implementation that uses the package name of the Message's payload as routing key.
 *
 * @author Allard Buijze
 * @since 2.0
 */
public class PackageRoutingKeyResolver implements RoutingKeyResolver {

    @Override
    public String resolveRoutingKey(EventMessage event) {
        return event.getPayloadType().getPackage().getName();
    }
}