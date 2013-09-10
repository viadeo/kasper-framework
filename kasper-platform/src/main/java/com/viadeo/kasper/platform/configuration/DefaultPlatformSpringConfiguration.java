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
import com.viadeo.kasper.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.platform.impl.KasperPlatform;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventhandling.annotation.AnnotationEventListenerBeanPostProcessor;
import org.springframework.context.annotation.Bean;

public class DefaultPlatformSpringConfiguration extends DefaultPlatformConfiguration {

    private ComponentsInstanceManager instancesManager;

    @Bean
    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        if (null != this.instancesManager) {
            return this.instancesManager;
        } else {
            final SpringComponentsInstanceManager sman = new SpringComponentsInstanceManager();
            this.instancesManager = sman;
            return sman;
        }
    }

    /* FIXME: see https://github.com/viadeo/kasper-framework/issues/49 */
    @Bean
    public AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor(final EventBus eventBus) {
        final AnnotationEventListenerBeanPostProcessor annotationEventListenerBeanPostProcessor = new AnnotationEventListenerBeanPostProcessor();
        annotationEventListenerBeanPostProcessor.setEventBus(eventBus);
        return annotationEventListenerBeanPostProcessor;
    }

    // ------------------------------------------------------------------------

    @Bean
    @Override
    public AnnotationRootProcessor annotationRootProcessor(final ComponentsInstanceManager instancesManager){
        return super.annotationRootProcessor(instancesManager);
    }

    @Bean(initMethod = "boot")
    @Override
    public KasperPlatform kasperPlatform(final CommandGateway commandGateway
            , final QueryGateway queryGateway
            , final KasperEventBus eventBus
            , final AnnotationRootProcessor annotationRootProcessor
    ) {
        return super.kasperPlatform(commandGateway, queryGateway, eventBus, annotationRootProcessor);
    }

    @Bean
    @Override
    public KasperEventBus eventBus(){
        return super.eventBus();
    }

    @Bean
    @Override
    public CommandGateway commandGateway(final CommandBus commandBus) {
        return super.commandGateway(commandBus);
    }

    @Bean
    @Override
    public CommandBus commandBus(){
        return super.commandBus();
    }

    @Bean
    @Override
    public DomainLocator domainLocator() {
        return super.domainLocator();
    }

    @Bean
    @Override
    public QueryServicesLocator queryServicesLocator(){
        return super.queryServicesLocator();
    }

    @Bean
    @Override
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator){
        return super.commandHandlersProcessor(commandBus, domainLocator);
    }

    @Bean
    @Override
    public RepositoriesProcessor repositoriesProcessor(final DomainLocator locator, final KasperEventBus eventBus){
        return super.repositoriesProcessor(locator, eventBus);
    }

    @Bean
    @Override
    public ServiceFiltersProcessor serviceFiltersProcessor(QueryServicesLocator locator) {
        return super.serviceFiltersProcessor(locator);
    }

    @Bean
    @Override
    public EventListenersProcessor eventListenersProcessor(final KasperEventBus eventBus){
        return super.eventListenersProcessor(eventBus);
    }

    @Bean
    @Override
    public QueryServicesProcessor queryServicesProcessor(final QueryServicesLocator locator){
        return super.queryServicesProcessor(locator);
    }

    @Bean
    @Override
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        return super.domainsProcessor(domainLocator);
    }

    @Bean
    @Override
    public QueryGateway queryGateway(final QueryServicesLocator locator){
        return super.queryGateway(locator);
    }

}
