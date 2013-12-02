package com.viadeo.kasper.client.platform.domain.descriptor;

import com.viadeo.kasper.ddd.repository.Repository;

public class RepositoryDescriptor implements Descriptor {
    private final Class<? extends Repository> repositoryClass;
    private final AggregateDescriptor aggregateDescriptor;

    public RepositoryDescriptor(Class<? extends Repository> repositoryClass, AggregateDescriptor aggregateDescriptor) {
        this.repositoryClass = repositoryClass;
        this.aggregateDescriptor = aggregateDescriptor;
    }

    @Override
    public Class<? extends Repository> getReferenceClass() {
        return repositoryClass;
    }

    public AggregateDescriptor getAggregateDescriptor() {
        return aggregateDescriptor;
    }

}