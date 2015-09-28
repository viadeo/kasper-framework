// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.core.component.Handler;

import java.util.Set;

/**
 * A <code>EventListener</code> is invoked to listen an <code>Event</code>. It's very useful in order to uncouple our code.
 *
 * @param <EVENT> the event class listened by this <code>EventListener</code>.
 */
public interface EventListener<EVENT extends Event>
        extends Handler<EventMessage<EVENT>, EventResponse, EVENT>, org.axonframework.eventhandling.EventListener
{

    /**
     * Generic parameter position for the listened event
     */
    public static final int EVENT_PARAMETER_POSITION = 0;

    @Override
    EventResponse handle(EventMessage<EVENT> message);

    /**
     * @return the name of this <code>EventListener</code>
     */
    String getName();

    /**
     * @return all event descriptors handled by this <code>EventListener</code>
     */
    Set<EventDescriptor> getEventDescriptors();

}
