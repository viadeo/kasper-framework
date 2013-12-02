package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.ddd.Domain;

import java.util.Collection;

public class DomainDescriptor implements Descriptor {

    private final String domainName;
    private final Class<? extends Domain> domainClass;
    private final ImmutableList<QueryHandlerDescriptor> queryHandlerDescriptors;
    private final ImmutableList<CommandHandlerDescriptor> commandHandlerDescriptors;
    private final ImmutableList<RepositoryDescriptor> repositoryDescriptor;
    private final ImmutableList<EventListenerDescriptor> eventListenerDescriptor;

    public DomainDescriptor(
              String domainName
            , Class<? extends Domain> domainClass
            , Collection<QueryHandlerDescriptor> queryHandlerDescriptors
            , Collection<CommandHandlerDescriptor> commandHandlerDescriptors
            , Collection<RepositoryDescriptor> repositoryDescriptor
            , Collection<EventListenerDescriptor> eventListenerDescriptor) {
        this.domainName = domainName;
        this.domainClass = Preconditions.checkNotNull(domainClass);
        this.queryHandlerDescriptors = ImmutableList.copyOf(Preconditions.checkNotNull(queryHandlerDescriptors));
        this.commandHandlerDescriptors = ImmutableList.copyOf(Preconditions.checkNotNull(commandHandlerDescriptors));
        this.repositoryDescriptor = ImmutableList.copyOf(Preconditions.checkNotNull(repositoryDescriptor));
        this.eventListenerDescriptor = ImmutableList.copyOf(Preconditions.checkNotNull(eventListenerDescriptor));
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

    @Override
    public Class<? extends Domain> getReferenceClass() {
        return domainClass;
    }

    public String getName(){
        return domainName;
    }
}