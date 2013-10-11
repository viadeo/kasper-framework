// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.event.Event;
import com.viadeo.kasper.event.EventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

public class EventListenerResolver extends AbstractResolver {

    private static ConcurrentMap<Class, Class> cacheDomains = Maps.newConcurrentMap();

    private EventResolver eventResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "EventListener";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomain(Class<?> clazz) {

        if ( ! EventListener.class.isAssignableFrom(clazz)) {
            return Optional.absent();
        }

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Optional<Class<? extends Event>> eventClazz =
                (Optional<Class<? extends Event>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, EventListener.class, EventListener.EVENT_PARAMETER_POSITION);

        if (!eventClazz.isPresent()) {
            throw new KasperException("Unable to find event type for listener " + clazz.getClass());
        }

        final Optional<Class<? extends Domain>> eventDomain = eventResolver.getDomain(clazz);

        if (eventDomain.isPresent()) {
            cacheDomains.put(clazz, eventDomain.get());
        }

        return eventDomain;
    }

    // ------------------------------------------------------------------------

    public void setEventResolver(final EventResolver eventResolver) {
        this.eventResolver = eventResolver;
    }

}
