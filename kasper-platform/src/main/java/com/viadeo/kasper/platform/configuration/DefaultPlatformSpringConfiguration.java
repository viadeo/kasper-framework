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
import org.springframework.context.annotation.Bean;

public class DefaultPlatformSpringConfiguration {

    @Bean
    public ComponentsInstanceManager getComponentsInstanceManager() {
        final SpringComponentsInstanceManager sman = new SpringComponentsInstanceManager();
        return sman;
    }

    @Bean
    public AnnotationRootProcessor annotationRootProcessor(final ComponentsInstanceManager instancesManager){
        final AnnotationRootProcessor rootProcessor =  new AnnotationRootProcessor();
        rootProcessor.setComponentsInstanceManager(instancesManager);
        return rootProcessor;
    }

    @Bean(initMethod = "boot")
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

    @Bean
    public EventBus eventBus(){
        return new KasperHybridEventBus();
    }

    @Bean
    public CommandGateway commandGateway(final CommandGatewayFactoryBean commandGatewayFactoryBean){
        try {
            return (CommandGateway) commandGatewayFactoryBean.getObject();
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Bean
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(final EventBus eventBus){
        AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor = new AnnotationEventListenerBeanPostProcessor();
        annotationEventListenerBeanPostProcessor.setEventBus(eventBus);
        return annotationEventListenerBeanPostProcessor;
    }

    @Bean
    public CommandBus commandBus(){
        return  new KasperCommandBus();
    }

    @Bean @SuppressWarnings("unchecked")
    public CommandGatewayFactoryBean commandGatewayFactoryBean(final CommandBus commandBus){
        final CommandGatewayFactoryBean commandGatewayFactoryBean = new CommandGatewayFactoryBean();
        commandGatewayFactoryBean.setCommandBus(commandBus);
        commandGatewayFactoryBean.setGatewayInterface(CommandGateway.class);
        return commandGatewayFactoryBean;
    }

    @Bean
    public DomainLocator domainLocator(){
        return new DefaultDomainLocator();
    }

    @Bean
    public QueryServicesLocator queryServicesLocator(){
        return new DefaultQueryServicesLocator();
    }

    @Bean
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator){
        CommandHandlersProcessor commandHandlersProcessor = new CommandHandlersProcessor();
        commandHandlersProcessor.setCommandBus(commandBus);
        commandHandlersProcessor.setDomainLocator(domainLocator);
        return commandHandlersProcessor;
    }

    @Bean
    public RepositoriesProcessor repositoriesProcessor(final DomainLocator locator, final EventBus eventBus){
        final RepositoriesProcessor repositoriesProcessor = new RepositoriesProcessor();
        repositoriesProcessor.setDomainLocator(locator);
        repositoriesProcessor.setEventBus(eventBus);
        return repositoriesProcessor;
    }

    @Bean
    public EventListenersProcessor eventListenersProcessor(final EventBus eventBus){
        final EventListenersProcessor eventListenersProcessor = new EventListenersProcessor();
        eventListenersProcessor.setEventBus(eventBus);
        return eventListenersProcessor;
    }


    @Bean
    public QueryServicesProcessor queryServicesProcessor(final QueryServicesLocator locator){
        final QueryServicesProcessor queryServicesProcessor = new QueryServicesProcessor();
        queryServicesProcessor.setQueryServicesLocator(locator);
        return queryServicesProcessor;
    }

    @Bean
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        final DomainsProcessor domainsProcessor = new DomainsProcessor();
        domainsProcessor.setDomainLocator(domainLocator);
        return domainsProcessor;
    }


    @Bean
    public DefaultQueryGateway queryGateway(final QueryServicesLocator locator){
        final DefaultQueryGateway defaultQueryGateway = new DefaultQueryGateway();
        defaultQueryGateway.setQueryServicesLocator(locator);
        return defaultQueryGateway;
    }

}
