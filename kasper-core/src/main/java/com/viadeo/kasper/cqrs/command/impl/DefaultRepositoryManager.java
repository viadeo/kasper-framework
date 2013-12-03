package com.viadeo.kasper.cqrs.command.impl;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.ddd.AggregateRoot;
import com.viadeo.kasper.ddd.IRepository;
import com.viadeo.kasper.ddd.repository.ClientRepository;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.exception.KasperException;

import java.util.Map;

public class DefaultRepositoryManager implements RepositoryManager {
    private final Map<Class, Repository> repositoryByAggregateClass;

    public DefaultRepositoryManager() {
        this.repositoryByAggregateClass = Maps.newHashMap();
    }

    @Override
    public void register(Repository repository) {
        Preconditions.checkNotNull(repository);
        if (!repository.isInitialized()){
            throw new KasperException("The repository isn't initialized : " + repository.getClass().getName());
        }
        repositoryByAggregateClass.put(repository.getAggregateClass(), repository);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <E extends AggregateRoot> Optional<ClientRepository<E>> getEntityRepository(Class<E> aggregateClass) {
        Preconditions.checkNotNull(aggregateClass);
        IRepository<E> repository = repositoryByAggregateClass.get(aggregateClass);
        if(repository == null){
            return Optional.absent();
        }
        return Optional.of(new ClientRepository<E>(repository));
    }

    public boolean isRegistered(Repository repository){
        return repositoryByAggregateClass.values().contains(repository);
    }

}
