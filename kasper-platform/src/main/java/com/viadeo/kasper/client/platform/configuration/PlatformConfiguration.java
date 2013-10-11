// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.impl.KasperPlatform;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import org.axonframework.commandhandling.CommandBus;

/**
 * Kasper platform components configuration
 *
 * Acts as a library of factories for each platform's component
 *
 * IMPORTANT : All of these factories must act as singleton factories
 *
 * The classes implementing this interface are responsible to instanciate all
 * the components of the platform
 *
 * You can subclass it to use your own component implementations
 *
 */
public interface PlatformConfiguration {

     /**
      * @return the manager responsible to keep a reference to all Kasper platform
      * elements (domains, handlers, listeners, query services, repositories, service filters, ...)
     */
     ComponentsInstanceManager getComponentsInstanceManager();

    /**
     * Warning : override the two methods at once
     *
     * @param instancesManager the instances manager to be used
     * @return the annotation root processor
     */
     AnnotationRootProcessor annotationRootProcessor(ComponentsInstanceManager instancesManager);
     AnnotationRootProcessor annotationRootProcessor();

    /**
     * Warning : override the two methods at once
     *
     * @param commandGateway the command gateway to be used
     * @param queryGateway the query gateway to be used
     * @param eventBus the event bus to be used
     * @param annotationRootProcessor the annotation root processor to be used
     * @return the built Kasper platform
     */
     KasperPlatform kasperPlatform(CommandGateway commandGateway
            ,  QueryGateway queryGateway
            ,  KasperEventBus eventBus
            ,  AnnotationRootProcessor annotationRootProcessor
     );
     KasperPlatform kasperPlatform();

    /**
     * @return the event bus
     */
     KasperEventBus eventBus();

    /**
     * Warning : override the two methods at once
     *
     * @param commandBus the command bus to be used
     * @return the command gateway
     */
     CommandGateway commandGateway(CommandBus commandBus);
     CommandGateway commandGateway();

    /**
     * Warning : override the two methods at once
     *
     * @param locator the query services locator to be used
     * @return the query gateway
     */
     QueryGateway queryGateway(QueryServicesLocator locator);
     QueryGateway queryGateway();

    /**
     * @return the command bus
     */
     CommandBus commandBus();

    /**
     * Warning : override the two methods at once
     *
     * @param commandHandlerResolver the command handler resolver
     * @param repositoryResolver the repository resolver
     *
     * @return the domain locator
     */
    DomainLocator domainLocator(CommandHandlerResolver commandHandlerResolver, RepositoryResolver repositoryResolver);
    DomainLocator domainLocator();

    /**
     * Warning : override the two methods at once
     *
     * @param queryServiceResolver the resolver factory
     *
     * @return the query services locator
     */
    QueryServicesLocator queryServicesLocator(QueryServiceResolver queryServiceResolver);
    QueryServicesLocator queryServicesLocator();

    /**
     * Warning : override the two methods at once
     *
     * @param commandBus the command bus to be used
     * @param domainLocator the domain locator to be used
     * @return the processor
     */
     CommandHandlersProcessor commandHandlersProcessor(CommandBus commandBus, DomainLocator domainLocator, KasperEventBus eventBus, CommandHandlerResolver commandHandlerResolver);
     CommandHandlersProcessor commandHandlersProcessor();

    /**
     * Warning : override the two methods at once
     *
     * @param domainLocator the domain locator to be used
     * @return the processor
     */
     DomainsProcessor domainsProcessor(DomainLocator domainLocator);
     DomainsProcessor domainsProcessor();

    /**
     * Warning : override the two methods at once
     *
     * @param eventBus the event bus to be used
     * @return the processor
     */
     EventListenersProcessor eventListenersProcessor(KasperEventBus eventBus, CommandGateway commandGateway);
     EventListenersProcessor eventListenersProcessor();

    /**
     * Warning : override the two methods at once
     *
     * @param locator the query services locator to be used
     * @return the processor
     */
     QueryServicesProcessor queryServicesProcessor(QueryServicesLocator locator);
     QueryServicesProcessor queryServicesProcessor();

