// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.unitofwork.UnitOfWorkFactory;

public class PlatformFactory {

    private final PlatformConfiguration platformConfiguration;
    private Platform platform;

    // ------------------------------------------------------------------------

    public PlatformFactory() {
        platformConfiguration = new DefaultPlatformConfiguration();
    }

    public PlatformFactory(final PlatformConfiguration pc) {
        this.platformConfiguration = pc;
    }

    public PlatformConfiguration getPlatformConfiguration() {
        return this.platformConfiguration;
    }

    // ------------------------------------------------------------------------

    public PlatformFactory configure() {
        if (null != platform) {
            throw new KasperException("Platform factory as already been configured");
        }
        platform = getPlatform();
        return this;
    }

    public PlatformFactory boot() {
        if (null == platform) {
            this.configure();
        }
        if (platform.isBooted()) {
            throw new KasperException("Platform has already been booted");
        }
        platform.boot();
        return this;
    }

    // ------------------------------------------------------------------------

    public Platform getPlatform() {
        return this.getPlatform(false);
    }

    public Platform getPlatform(final boolean bootPlatform) {

        if (null != platform) {
            return platform;
        }

        // -- COMMAND

        final UnitOfWorkFactory uowFactory = platformConfiguration.uowFactory();
        final CommandBus commandBus = platformConfiguration.commandBus(uowFactory);
        final CommandGateway commandGateway = platformConfiguration.commandGateway(commandBus);

        // -- MAIN RESOLVERS

        final DomainResolver domainResolver = platformConfiguration.domainResolver();

        final CommandHandlerResolver commandHandlerResolver = platformConfiguration.commandHandlerResolver(domainResolver);

        final ConceptResolver conceptResolver = platformConfiguration.conceptResolver(domainResolver);

        final RelationResolver relationResolver = platformConfiguration.relationResolver(domainResolver, conceptResolver);

        final EntityResolver entityResolver = platformConfiguration.entityResolver(conceptResolver, relationResolver, domainResolver);

        final RepositoryResolver repositoryResolver = platformConfiguration.repositoryResolver(entityResolver, domainResolver);

         // -- DOMAIN LOCATOR

        final DomainLocator domainLocator = platformConfiguration.domainLocator(commandHandlerResolver, repositoryResolver);

        // -- COMPONENTS RESOLVERS

        final CommandResolver commandResolver = platformConfiguration.commandResolver(domainLocator, domainResolver, commandHandlerResolver);

        final EventResolver eventResolver = platformConfiguration.eventResolver(domainResolver);

        final EventListenerResolver eventListenerResolver = platformConfiguration.eventListenerResolver(domainResolver);

        final QueryHandlerResolver queryHandlerResolver = platformConfiguration.queryHandlerResolver(domainResolver);

        final QueryHandlersLocator queryHandlersLocator = platformConfiguration.queryHandlersLocator(queryHandlerResolver);

        final QueryResolver queryResolver = platformConfiguration.queryResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);

        final QueryResultResolver queryResultResolver = platformConfiguration.queryResultResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);
        
        final ResolverFactory resolverFactory = platformConfiguration.resolverFactory(
                domainResolver,
                commandResolver,
                commandHandlerResolver,
                eventListenerResolver,
                queryResolver,
                queryResultResolver,
                queryHandlerResolver,
                repositoryResolver,
                entityResolver,
                conceptResolver,
                relationResolver,
                eventResolver
        );

        // -- QUERY
        final QueryGateway queryGateway = platformConfiguration.queryGateway(queryHandlersLocator);

        // -- EVENT
        final KasperEventBus eventBus = platformConfiguration.eventBus();

        // -- ROOT PROCESSING
        final ComponentsInstanceManager componentsInstanceManager = platformConfiguration.getComponentsInstanceManager();
        final AnnotationRootProcessor annotationRootProcessor = platformConfiguration.annotationRootProcessor(componentsInstanceManager);

        final CommandHandlersProcessor commandHandlersProcessor = platformConfiguration.commandHandlersProcessor(commandBus, domainLocator, eventBus, commandHandlerResolver);
        annotationRootProcessor.registerProcessor(commandHandlersProcessor);

        final DomainsProcessor domainsProcessor = platformConfiguration.domainsProcessor(domainLocator);
        annotationRootProcessor.registerProcessor(domainsProcessor);

        final EventListenersProcessor eventListenersProcessor = platformConfiguration.eventListenersProcessor(eventBus, commandGateway);
        annotationRootProcessor.registerProcessor(eventListenersProcessor);

        final QueryHandlersProcessor queryHandlersProcessor = platformConfiguration.queryHandlersProcessor(queryHandlersLocator);
        annotationRootProcessor.registerProcessor(queryHandlersProcessor);

        final RepositoriesProcessor repositoriesProcessor = platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
        annotationRootProcessor.registerProcessor(repositoriesProcessor);

        final QueryHandlerFiltersProcessor queryHandlerFiltersProcessor = platformConfiguration.queryHandlerFiltersProcessor(queryHandlersLocator);
        annotationRootProcessor.registerProcessor(queryHandlerFiltersProcessor);

        // -- Metrics
        KasperMetrics.setResolverFactory(resolverFactory);

        // -- PLATFORM
        platform = platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);

        if (bootPlatform) {
            platform.boot();
        }

        return platform;
    }

}
