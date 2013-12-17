// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.client.platform.OldPlatform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.unitofwork.UnitOfWorkFactory;

/**
 * @deprecated use {@link com.viadeo.kasper.client.platform.Platform.Builder} instead.
 */
@Deprecated
public class PlatformFactory {

    private final OldPlatformConfiguration oldPlatformConfiguration;
    private OldPlatform platform;

    // ------------------------------------------------------------------------

    public PlatformFactory() {
        oldPlatformConfiguration = new DefaultOldPlatformConfiguration();
    }

    public PlatformFactory(final OldPlatformConfiguration pc) {
        this.oldPlatformConfiguration = pc;
    }

    public OldPlatformConfiguration getPlatformConfiguration() {
        return this.oldPlatformConfiguration;
    }

    // ------------------------------------------------------------------------

    public PlatformFactory configure() {
        if (null != platform) {
            throw new KasperException("OldPlatform factory as already been configured");
        }
        platform = getPlatform();
        return this;
    }

    public PlatformFactory boot() {
        if (null == platform) {
            this.configure();
        }
        if (platform.isBooted()) {
            throw new KasperException("OldPlatform has already been booted");
        }
        platform.boot();
        return this;
    }

    // ------------------------------------------------------------------------

    public OldPlatform getPlatform() {
        return this.getPlatform(false);
    }

    public OldPlatform getPlatform(final boolean bootPlatform) {

        if (null != platform) {
            return platform;
        }

        // -- COMMAND

        final UnitOfWorkFactory uowFactory = oldPlatformConfiguration.uowFactory();
        final CommandBus commandBus = oldPlatformConfiguration.commandBus(uowFactory);
        final CommandGateway commandGateway = oldPlatformConfiguration.commandGateway(commandBus);

        // -- MAIN RESOLVERS

        final DomainResolver domainResolver = oldPlatformConfiguration.domainResolver();

        final CommandHandlerResolver commandHandlerResolver = oldPlatformConfiguration.commandHandlerResolver(domainResolver);

        final ConceptResolver conceptResolver = oldPlatformConfiguration.conceptResolver(domainResolver);

        final RelationResolver relationResolver = oldPlatformConfiguration.relationResolver(domainResolver, conceptResolver);

        final EntityResolver entityResolver = oldPlatformConfiguration.entityResolver(conceptResolver, relationResolver, domainResolver);

        final RepositoryResolver repositoryResolver = oldPlatformConfiguration.repositoryResolver(entityResolver, domainResolver);

         // -- DOMAIN LOCATOR

        final DomainLocator domainLocator = oldPlatformConfiguration.domainLocator(commandHandlerResolver, repositoryResolver);

        // -- REPOSITORY MANAGER

        final RepositoryManager repositoryManager = oldPlatformConfiguration.repositoryManager();

        // -- COMPONENTS RESOLVERS

        final CommandResolver commandResolver = oldPlatformConfiguration.commandResolver(domainLocator, domainResolver, commandHandlerResolver);

        final EventResolver eventResolver = oldPlatformConfiguration.eventResolver(domainResolver);

        final EventListenerResolver eventListenerResolver = oldPlatformConfiguration.eventListenerResolver(domainResolver);

        final QueryHandlerResolver queryHandlerResolver = oldPlatformConfiguration.queryHandlerResolver(domainResolver);

        final QueryHandlersLocator queryHandlersLocator = oldPlatformConfiguration.queryHandlersLocator(queryHandlerResolver);

        final QueryResolver queryResolver = oldPlatformConfiguration.queryResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);

        final QueryResultResolver queryResultResolver = oldPlatformConfiguration.queryResultResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);
        
        final ResolverFactory resolverFactory = oldPlatformConfiguration.resolverFactory(
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
        final QueryGateway queryGateway = oldPlatformConfiguration.queryGateway(queryHandlersLocator);

        // -- EVENT
        final KasperEventBus eventBus = oldPlatformConfiguration.eventBus();

        // -- ROOT PROCESSING
        final ComponentsInstanceManager componentsInstanceManager = oldPlatformConfiguration.getComponentsInstanceManager();
        final AnnotationRootProcessor annotationRootProcessor = oldPlatformConfiguration.annotationRootProcessor(componentsInstanceManager);

        final CommandHandlersProcessor commandHandlersProcessor = oldPlatformConfiguration.commandHandlersProcessor(commandBus, domainLocator, repositoryManager, eventBus, commandHandlerResolver);
        annotationRootProcessor.registerProcessor(commandHandlersProcessor);

        final DomainsProcessor domainsProcessor = oldPlatformConfiguration.domainsProcessor(domainLocator);
        annotationRootProcessor.registerProcessor(domainsProcessor);

        final EventListenersProcessor eventListenersProcessor = oldPlatformConfiguration.eventListenersProcessor(eventBus, commandGateway);
        annotationRootProcessor.registerProcessor(eventListenersProcessor);

        final QueryHandlersProcessor queryHandlersProcessor = oldPlatformConfiguration.queryHandlersProcessor(queryHandlersLocator);
        annotationRootProcessor.registerProcessor(queryHandlersProcessor);

        final RepositoriesProcessor repositoriesProcessor = oldPlatformConfiguration.repositoriesProcessor(repositoryManager, eventBus);
        annotationRootProcessor.registerProcessor(repositoriesProcessor);

        final QueryHandlerAdaptersProcessor queryHandlerAdaptersProcessor = oldPlatformConfiguration.queryHandlerAdaptersProcessor(queryHandlersLocator);
        annotationRootProcessor.registerProcessor(queryHandlerAdaptersProcessor);

        // -- Metrics
        KasperMetrics.setResolverFactory(resolverFactory);

        // -- PLATFORM
        platform = oldPlatformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);

        if (bootPlatform) {
            platform.boot();
        }

        return platform;
    }

}
