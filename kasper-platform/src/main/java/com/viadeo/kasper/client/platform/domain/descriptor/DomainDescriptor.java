// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.domain.descriptor;

import com.google.common.collect.ImmutableList;
import com.viadeo.kasper.api.domain.Domain;
import com.viadeo.kasper.api.domain.event.Event;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

public class DomainDescriptor implements KasperComponentDescriptor {

    private final String domainName;
    private final Class<? extends Domain> domainClass;
    private final ImmutableList<QueryHandlerDescriptor> queryHandlerDescriptors;
    private final ImmutableList<CommandHandlerDescriptor> commandHandlerDescriptors;
    private final ImmutableList<RepositoryDescriptor> repositoryDescriptor;
    private final ImmutableList<EventListenerDescriptor> eventListenerDescriptor;
    private final ImmutableList<SagaDescriptor> sagaDescriptor;
    private final ImmutableList<Class<? extends Event>> eventClasses;

    // ------------------------------------------------------------------------

    public DomainDescriptor(final String domainName,
                            final Class<? extends Domain> domainClass,
                            final Collection<QueryHandlerDescriptor> queryHandlerDescriptors,
                            final Collection<CommandHandlerDescriptor> commandHandlerDescriptors,
                            final Collection<RepositoryDescriptor> repositoryDescriptor,
                            final Collection<EventListenerDescriptor> eventListenerDescriptor,
                            final Collection<SagaDescriptor> sagaDescriptor,
                            final Collection<Class<? extends Event>> eventClasses
    ) {
        this.domainName = checkNotNull(domainName);
        this.domainClass = checkNotNull(domainClass);
        this.queryHandlerDescriptors = ImmutableList.copyOf(checkNotNull(queryHandlerDescriptors));
        this.commandHandlerDescriptors = ImmutableList.copyOf(checkNotNull(commandHandlerDescriptors));
        this.repositoryDescriptor = ImmutableList.copyOf(checkNotNull(repositoryDescriptor));
        this.eventListenerDescriptor = ImmutableList.copyOf(checkNotNull(eventListenerDescriptor));
        this.sagaDescriptor = ImmutableList.copyOf(checkNotNull(sagaDescriptor));
        this.eventClasses = ImmutableList.copyOf(checkNotNull(eventClasses));
    }

    // ------------------------------------------------------------------------

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

    public ImmutableList<SagaDescriptor> getSagaDescriptors() {
        return sagaDescriptor;
    }

    public ImmutableList<Class<? extends Event>> getEventClasses() {
        return eventClasses;
    }

    @Override
    public Class<? extends Domain> getReferenceClass() {
        return domainClass;
    }

    public String getName(){
        return domainName;
    }

}
