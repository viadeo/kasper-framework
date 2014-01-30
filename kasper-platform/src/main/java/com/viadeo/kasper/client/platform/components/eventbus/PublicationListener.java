// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.components.eventbus;

import org.axonframework.domain.EventMessage;

public interface PublicationListener {

    /**
     * Invoked when an event message is published on the bus.
     * @param eventMessage the event message
     */
    void eventMessagePublished(EventMessage eventMessage);

}
