// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.api.domain.event.Event;
import com.viadeo.kasper.api.documentation.XKasperEvent;
import com.viadeo.kasper.api.domain.event.DomainEvent;
import com.viadeo.kasper.api.domain.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import static com.google.common.base.Preconditions.checkNotNull;

public class EventResolver extends AbstractResolver<Event> {

    @Override
    public String getTypeName() {
        return "Event";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Event> clazz) {

        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final Optional<Class<? extends Domain>> domainClazz;

        if (DomainEvent.class.isAssignableFrom(clazz)) {
            domainClazz = (Optional<Class<? extends Domain>>)
                    ReflectionGenericsResolver.getParameterTypeFromClass(
                            clazz,
                            DomainEvent.class,
                            DomainEvent.DOMAIN_PARAMETER_POSITION
                    );

            if ( ! domainClazz.isPresent()) {
                throw new KasperException("Unable to find domain type for domain event " + clazz.getClass());
            }

        } else if (null == domainResolver) {
            domainClazz = Optional.absent();
        } else {
           domainClazz = domainResolver.getDomainClassOf(clazz);
        }

        if (domainClazz.isPresent()) {
            DOMAINS_CACHE.put(clazz, domainClazz.get());
        }

        return domainClazz;
    }

    @Override
    public String getLabel(final Class<? extends Event> clazz) {
        return checkNotNull(clazz).getSimpleName().replace("Event", "");
    }

    // ------------------------------------------------------------------------

    @Override
    public String getDescription(final Class<? extends Event> eventClazz) {
        final XKasperEvent annotation =
                checkNotNull(eventClazz).getAnnotation(XKasperEvent.class);

        String description = "";

        if (null != annotation) {
            description = annotation.description();
        }

        if (description.isEmpty()) {
            description = String.format("The %s event", this.getLabel(eventClazz));
        }

        return description;
    }

    // ------------------------------------------------------------------------

    public String getAction(final Class<? extends Event> eventClazz) {
        final XKasperEvent annotation =
                checkNotNull(eventClazz).getAnnotation(XKasperEvent.class);

        String action = "";
        if (null != annotation) {
            action = annotation.action();
        }

        return action;
    }

}
