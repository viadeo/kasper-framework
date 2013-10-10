// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.impl.KasperPlatform;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import org.axonframework.commandhandling.CommandBus;
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
    public DomainLocator domainLocator(final CommandHandlerResolver commandHandlerResolver) {
        return super.domainLocator(commandHandlerResolver);
    }

    @Bean
    @Override
    public QueryServicesLocator queryServicesLocator(){
        return super.queryServicesLocator();
    }

    @Bean
    @Override
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator, final KasperEventBus eventBus){
        return super.commandHandlersProcessor(commandBus, domainLocator, eventBus);
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
    public EventListenersProcessor eventListenersProcessor(final KasperEventBus eventBus, final CommandGateway commandGateway) {
        return super.eventListenersProcessor(eventBus, commandGateway);
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

    // ------------------------------------------------------------------------

    @Bean
    @Override
    public CommandHandlerResolver commandHandlerResolver() {
        return super.commandHandlerResolver();
    }

    @Bean
    @Override
    public DomainResolver domainResolver(
            final CommandResolver commandResolver,
            final EventListenerResolver eventListenerResolver,
            final QueryResolver queryResolver,
            final RepositoryResolver repositoryResolver
    ) {
        return super.domainResolver(commandResolver, eventListenerResolver, queryResolver, repositoryResolver);
    }

    @Bean
    @Override
    public CommandResolver commandResolver(
            final DomainLocator domainLocator,
            final CommandHandlerResolver commandHandlerResolver
    ) {
        return super.commandResolver(domainLocator, commandHandlerResolver);
    }

    @Bean
    @Override
    public EventListenerResolver eventListenerResolver(
            final EventResolver eventResolver
    ) {
        return super.eventListenerResolver(eventResolver);
    }

    @Bean
    @Override
    public QueryResolver queryResolver(
            final QueryServiceResolver queryServiceResolver,
            final QueryServicesLocator queryServicesLocator
    ) {
        return super.queryResolver(queryServiceResolver, queryServicesLocator);
    }

    @Bean
    @Override
    public QueryServiceResolver queryServiceResolver() {
        return super.queryServiceResolver();
    }

    @Bean
    @Override
    public RepositoryResolver repositoryResolver(final EntityResolver entityResolver) {
        return super.repositoryResolver(entityResolver);
    }

    @Bean
    @Override
    public EntityResolver entityResolver(
            final ConceptResolver conceptResolver,
            final RelationResolver relationResolver
    ) {
        return super.entityResolver(conceptResolver, relationResolver);
    }

    @Bean
    @Override
    public ConceptResolver conceptResolver() {
        return super.conceptResolver();
    }

    @Bean
    @Override
    public RelationResolver relationResolver()  {
        return super.relationResolver();
    }

    @Bean
    @Override
    public EventResolver eventResolver() {
        return super.eventResolver();
    }

}
