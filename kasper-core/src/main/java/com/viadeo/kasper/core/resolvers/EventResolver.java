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
import com.viadeo.kasper.event.domain.DomainEvent;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

public class EventResolver extends AbstractResolver {

    @Override
    public String getTypeName() {
        return "Event";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomain(Class clazz) {

        /* Force events to be DomainEvents for domain resolution */
        if ( ! DomainEvent.class.isAssignableFrom(clazz)) {
            return Optional.absent();
        }

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        final Optional<Class<? extends Domain>> domainClazz =
                (Optional<Class<? extends Domain>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                                clazz,
                                DomainEvent.class,
                                DomainEvent.DOMAIN_PARAMETER_POSITION);

        if (!domainClazz.isPresent()) {
            throw new KasperException("Unable to find domain type for domain event " + clazz.getClass());
        }

        if (domainClazz.isPresent()) {
            cacheDomains.put(clazz, domainClazz.get());
        }

        return domainClazz;
    }

}
