package com.viadeo.kasper.client.platform.domain.descriptor;

public class RepositoryDescriptor {
    private final Class repositoryClass;
    private final AggregateDescriptor aggregateDescriptor;

    public RepositoryDescriptor(Class repositoryClass, AggregateDescriptor aggregateDescriptor) {
        this.repositoryClass = repositoryClass;
        this.aggregateDescriptor = aggregateDescriptor;
    }

    public Class getReferenceClass() {
        return repositoryClass;
    }

    public AggregateDescriptor getAggregateDescriptor() {
        return aggregateDescriptor;
    }

}