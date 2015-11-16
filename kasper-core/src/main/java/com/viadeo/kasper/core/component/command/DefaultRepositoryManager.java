// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.core.component.command;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;
import com.viadeo.kasper.core.component.command.repository.Repository;

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
        repositoryByAggregateClass.put(repository.getAggregateClass(), repository);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <REPO extends Repository> Optional<REPO> getEntityRepository(final Class aggregateClass) {
        checkNotNull(aggregateClass);
        final Repository repository = repositoryByAggregateClass.get(aggregateClass);

        if (null == repository) {
            return Optional.absent();
        }

        return Optional.of((REPO)repository);
    }

    public boolean isRegistered(final Repository repository){
        return repositoryByAggregateClass.values().contains(repository);
    }

}
