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
import com.viadeo.kasper.event.annotation.XKasperEventListener;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

public class EventListenerResolver extends AbstractResolver<EventListener> {

    private static ConcurrentMap<Class, Class> cacheEvents = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "EventListener";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends EventListener> clazz) {

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final XKasperEventListener eventAnnotation = clazz.getAnnotation(XKasperEventListener.class);

        if (null != eventAnnotation) {
            final Class<? extends Domain> domain = eventAnnotation.domain();
            cacheDomains.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        } else {
            throw new KasperException("Event event is not decorated : " + clazz.getName());
        }
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Event> getEventClass(final Class<? extends EventListener> clazz) {

        if (cacheEvents.containsKey(clazz)) {
            return cacheEvents.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Event>> eventClazz =
                (Optional<Class<? extends Event>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz, EventListener.class, EventListener.EVENT_PARAMETER_POSITION);

        if (!eventClazz.isPresent()) {
            throw new KasperException("Unable to find event type for listener " + clazz.getClass());
        }

        cacheEvents.put(clazz, eventClazz.get());

        return eventClazz.get();
    }

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        cacheEvents.clear();
    }

}
