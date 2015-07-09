// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.event;

import com.viadeo.kasper.context.Context;

import java.util.Set;

public interface IEventListener<EVENT extends Event> {

    EventResponse handle(EventMessage<EVENT> message);

    EventResponse handle(Context context, EVENT event);

    String getName();

    Set<Class<?>> getEventClasses();

}
