// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.platform.Platform;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.eventhandling.EventBus;

public class PlatformFactory {

    private final PlatformConfiguration platformConfiguration;

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

    public Platform getPlatform() {
        return this.getPlatform(false);
    }

    public Platform getPlatform(final boolean bootPlatform) {

        // -- COMMAND
        final CommandBus commandBus = platformConfiguration.commandBus();
        final CommandGatewayFactoryBean cmdGtwFactoryBean = platformConfiguration.commandGatewayFactoryBean(commandBus);
        try {
            /* Spring will initialize this with a BeanPostProcessor */
            if (!DefaultPlatformSpringConfiguration.class.isAssignableFrom(this.platformConfiguration.getClass())) {
                cmdGtwFactoryBean.afterPropertiesSet();
            }
        } catch (final Exception e) {
            throw new KasperException("Unable to bind the gateway", e);
        }
        final CommandGateway commandGateway = platformConfiguration.commandGateway(cmdGtwFactoryBean);

        // -- QUERY
        final QueryServicesLocator queryServicesLocator = platformConfiguration.queryServicesLocator();
        final QueryGateway queryGateway = platformConfiguration.queryGateway(queryServicesLocator);

        // -- EVENT
        final EventBus eventBus = platformConfiguration.eventBus();

        // -- ROOT PROCESSING
        final ComponentsInstanceManager componentsInstanceManager = platformConfiguration.getComponentsInstanceManager();
        final AnnotationRootProcessor annotationRootProcessor = platformConfiguration.annotationRootProcessor(componentsInstanceManager);

        final DomainLocator domainLocator = platformConfiguration.domainLocator();

        final CommandHandlersProcessor commandHandlersProcessor = platformConfiguration.commandHandlersProcessor(commandBus, domainLocator);
        annotationRootProcessor.registerProcessor(commandHandlersProcessor);

        final DomainsProcessor domainsProcessor = platformConfiguration.domainsProcessor(domainLocator);
        annotationRootProcessor.registerProcessor(domainsProcessor);

        final EventListenersProcessor eventListenersProcessor = platformConfiguration.eventListenersProcessor(eventBus);
        annotationRootProcessor.registerProcessor(eventListenersProcessor);

        final QueryServicesProcessor queryServicesProcessor = platformConfiguration.queryServicesProcessor(queryServicesLocator);
        annotationRootProcessor.registerProcessor(queryServicesProcessor);

        final RepositoriesProcessor repositoriesProcessor = platformConfiguration.repositoriesProcessor(domainLocator, eventBus);
        annotationRootProcessor.registerProcessor(repositoriesProcessor);

        final ServiceFiltersProcessor serviceFiltersProcessor = platformConfiguration.serviceFiltersProcessor(queryServicesLocator);
        annotationRootProcessor.registerProcessor(serviceFiltersProcessor);

        // -- PLATFORM
        final Platform platform = platformConfiguration.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);

        if (bootPlatform) {
            platform.boot();
        }

        return platform;
    }

}
