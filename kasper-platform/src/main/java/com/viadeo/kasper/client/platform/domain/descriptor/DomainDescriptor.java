package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import java.util.Collection;

public class DomainDescriptor {

    private final Class domainClass;
    private final ImmutableList<QueryHandlerDescriptor> queryHandlerDescriptors;
    private final ImmutableList<CommandHandlerDescriptor> commandHandlerDescriptors;
    private final ImmutableList<RepositoryDescriptor> repositoryDescriptor;
    private final ImmutableList<EventListenerDescriptor> eventListenerDescriptor;

    public DomainDescriptor(Class domainClass
            , ImmutableList<QueryHandlerDescriptor> queryHandlerDescriptors
            , ImmutableList<CommandHandlerDescriptor> commandHandlerDescriptors
            , ImmutableList<RepositoryDescriptor> repositoryDescriptor
            , ImmutableList<EventListenerDescriptor> eventListenerDescriptor) {
        this.domainClass = Preconditions.checkNotNull(domainClass);
        this.queryHandlerDescriptors = Preconditions.checkNotNull(queryHandlerDescriptors);
        this.commandHandlerDescriptors = Preconditions.checkNotNull(commandHandlerDescriptors);
        this.repositoryDescriptor = Preconditions.checkNotNull(repositoryDescriptor);
        this.eventListenerDescriptor = Preconditions.checkNotNull(eventListenerDescriptor);
    }

    public Class getDomainClass() {
        return domainClass;
    }

    public ImmutableList<QueryHandlerDescriptor> getQueryHandlerDescriptors() {
        return queryHandlerDescriptors;
    }

    public Collection<CommandHandlerDescriptor> getCommandHandlerDescriptors() {
        return commandHandlerDescriptors;
    }

    public Collection<RepositoryDescriptor> getRepositoryDescriptors() {
        return repositoryDescriptor;
    }

    public Collection<EventListenerDescriptor> getEventListenerDescriptors() {
        return eventListenerDescriptor;
    }
}