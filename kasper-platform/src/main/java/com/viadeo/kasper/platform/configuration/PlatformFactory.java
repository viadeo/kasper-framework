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

    private final PlatformConfiguration pc;

    // ------------------------------------------------------------------------

    public PlatformFactory() {
        pc = new DefaultPlatformSpringConfiguration();
    }

    public PlatformFactory(final PlatformConfiguration pc) {
        this.pc = pc;
    }

    // ------------------------------------------------------------------------

    public Platform getPlatform() {

        // -- COMMAND
        final CommandBus commandBus = pc.commandBus();
        final CommandGatewayFactoryBean cmdGtwFactoryBean = pc.commandGatewayFactoryBean(commandBus);
        try {
            cmdGtwFactoryBean.afterPropertiesSet();
        } catch (final Exception e) {
            throw new KasperException("Unable to bind the gateway", e);
        }
        final CommandGateway commandGateway = pc.commandGateway(cmdGtwFactoryBean);

        // -- QUERY
        final QueryServicesLocator queryServicesLocator = pc.queryServicesLocator();
        final QueryGateway queryGateway = pc.queryGateway(queryServicesLocator);

        // -- EVENT
        final EventBus eventBus = pc.eventBus();

        // -- ROOT PROCESSING
        final ComponentsInstanceManager componentsInstanceManager = pc.getComponentsInstanceManager();
        final AnnotationRootProcessor annotationRootProcessor = pc.annotationRootProcessor(componentsInstanceManager);

        final DomainLocator domainLocator = pc.domainLocator();

        final CommandHandlersProcessor commandHandlersProcessor = pc.commandHandlersProcessor(commandBus, domainLocator);
        annotationRootProcessor.registerProcessor(commandHandlersProcessor);

        final DomainsProcessor domainsProcessor = pc.domainsProcessor(domainLocator);
        annotationRootProcessor.registerProcessor(domainsProcessor);

        final EventListenersProcessor eventListenersProcessor = pc.eventListenersProcessor(eventBus);
        annotationRootProcessor.registerProcessor(eventListenersProcessor);

        final QueryServicesProcessor queryServicesProcessor = pc.queryServicesProcessor(queryServicesLocator);
        annotationRootProcessor.registerProcessor(queryServicesProcessor);

        final RepositoriesProcessor repositoriesProcessor = pc.repositoriesProcessor(domainLocator, eventBus);
        annotationRootProcessor.registerProcessor(repositoriesProcessor);

        final ServiceFiltersProcessor serviceFiltersProcessor = pc.serviceFiltersProcessor(queryServicesLocator);
        annotationRootProcessor.registerProcessor(serviceFiltersProcessor);

        // -- PLATFORM
        final Platform platform = pc.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);

        return platform;
    }

}
