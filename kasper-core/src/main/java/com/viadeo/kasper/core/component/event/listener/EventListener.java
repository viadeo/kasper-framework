// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.core.component.Handler;

import java.util.Set;

public interface EventListener<EVENT extends Event> extends Handler<EventResponse, EVENT>, org.axonframework.eventhandling.EventListener {

    /**
     * Generic parameter position for the listened event
     */
    public static final int EVENT_PARAMETER_POSITION = 0;

    @Override
    EventResponse handle(Context context, EVENT event);

    String getName();

    Set<Class<?>> getEventClasses();

}
