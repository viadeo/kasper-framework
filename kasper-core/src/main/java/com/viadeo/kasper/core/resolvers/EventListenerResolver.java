// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.api.component.Domain;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.exception.KasperException;
import com.viadeo.kasper.common.tools.ReflectionGenericsResolver;
import com.viadeo.kasper.core.component.annotation.XKasperEventListener;
import com.viadeo.kasper.core.component.event.listener.EventListener;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

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

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperEventListener eventAnnotation = clazz.getAnnotation(XKasperEventListener.class);

        if (null != eventAnnotation) {
            final Class<? extends Domain> domain = eventAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    @Override
    public String getLabel(final Class<? extends EventListener> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("QueryEventListener", "")
                .replace("CommandEventListener", "")
                .replace("QueryListener", "")
                .replace("CommandListener", "")
                .replace("EventListener", "")
                .replace("Listener", "");
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends EventListener> clazz) {
        final XKasperEventListener annotation =
                checkNotNull(clazz).getAnnotation(XKasperEventListener.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s event listener", this.getLabel(clazz));
        }

        return description;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Event> getEventClass(final Class<? extends EventListener> clazz) {

        if (cacheEvents.containsKey(checkNotNull(clazz))) {
            return cacheEvents.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Event>> eventClazz =
                (Optional<Class<? extends Event>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                EventListener.class,
                                EventListener.EVENT_PARAMETER_POSITION
                        );

        if ( ! eventClazz.isPresent()) {
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
