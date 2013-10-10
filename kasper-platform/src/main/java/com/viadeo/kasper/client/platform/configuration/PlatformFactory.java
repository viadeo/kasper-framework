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
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;

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
            throw new KasperException("Platform factory is not configured");
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
        final CommandBus commandBus = platformConfiguration.commandBus();
        final CommandGateway commandGateway = platformConfiguration.commandGateway(commandBus);

        // -- QUERY
        final QueryServicesLocator queryServicesLocator = platformConfiguration.queryServicesLocator();
        final QueryGateway queryGateway = platformConfiguration.queryGateway(queryServicesLocator);

        // -- EVENT
        final KasperEventBus eventBus = platformConfiguration.eventBus();

        // -- ROOT PROCESSING
        final ComponentsInstanceManager componentsInstanceManager = platformConfiguration.getComponentsInstanceManager();
        final AnnotationRootProcessor annotationRootProcessor = platformConfiguration.annotationRootProcessor(componentsInstanceManager);

        final CommandHandlerResolver commandHandlerResolver = platformConfiguration.commandHandlerResolver();
        final DomainLocator domainLocator = platformConfiguration.domainLocator(commandHandlerResolver);

        final CommandHandlersProcessor commandHandlersProcessor = platformConfiguration.commandHandlersProcessor(commandBus, domainLocator, eventBus);
        annotationRootProcessor.registerProcessor(commandHandlersProcessor);

        final DomainsProcessor domainsProcessor = platformConfiguration.domainsProcessor(domainLocator);
        annotationRootProcessor.registerProcessor(domainsProcessor);

        final EventListenersProcessor eventListenersProcessor = platformConfiguration.eventListenersProcessor(eventBus, commandGateway);
        annotationRootProcessor.registerProcessor(eventListenersProcessor);

        final QueryServicesProcessor queryServicesProcessor = platformConfiguration.queryServicesProcessor(queryServicesLocator);
        annotationRootProcessor.registerProcessor(queryServicesProcessor);

        final RepositoriesProcessor repositoriesProcessor = platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
        annotationRootProcessor.registerProcessor(repositoriesProcessor);

        final ServiceFiltersProcessor serviceFiltersProcessor = platformConfiguration.serviceFiltersProcessor(queryServicesLocator);
        annotationRootProcessor.registerProcessor(serviceFiltersProcessor);

        // -- COMPONENTS RESOLVERS

        final CommandResolver commandResolver = platformConfiguration.commandResolver(domainLocator, commandHandlerResolver);

        final EventResolver eventResolver = platformConfiguration.eventResolver();

        final EventListenerResolver eventListenerResolver = platformConfiguration.eventListenerResolver(eventResolver);

        final QueryServiceResolver queryServiceResolver = platformConfiguration.queryServiceResolver();

        final QueryResolver queryResolver = platformConfiguration.queryResolver(queryServiceResolver, queryServicesLocator);

        final ConceptResolver conceptResolver = platformConfiguration.conceptResolver();

        final RelationResolver relationResolver = platformConfiguration.relationResolver();

        final EntityResolver entityResolver = platformConfiguration.entityResolver(conceptResolver, relationResolver);

        final RepositoryResolver repositoryResolver = platformConfiguration.repositoryResolver(entityResolver);

        final DomainResolver domainResolver = platformConfiguration.domainResolver(
                commandResolver,
                eventListenerResolver,
                queryResolver,
                repositoryResolver
        );

        // -- Metrics
        KasperMetrics.setDomainResolver(domainResolver);

        // -- PLATFORM
        platform = platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);

        if (bootPlatform) {
            platform.boot();
        }

        return platform;
    }

}
