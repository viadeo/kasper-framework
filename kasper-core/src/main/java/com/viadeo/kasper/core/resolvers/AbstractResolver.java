// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.viadeo.kasper.ddd.Domain;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractResolver implements Resolver {

    private DomainResolver domainResolver;

    // ------------------------------------------------------------------------

    @Override
    public Optional<String> getDomainLabel(final Class<?> clazz) {

        final Optional<Class<? extends Domain>> domain = this.getDomain(clazz);
        if (domain.isPresent()) {
            return domainResolver.getLabel(domain.get());
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setDomainResolver(final DomainResolver domainResolver) {
        this.domainResolver = checkNotNull(domainResolver);
    }

}
