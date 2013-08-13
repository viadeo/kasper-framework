// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import com.google.common.base.Throwables;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.platform.components.eventbus.KasperHybridEventBus;
import com.viadeo.kasper.platform.impl.KasperPlatform;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;

public class DefaultPlatformConfiguration implements PlatformConfiguration {

    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        final SpringComponentsInstanceManager sman = new SpringComponentsInstanceManager();
        return sman;
    }

    @Override
    public AnnotationRootProcessor annotationRootProcessor(final ComponentsInstanceManager instancesManager){
        final AnnotationRootProcessor rootProcessor =  new AnnotationRootProcessor();
        rootProcessor.setComponentsInstanceManager(instancesManager);
        return rootProcessor;
    }

    @Override
    public KasperPlatform kasperPlatform(final CommandGateway commandGateway
            , final QueryGateway queryGateway
            , final EventBus eventBus
            , final AnnotationRootProcessor annotationRootProcessor
    ) {
        final KasperPlatform kasperPlatform = new KasperPlatform();
        kasperPlatform.setCommandGateway(commandGateway);
        kasperPlatform.setQueryGateway(queryGateway);
        kasperPlatform.setRootProcessor(annotationRootProcessor);
        kasperPlatform.setEventBus(eventBus);
        return kasperPlatform;
    }

    @Override
    public EventBus eventBus(){
        return new KasperHybridEventBus();
    }

    @Override
    public CommandGateway commandGateway(final CommandGatewayFactoryBean commandGatewayFactoryBean){
        try {
            return (CommandGateway) commandGatewayFactoryBean.getObject();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(final EventBus eventBus){
        AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor = new AnnotationEventListenerBeanPostProcessor();
        annotationEventListenerBeanPostProcessor.setEventBus(eventBus);
        return annotationEventListenerBeanPostProcessor;
    }

    @Override
    public CommandBus commandBus(){
        return  new KasperCommandBus();
    }

    @Override
    public CommandGatewayFactoryBean commandGatewayFactoryBean(final CommandBus commandBus){
        final CommandGatewayFactoryBean commandGatewayFactoryBean = new CommandGatewayFactoryBean();
        commandGatewayFactoryBean.setCommandBus(commandBus);
        commandGatewayFactoryBean.setGatewayInterface(CommandGateway.class);
        return commandGatewayFactoryBean;
    }

    @Override
    public DomainLocator domainLocator(){
        return new DefaultDomainLocator();
    }

    @Override
    public QueryServicesLocator queryServicesLocator(){
        return new DefaultQueryServicesLocator();
    }

    @Override
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator){
        CommandHandlersProcessor commandHandlersProcessor = new CommandHandlersProcessor();
        commandHandlersProcessor.setCommandBus(commandBus);
        commandHandlersProcessor.setDomainLocator(domainLocator);
        return commandHandlersProcessor;
    }

    @Override
    public RepositoriesProcessor repositoriesProcessor(final DomainLocator locator, final EventBus eventBus){
        final RepositoriesProcessor repositoriesProcessor = new RepositoriesProcessor();
        repositoriesProcessor.setDomainLocator(locator);
        repositoriesProcessor.setEventBus(eventBus);
        return repositoriesProcessor;
    }

    @Override
    public ServiceFiltersProcessor serviceFiltersProcessor(QueryServicesLocator locator) {
        final ServiceFiltersProcessor serviceFiltersProcessor = new ServiceFiltersProcessor();
        serviceFiltersProcessor.setQueryServicesLocator(locator);
        return serviceFiltersProcessor;
    }

    @Override
    public EventListenersProcessor eventListenersProcessor(final EventBus eventBus){
        final EventListenersProcessor eventListenersProcessor = new EventListenersProcessor();
        eventListenersProcessor.setEventBus(eventBus);
        return eventListenersProcessor;
    }


    @Override
    public QueryServicesProcessor queryServicesProcessor(final QueryServicesLocator locator){
        final QueryServicesProcessor queryServicesProcessor = new QueryServicesProcessor();
        queryServicesProcessor.setQueryServicesLocator(locator);
        return queryServicesProcessor;
    }

    @Override
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        final DomainsProcessor domainsProcessor = new DomainsProcessor();
        domainsProcessor.setDomainLocator(domainLocator);
        return domainsProcessor;
    }

    @Override
    public DefaultQueryGateway queryGateway(final QueryServicesLocator locator){
        final DefaultQueryGateway defaultQueryGateway = new DefaultQueryGateway();
        defaultQueryGateway.setQueryServicesLocator(locator);
        return defaultQueryGateway;
    }

}
