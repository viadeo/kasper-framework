// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.api.domain.exception.KasperException;

import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

public class DefaultRepositoryManager implements RepositoryManager {

    private final Map<Class, Repository> repositoryByAggregateClass;

    // ------------------------------------------------------------------------

    public DefaultRepositoryManager() {
        this.repositoryByAggregateClass = Maps.newHashMap();
    }

    // ------------------------------------------------------------------------

    @Override
    public void register(final Repository repository) {
        checkNotNull(repository);
        if ( ! repository.isInitialized()) {
            throw new KasperException(
                    "The repository isn't initialized : "
                            + repository.getClass().getName()
            );
        }
        repositoryByAggregateClass.put(repository.getAggregateClass(), repository);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends AggregateRoot> Optional<ClientRepository<E>> getEntityRepository(final Class<E> aggregateClass) {
        checkNotNull(aggregateClass);
        final IRepository<E> repository = repositoryByAggregateClass.get(aggregateClass);

        if (null == repository) {
            return Optional.absent();
        }

        return Optional.of(new ClientRepository<>(repository));
    }

    public boolean isRegistered(final Repository repository){
        return repositoryByAggregateClass.values().contains(repository);
    }

}
