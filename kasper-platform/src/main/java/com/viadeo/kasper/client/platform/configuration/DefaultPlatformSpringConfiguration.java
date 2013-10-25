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
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
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
    public DomainLocator domainLocator(final CommandHandlerResolver commandHandlerResolver, final RepositoryResolver repositoryResolver) {
        return super.domainLocator(commandHandlerResolver, repositoryResolver);
    }

    @Bean
    @Override
    public QueryHandlersLocator queryHandlersLocator(final QueryHandlerResolver queryHandlerResolver) {
        return super.queryHandlersLocator(queryHandlerResolver);
    }

    @Bean
    @Override
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator,
                                                             final KasperEventBus eventBus, final CommandHandlerResolver commandHandlerResolver ){
        return super.commandHandlersProcessor(commandBus, domainLocator, eventBus, commandHandlerResolver);
    }

    @Bean
    @Override
    public RepositoriesProcessor repositoriesProcessor(final DomainLocator locator, final KasperEventBus eventBus){
        return super.repositoriesProcessor(locator, eventBus);
    }

    @Bean
    @Override
    public QueryHandlerFiltersProcessor queryHandlerFiltersProcessor(QueryHandlersLocator locator) {
        return super.queryHandlerFiltersProcessor(locator);
    }

    @Bean
    @Override
    public EventListenersProcessor eventListenersProcessor(final KasperEventBus eventBus, final CommandGateway commandGateway) {
        return super.eventListenersProcessor(eventBus, commandGateway);
    }

    @Bean
    @Override
    public QueryHandlersProcessor queryHandlersProcessor(final QueryHandlersLocator locator){
        return super.queryHandlersProcessor(locator);
    }

    @Bean
    @Override
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        return super.domainsProcessor(domainLocator);
    }

    @Bean
    @Override
    public QueryGateway queryGateway(final QueryHandlersLocator locator){
        return super.queryGateway(locator);
    }

    // ------------------------------------------------------------------------

    @Bean
    @Override
    public CommandHandlerResolver commandHandlerResolver(final DomainResolver domainResolver) {
        return super.commandHandlerResolver(domainResolver);
    }

    @Bean
    @Override
    public DomainResolver domainResolver() {
        return super.domainResolver();
    }

    @Bean
    @Override
    public CommandResolver commandResolver(
            final DomainLocator domainLocator,
            final DomainResolver domainResolver,
            final CommandHandlerResolver commandHandlerResolver
    ) {
        return super.commandResolver(domainLocator, domainResolver, commandHandlerResolver);
    }

    @Bean
    @Override
    public EventListenerResolver eventListenerResolver(
            final DomainResolver domainResolver
    ) {
        return super.eventListenerResolver(domainResolver);
    }

    @Bean
    @Override
    public QueryResolver queryResolver(
            final DomainResolver domainResolver,
            final QueryHandlerResolver queryHandlerResolver,
            final QueryHandlersLocator queryHandlersLocator
    ) {
        return super.queryResolver(domainResolver, queryHandlerResolver, queryHandlersLocator);
    }

    @Bean
    @Override
    public QueryAnswerResolver queryAnswerResolver(
            final DomainResolver domainResolver,
            final QueryServiceResolver queryServiceResolver,
            final QueryServicesLocator queryServicesLocator
    ) {
        return super.queryAnswerResolver(domainResolver, queryServiceResolver, queryServicesLocator);
    }

    @Bean
    @Override
    public QueryHandlerResolver queryHandlerResolver(final DomainResolver domainResolver) {
        return super.queryHandlerResolver(domainResolver);
    }

    @Bean
    @Override
    public RepositoryResolver repositoryResolver(final EntityResolver entityResolver, final DomainResolver domainResolver) {
        return super.repositoryResolver(entityResolver, domainResolver);
    }

    @Bean
    @Override
    public EntityResolver entityResolver(
            final ConceptResolver conceptResolver,
            final RelationResolver relationResolver,
            final DomainResolver domainResolver
    ) {
        return super.entityResolver(conceptResolver, relationResolver, domainResolver);
    }

    @Bean
    @Override
    public ConceptResolver conceptResolver(final DomainResolver domainResolver) {
        return super.conceptResolver(domainResolver);
    }

    @Bean
    @Override
    public RelationResolver relationResolver(final DomainResolver domainResolver, final ConceptResolver conceptResolver)  {
        return super.relationResolver(domainResolver, conceptResolver);
    }

    @Bean
    @Override
    public EventResolver eventResolver(final DomainResolver domainResolver) {
        return super.eventResolver(domainResolver);
    }

    @Bean
    @Override
    public ResolverFactory resolverFactory(
            final DomainResolver domainResolver,
            final CommandResolver commandResolver,
            final CommandHandlerResolver commandHandlerResolver,
            final EventListenerResolver eventListenerResolver,
            final QueryResolver queryResolver,
            final QueryHandlerResolver queryHandlerResolver,
            final RepositoryResolver repositoryResolver,
            final EntityResolver entityResolver,
            final ConceptResolver conceptResolver,
            final RelationResolver relationResolver,
            final EventResolver eventResolver
    ) {
        return super.resolverFactory(
                domainResolver, commandResolver,
                commandHandlerResolver, eventListenerResolver,
                queryResolver, queryHandlerResolver,
                repositoryResolver, entityResolver,
                conceptResolver, relationResolver, eventResolver);
    }

}
