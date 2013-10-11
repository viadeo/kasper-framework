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

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractResolver<T> implements Resolver<T> {

    private DomainResolver domainResolver;

    protected static ConcurrentMap<Class, Class> cacheDomains = Maps.newConcurrentMap();

    // ------------------------------------------------------------------------

    @Override
    public Optional<String> getDomainLabel(final Class<? extends T> clazz) {

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

    // ------------------------------------------------------------------------

    @Override
    public void clearCache() {
        // FIXME: only clear keys related to an assignable class via identification
        //        of the generic
        cacheDomains.clear();
    }

}
