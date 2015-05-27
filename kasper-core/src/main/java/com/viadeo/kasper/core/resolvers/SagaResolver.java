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
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.KasperSaga;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaResolver extends AbstractResolver<KasperSaga> {

    private static ConcurrentMap<Class, Class> cacheSagas = Maps.newConcurrentMap();

    @Override
    public String getTypeName() {
        return "Saga";
    }

    @Override
    public Optional<Class<? extends Domain>> getDomainClass(Class<? extends KasperSaga> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperSaga sagaAnnotation = clazz.getAnnotation(XKasperSaga.class);

        if (null != sagaAnnotation) {
            final Class<? extends Domain> domain = sagaAnnotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    @Override
    public String getLabel(Class<? extends KasperSaga> clazz) {
        return checkNotNull(clazz).getSimpleName()
                .replace("Saga", "");
    }

    @Override
    public String getDescription(Class<? extends KasperSaga> clazz) {
        final XKasperSaga annotation =
                checkNotNull(clazz).getAnnotation(XKasperSaga.class);

        String description = "";
        if (null != annotation) {
            description = annotation.description();
        }
        if (description.isEmpty()) {
            description = String.format("The %s saga", this.getLabel(clazz));
        }

        return description;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    public Class<? extends Event> getEventClass(final Class<? extends KasperSaga> clazz) {

        if (cacheSagas.containsKey(checkNotNull(clazz))) {
            return cacheSagas.get(clazz);
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends Event>> eventClazz =
                (Optional<Class<? extends Event>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                KasperSaga.class,
                                KasperSaga.PARAMETER_EVENT_POSITION
                        );

        if ( ! eventClazz.isPresent()) {
            throw new KasperException("Unable to find event type for Saga " + clazz.getClass());
        }

        cacheSagas.put(clazz, eventClazz.get());

        return eventClazz.get();
    }

    // ------------------------------------------------------------------------

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        cacheSagas.clear();
    }
}
