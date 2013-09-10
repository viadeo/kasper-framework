// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.impl.KasperPlatform;
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
     * @return the domain locator
     */
     DomainLocator domainLocator();

    /**
     * @return the query services locator
     */
     QueryServicesLocator queryServicesLocator();

    /**
     * Warning : override the two methods at once
     *
     * @param commandBus the command bus to be used
     * @param domainLocator the domain locator to be used
     * @return the processor
     */
     CommandHandlersProcessor commandHandlersProcessor(CommandBus commandBus, DomainLocator domainLocator);
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
     EventListenersProcessor eventListenersProcessor(KasperEventBus eventBus);
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

}
