// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.resolvers;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.Domain;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.impl.Repository;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.tools.ReflectionGenericsResolver;

import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.Preconditions.checkNotNull;

public class RepositoryResolver extends AbstractResolver {

    private EntityResolver entityResolver;

    // ------------------------------------------------------------------------

    @Override
    public String getTypeName() {
        return "Repository";
    }

    // ------------------------------------------------------------------------

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Class<? extends Domain>> getDomain(Class clazz) {

        if ( ! Repository.class.isAssignableFrom(clazz)) {
            return Optional.absent();
        }

        if (cacheDomains.containsKey(clazz)) {
            return Optional.<Class<? extends Domain>>of(cacheDomains.get(clazz));
        }

        @SuppressWarnings("unchecked") // Safe
        final Optional<Class<? extends AggregateRoot>> agr =
                (Optional<Class<? extends AggregateRoot>>)
                        ReflectionGenericsResolver.getParameterTypeFromClass(
                               clazz, IRepository.class, IRepository.ENTITY_PARAMETER_POSITION);

        if (!agr.isPresent()) {
            throw new KasperException("Unable to find aggregate type for repository " + clazz.getClass());
        }

        final Optional<Class<? extends Domain>> domain = this.entityResolver.getDomain(agr.get());
        if (domain.isPresent()) {
            cacheDomains.put(clazz, domain.get());
            return domain;
        }

        return Optional.absent();
    }

    // ------------------------------------------------------------------------

    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = checkNotNull(entityResolver);
    }

}
