// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.event.listener;

import com.google.common.base.Optional;
import com.google.common.collect.Sets;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.event.EventResponse;
import com.viadeo.kasper.api.context.Context;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;

import java.util.Set;

public class BaseEventListener<EVENT extends Event>
    extends AxonEventListener<EVENT>
    implements EventListener<EVENT>
{

    private final Class<EVENT> eventClass;

    public BaseEventListener() {
        super();
        eventClass = init();
    }

    protected Class<EVENT> init() {
        @SuppressWarnings("unchecked")
        final Optional<Class<EVENT>> eventClassOpt =
                (Optional<Class<EVENT>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                this.getClass(),
                                EventListener.class,
                                EventListener.EVENT_PARAMETER_POSITION
                        );

        if (eventClassOpt.isPresent()) {
            return eventClassOpt.get();
        } else {
            throw new KasperException("Unable to identify event class for " + this.getClass());
        }
    }

    @Override
    public EventResponse handle(final EventMessage<EVENT> message) {
        return handle(message.getContext(), message.getEvent());
    }

    public EventResponse handle(final Context context, final EVENT event) {
        throw new UnsupportedOperationException("not yet implemented!");
    }


    @Override
    public void rollback(final EventMessage<EVENT> message) {
        rollback(message.getContext(), message.getInput());
    }

    public void rollback(final Context context, final Event event) {
        // nothing
    }

    @Override
    public Class<EVENT> getInputClass() {
        return eventClass;
    }

    @Override
    public Class<?> getHandlerClass() {
        return getClass();
    }

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public Set<EventDescriptor> getEventDescriptors() {
        return Sets.newHashSet(new EventDescriptor(this.eventClass, getClass().isAnnotationPresent(Deprecated.class)));
    }

    @Override
    public String toString() {
        return getName();
    }
}
