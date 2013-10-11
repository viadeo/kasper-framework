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
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.junit.Test;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.fail;

public class PlatformConfigurationTest {

    @Test
    public void testDefaultPlatformConfiguration() throws Exception {
        this.testPlatformConfiguration(new DefaultPlatformConfiguration());
    }

    @Test
    public void testDefaultSpringPlatformConfiguration() throws Exception {
        this.testPlatformConfiguration(new DefaultPlatformSpringConfiguration());
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unused")
    private void testPlatformConfiguration(final PlatformConfiguration platformConfiguration) throws Exception {

        final CommandBus commandBus =
                this.testCommandBus(platformConfiguration);

        final CommandGateway commandGateway =
                this.testCommandGateway(platformConfiguration, commandBus);

        final QueryServicesLocator queryServicesLocator=
                this.testQueryServicesLocator(platformConfiguration);

        final QueryGateway queryGateway =
                this.testQueryGateway(platformConfiguration, queryServicesLocator);

        final KasperEventBus eventBus =
                this.testEventBus(platformConfiguration);

        final ComponentsInstanceManager componentsInstanceManager =
                this.testComponentsInstanceManager(platformConfiguration);

        final AnnotationRootProcessor annotationRootProcessor =
                this.testAnnotationRootProcessor(platformConfiguration, componentsInstanceManager);

        /* FIXME: add test */
        final DomainResolver domainResolver = platformConfiguration.domainResolver();

        final CommandHandlerResolver commandHandlerResolver = testCommandHandlerResolver(platformConfiguration, domainResolver);

        final DomainLocator domainLocator =
                this.testDomainLocator(platformConfiguration, commandHandlerResolver);

        final ResolverFactory resolverFactory = testResolverFactory(
                platformConfiguration,
                domainLocator,
                commandHandlerResolver,
                queryServicesLocator,
                domainResolver
        );

        final CommandHandlersProcessor commandHandlersProcessor =
                this.testCommandHandlersProcessor(
                        platformConfiguration, commandBus, domainLocator, commandHandlerResolver, eventBus);

        final DomainsProcessor domainsProcessor =
                this.testDomainsProcessor(platformConfiguration, domainLocator);

        final EventListenersProcessor eventListenersProcessor =
                this.testEventListenersProcessor(platformConfiguration, eventBus, commandGateway);

        final QueryServicesProcessor queryServicesProcessor =
                this.testQueryServicesProcessor(platformConfiguration, queryServicesLocator);

        final RepositoriesProcessor repositoriesProcessor=
                this.testRepositoriesProcessor(platformConfiguration, domainLocator, eventBus);

        final ServiceFiltersProcessor serviceFiltersProcessor=
                this.testServiceFiltersProcessor(platformConfiguration, queryServicesLocator);

        final Platform platform =
                this.testPlatform(platformConfiguration,
                                  commandGateway, queryGateway,
                                  eventBus, annotationRootProcessor);
    }

    // ------------------------------------------------------------------------

    /*
     * FIXME: implement tests
     */
    private CommandHandlerResolver testCommandHandlerResolver(final PlatformConfiguration platformConfiguration, final DomainResolver domainResolver) {
        return platformConfiguration.commandHandlerResolver(domainResolver);
    }

    /*
     * FIXME: implement tests
     */
    private ResolverFactory testResolverFactory(
            final PlatformConfiguration platformConfiguration,
            final DomainLocator domainLocator,
            final CommandHandlerResolver commandHandlerResolver,
            final QueryServicesLocator queryServicesLocator,
            final DomainResolver domainResolver
    ) {

        final CommandResolver commandResolver = platformConfiguration.commandResolver(domainLocator, domainResolver, commandHandlerResolver);

        final EventResolver eventResolver = platformConfiguration.eventResolver(domainResolver);

        final EventListenerResolver eventListenerResolver = platformConfiguration.eventListenerResolver(eventResolver, domainResolver);

        final QueryServiceResolver queryServiceResolver = platformConfiguration.queryServiceResolver(domainResolver);

        final QueryResolver queryResolver = platformConfiguration.queryResolver(domainResolver, queryServiceResolver, queryServicesLocator);

        final ConceptResolver conceptResolver = platformConfiguration.conceptResolver(domainResolver);

        final RelationResolver relationResolver = platformConfiguration.relationResolver(domainResolver);

        final EntityResolver entityResolver = platformConfiguration.entityResolver(conceptResolver, relationResolver, domainResolver);

        final RepositoryResolver repositoryResolver = platformConfiguration.repositoryResolver(entityResolver, domainResolver);

        final ResolverFactory resolverFactory = new ResolverFactory();
        resolverFactory.setDomainResolver(domainResolver);
        resolverFactory.setCommandResolver(commandResolver);
        resolverFactory.setCommandHandlerResolver(commandHandlerResolver);
        resolverFactory.setEventListenerResolver(eventListenerResolver);
        resolverFactory.setQueryResolver(queryResolver);
        resolverFactory.setQueryServiceResolver(queryServiceResolver);
        resolverFactory.setRepositoryResolver(repositoryResolver);
        resolverFactory.setEntityResolver(entityResolver);
        resolverFactory.setConceptResolver(conceptResolver);
        resolverFactory.setRelationResolver(relationResolver);
        resolverFactory.setEventResolver(eventResolver);

        return resolverFactory;
    }

    // ------------------------------------------------------------------------

    private CommandBus testCommandBus(final PlatformConfiguration platformConfiguration) {
        final CommandBus commandBus = platformConfiguration.commandBus();
        assertSame(commandBus, platformConfiguration.commandBus());
        return commandBus;
    }

    // ------------------------------------------------------------------------

    private CommandGateway testCommandGateway(final PlatformConfiguration platformConfiguration,
                                              final CommandBus commandBus) {

        try {
            platformConfiguration.commandGateway();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final CommandGateway commandGateway = platformConfiguration.commandGateway(commandBus);
        assertSame(commandGateway, platformConfiguration.commandGateway());

        try {
            platformConfiguration.commandGateway(commandBus);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return commandGateway;
    }

    // ------------------------------------------------------------------------

    private QueryServicesLocator testQueryServicesLocator(final PlatformConfiguration platformConfiguration) {
        final QueryServicesLocator queryServicesLocator = platformConfiguration.queryServicesLocator();
        assertSame(queryServicesLocator, platformConfiguration.queryServicesLocator());

        return queryServicesLocator;
    }

    // ------------------------------------------------------------------------

    private QueryGateway testQueryGateway(final PlatformConfiguration platformConfiguration,
                                          final QueryServicesLocator queryServicesLocator) {
        try {
            platformConfiguration.queryGateway();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final QueryGateway queryGateway = platformConfiguration.queryGateway(queryServicesLocator);
        assertSame(queryGateway, platformConfiguration.queryGateway());

        try {
            platformConfiguration.queryGateway(queryServicesLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return queryGateway;
    }

    // ------------------------------------------------------------------------

    private KasperEventBus testEventBus(final PlatformConfiguration platformConfiguration) {
        final KasperEventBus eventBus = platformConfiguration.eventBus();
        assertSame(eventBus, platformConfiguration.eventBus());

        return eventBus;
    }

    // ------------------------------------------------------------------------

    private ComponentsInstanceManager testComponentsInstanceManager(final PlatformConfiguration platformConfiguration) {
        final ComponentsInstanceManager componentsInstanceManager = platformConfiguration.getComponentsInstanceManager();
        assertSame(componentsInstanceManager, platformConfiguration.getComponentsInstanceManager());

        return componentsInstanceManager;
    }

    // --------------------------------------------------------------------

    private AnnotationRootProcessor testAnnotationRootProcessor(final PlatformConfiguration platformConfiguration,
                                                                final ComponentsInstanceManager componentsInstanceManager) {
        try {
            platformConfiguration.annotationRootProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final AnnotationRootProcessor annotationRootProcessor = platformConfiguration.annotationRootProcessor(componentsInstanceManager);
        assertSame(annotationRootProcessor, platformConfiguration.annotationRootProcessor());

        try {
            platformConfiguration.annotationRootProcessor(componentsInstanceManager);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return annotationRootProcessor;
    }

    // ------------------------------------------------------------------------

    private DomainLocator testDomainLocator(final PlatformConfiguration platformConfiguration, final CommandHandlerResolver commandHandlerResolver) {
        final DomainLocator domainLocator = platformConfiguration.domainLocator(commandHandlerResolver);
        assertSame(domainLocator, platformConfiguration.domainLocator(commandHandlerResolver));

        return domainLocator;
    }

    // ------------------------------------------------------------------------

    private CommandHandlersProcessor testCommandHandlersProcessor(final PlatformConfiguration platformConfiguration,
                                                                  final CommandBus commandBus,
                                                                  final DomainLocator domainLocator,
                                                                  final CommandHandlerResolver commandHandlerResolver,
                                                                  final KasperEventBus eventBus) {
        try {
            platformConfiguration.commandHandlersProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final CommandHandlersProcessor commandHandlersProcessor =
                platformConfiguration.commandHandlersProcessor(
                        commandBus, domainLocator, eventBus, commandHandlerResolver);
        assertSame(commandHandlersProcessor, platformConfiguration.commandHandlersProcessor());

        try {
            platformConfiguration.commandHandlersProcessor(
                    commandBus, domainLocator, eventBus, commandHandlerResolver);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return commandHandlersProcessor;
    }

    // ------------------------------------------------------------------------

    private DomainsProcessor testDomainsProcessor(final PlatformConfiguration platformConfiguration,
                                                  final DomainLocator domainLocator) {
        try {
            platformConfiguration.domainsProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final DomainsProcessor domainsProcessor = platformConfiguration.domainsProcessor(domainLocator);
        assertSame(domainsProcessor, platformConfiguration.domainsProcessor());

        try {
            platformConfiguration.domainsProcessor(domainLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return domainsProcessor;
    }

    // ------------------------------------------------------------------------

    private EventListenersProcessor testEventListenersProcessor(final PlatformConfiguration platformConfiguration,
                                                                final KasperEventBus eventBus,
                                                                final CommandGateway commandGateway) {
        try {
            platformConfiguration.eventListenersProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final EventListenersProcessor eventListenersProcessor =
                platformConfiguration.eventListenersProcessor(eventBus, commandGateway);
        assertSame(eventListenersProcessor, platformConfiguration.eventListenersProcessor());

        try {
            platformConfiguration.eventListenersProcessor(eventBus, commandGateway);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return eventListenersProcessor;
    }

    // ------------------------------------------------------------------------

    private QueryServicesProcessor testQueryServicesProcessor(final PlatformConfiguration platformConfiguration,
                                                              final QueryServicesLocator queryServicesLocator){
        try {
            platformConfiguration.queryServicesProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final QueryServicesProcessor queryServicesProcessor = platformConfiguration.queryServicesProcessor(queryServicesLocator);
        assertSame(queryServicesProcessor, platformConfiguration.queryServicesProcessor());

        try {
            platformConfiguration.queryServicesProcessor(queryServicesLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return queryServicesProcessor;
    }

    // ------------------------------------------------------------------------

    private RepositoriesProcessor testRepositoriesProcessor(final PlatformConfiguration platformConfiguration,
                                                            final DomainLocator domainLocator,
                                                            final KasperEventBus eventBus) {
        try {
            platformConfiguration.repositoriesProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final RepositoriesProcessor repositoriesProcessor = platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
        assertSame(repositoriesProcessor, platformConfiguration.repositoriesProcessor());

        try {
            platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return repositoriesProcessor;
    }

    // ------------------------------------------------------------------------

    private ServiceFiltersProcessor testServiceFiltersProcessor(final PlatformConfiguration platformConfiguration,
                                                                final QueryServicesLocator queryServicesLocator) {
        try {
            platformConfiguration.serviceFiltersProcessor();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final ServiceFiltersProcessor serviceFiltersProcessor = platformConfiguration.serviceFiltersProcessor(queryServicesLocator);
        assertSame(serviceFiltersProcessor, platformConfiguration.serviceFiltersProcessor());

        try {
            platformConfiguration.serviceFiltersProcessor(queryServicesLocator);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return serviceFiltersProcessor;
    }

    // ------------------------------------------------------------------------

    private Platform testPlatform(final PlatformConfiguration platformConfiguration,
                                  final CommandGateway commandGateway,
                                  final QueryGateway queryGateway,
                                  final KasperEventBus eventBus,
                                  final AnnotationRootProcessor annotationRootProcessor) {
        try {
            platformConfiguration.kasperPlatform();
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        final Platform platform = platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);
        assertSame(platform, platformConfiguration.kasperPlatform());

        try {
            platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);
            fail();
        } catch (final KasperException e) {
            // Ignore
        }

        return platform;
    }

}
