// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.event.annotation.XKasperSaga;
import com.viadeo.kasper.event.saga.Saga;

import static com.google.common.base.Preconditions.checkNotNull;

public class SagaResolver extends AbstractResolver<Saga> {

    @Override
    public String getTypeName() {
        return "Saga";
    }

    @Override
    public Optional<Class<? extends Domain>> getDomainClass(final Class<? extends Saga> clazz) {
        if (DOMAINS_CACHE.containsKey(checkNotNull(clazz))) {
            return Optional.<Class<? extends Domain>>of(DOMAINS_CACHE.get(clazz));
        }

        final XKasperSaga annotation = clazz.getAnnotation(XKasperSaga.class);

        if (null != annotation) {
            final Class<? extends Domain> domain = annotation.domain();
            DOMAINS_CACHE.put(clazz, domain);
            return Optional.<Class<? extends Domain>>of(domain);
        }

        return Optional.absent();
    }

    @Override
    public String getLabel(final Class<? extends Saga> clazz) {
        return checkNotNull(clazz).getSimpleName().replace("Saga", "");
    }

    @Override
    public String getDescription(final Class<? extends Saga> clazz) {
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

}