    /**
     * Warning : override the two methods at once
     *
     * @param locator the domain locator to be used
     * @param eventBus the event bus to be used
     * @return the processor
     */
     RepositoriesProcessor repositoriesProcessor(DomainLocator locator, KasperEventBus eventBus);
     RepositoriesProcessor repositoriesProcessor();

    /**
     * Warning : override the two methods at once
     *
     * @param locator the query services locator to be used
     * @return the processor
     */
     ServiceFiltersProcessor serviceFiltersProcessor(QueryServicesLocator locator);
     ServiceFiltersProcessor serviceFiltersProcessor();

    /**
     * Initialize one or several Yammer metrics reporters
     *
     * Register KasperMetrics.getRegistry() in your reporter
     * cf http://metrics.codahale.com/
     *
     * Must be called by kasperPlatform()
     *
     */
    void initializeMetricsReporters();

    /**
     * @return the domain resolver
     */
    DomainResolver domainResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     *
     * @return the command handler resolver
     */
    CommandHandlerResolver commandHandlerResolver(final DomainResolver domainResolver);
    CommandHandlerResolver commandHandlerResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainLocator the domain locator
     * @param domainResolver the domain resolver
     * @param commandHandlerResolver the command handler resolver
     *
     * @return the command resolver
     */
    CommandResolver commandResolver(
            DomainLocator domainLocator,
            DomainResolver domainResolver,
            CommandHandlerResolver commandHandlerResolver
    );
    CommandResolver commandResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the event resolver
     *
     * @return the event listener resolver
     */
    EventListenerResolver eventListenerResolver(DomainResolver domainResolver);
    EventListenerResolver eventListenerResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     * @param queryServiceResolver the query service resolver
     * @param queryServicesLocator the query services locator
     *
     * @return the query resolver
     */
    QueryResolver queryResolver(
            DomainResolver domainResolver,
            QueryServiceResolver queryServiceResolver,
            QueryServicesLocator queryServicesLocator
    );
    QueryResolver queryResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     *
     * @return the query services resolver
     */
    QueryServiceResolver queryServiceResolver(final DomainResolver domainResolver);
    QueryServiceResolver queryServiceResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param entityResolver the entity resolver
     * @param domainResolver the domain resolver
     *
     * @return the entity resolver
     */
    RepositoryResolver repositoryResolver(EntityResolver entityResolver, DomainResolver domainResolver);
    RepositoryResolver repositoryResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param conceptResolver the concept resolver
     * @param relationResolver the relation resolver
     * @param domainResolver the domain resolver
     *
     * @return the entity resolver
     */
    EntityResolver entityResolver(
            ConceptResolver conceptResolver,
            RelationResolver relationResolver,
            DomainResolver domainResolver
    );
    EntityResolver entityResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     *
     * @return the concept resolver
     */
    ConceptResolver conceptResolver(DomainResolver domainResolver);
    ConceptResolver conceptResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     *
     * @return the relation resolver
     */
    RelationResolver relationResolver(DomainResolver domainResolver);
    RelationResolver relationResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     *
     * @return the event resolver
     */
    EventResolver eventResolver(DomainResolver domainResolver);
    EventResolver eventResolver();

    /**
     * Warning : override the two methods at once
     *
     * @param domainResolver the domain resolver
     * @param commandResolver the command resolver
     * @param commandHandlerResolver the command handler resolver
     * @param eventListenerResolver the event listener resolver
     * @param queryResolver the query resolver
     * @param queryServiceResolver the query service resolver
     * @param repositoryResolver the repository resolver
     * @param entityResolver the entity resolver
     * @param conceptResolver the concept resolver
     * @param relationResolver the relation resolver
     * @param eventResolver the event resolver
     *
     * @return the resolver factory
     */
    ResolverFactory resolverFactory(
            DomainResolver domainResolver,
            CommandResolver commandResolver,
            CommandHandlerResolver commandHandlerResolver,
            EventListenerResolver eventListenerResolver,
            QueryResolver queryResolver,
            QueryServiceResolver queryServiceResolver,
            RepositoryResolver repositoryResolver,
            EntityResolver entityResolver,
            ConceptResolver conceptResolver,
            RelationResolver relationResolver,
            EventResolver eventResolver
    );
    ResolverFactory resolverFactory();

}
