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
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.platform.impl.KasperPlatform;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;

 interface PlatformConfiguration {

     ComponentsInstanceManager getComponentsInstanceManager();

     AnnotationRootProcessor annotationRootProcessor(ComponentsInstanceManager instancesManager);

     KasperPlatform kasperPlatform(CommandGateway commandGateway
            ,  QueryGateway queryGateway
            ,  EventBus eventBus
            ,  AnnotationRootProcessor annotationRootProcessor
     );

     EventBus eventBus();

     CommandGateway commandGateway(CommandGatewayFactoryBean commandGatewayFactoryBean);

     AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(EventBus eventBus);

     CommandBus commandBus();

     CommandGatewayFactoryBean commandGatewayFactoryBean(CommandBus commandBus);

     DomainLocator domainLocator();

     QueryServicesLocator queryServicesLocator();

     CommandHandlersProcessor commandHandlersProcessor(CommandBus commandBus, DomainLocator domainLocator);

     DomainsProcessor domainsProcessor(DomainLocator domainLocator);

     EventListenersProcessor eventListenersProcessor(EventBus eventBus);

     QueryServicesProcessor queryServicesProcessor(QueryServicesLocator locator);

     RepositoriesProcessor repositoriesProcessor(DomainLocator locator, EventBus eventBus);

     ServiceFiltersProcessor serviceFiltersProcessor(QueryServicesLocator locator);

     DefaultQueryGateway queryGateway(QueryServicesLocator locator);
}
