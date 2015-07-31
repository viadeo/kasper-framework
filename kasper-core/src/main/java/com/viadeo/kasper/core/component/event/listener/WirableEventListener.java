// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.viadeo.kasper.api.component.event.Event;
import org.axonframework.eventhandling.EventBus;

/**
 * A class implements this interface in order to have the capability to be auto wired with the platform components.
 *
 * @param <EVENT> the handled event class
 *
 * @see EventListener
 */
public interface WirableEventListener<EVENT extends Event> extends EventListener<EVENT> {

    /**
     * Wires an event bus on this <code>EventListener</code> instance.
     * @param eventBus an event bus
     */
    void setEventBus(final EventBus eventBus);
}
