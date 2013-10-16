// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform.configuration;

import com.codahale.metrics.Slf4jReporter;
import com.google.common.base.Throwables;
import com.google.common.collect.MutableClassToInstanceMap;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.platform.impl.KasperPlatform;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class DefaultPlatformConfiguration implements PlatformConfiguration {
    private final static Logger LOGGER = LoggerFactory.getLogger(DefaultPlatformConfiguration.class);

    private static final String INSTANCE_NOT_YET_AVAILABLE = "Component %s cannot be retrieved : it has not yet been instanciated (platform not yet booted ?)";
    private static final String INSTANCE_ALREADY_CREATED = "Component %s has already been created !";

    protected final MutableClassToInstanceMap<Object> components = MutableClassToInstanceMap.create();

    protected <T> T getAvailableInstance(final Class<T> clazz) {
        if (components.containsKey(clazz)) {
            return components.getInstance(clazz);
        }
        throw new KasperException(String.format(INSTANCE_NOT_YET_AVAILABLE, clazz.getSimpleName()));
    }

    protected void ensureNotPresent(final Class clazz) {
        if (components.containsKey(clazz)) {
            throw new KasperException(String.format(INSTANCE_ALREADY_CREATED, clazz.getSimpleName()));
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        if (components.containsKey(ComponentsInstanceManager.class)) {
            return components.getInstance(ComponentsInstanceManager.class);
        } else {

            final SimpleComponentsInstanceManager sman = new SimpleComponentsInstanceManager();

            components.putInstance(ComponentsInstanceManager.class, sman);
            return sman;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public AnnotationRootProcessor annotationRootProcessor(final ComponentsInstanceManager instancesManager){
        this.ensureNotPresent(AnnotationRootProcessor.class);

        final AnnotationRootProcessor rootProcessor =  new AnnotationRootProcessor();
        rootProcessor.setComponentsInstanceManager(instancesManager);

        components.putInstance(AnnotationRootProcessor.class, rootProcessor);
        return rootProcessor;
    }

    @Override
    public AnnotationRootProcessor annotationRootProcessor() {
        return this.getAvailableInstance(AnnotationRootProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public KasperPlatform kasperPlatform(final CommandGateway commandGateway
            , final QueryGateway queryGateway
            , final KasperEventBus eventBus
            , final AnnotationRootProcessor annotationRootProcessor) {

        this.ensureNotPresent(KasperPlatform.class);

        final KasperPlatform kasperPlatform = new KasperPlatform();
        kasperPlatform.setCommandGateway(commandGateway);
        kasperPlatform.setQueryGateway(queryGateway);
        kasperPlatform.setRootProcessor(annotationRootProcessor);
        kasperPlatform.setEventBus(eventBus);

        components.putInstance(KasperPlatform.class, kasperPlatform);

         /* Initialize metrics reporters */
        this.initializeMetricsReporters();

        return kasperPlatform;
    }

    @Override
    public KasperPlatform kasperPlatform() {
        return this.getAvailableInstance(KasperPlatform.class);
    }

    // ------------------------------------------------------------------------

    // FIXME: put a configurable policy (then specific bean)
    @Override
    public KasperEventBus eventBus() {
        if (components.containsKey(KasperEventBus.class)) {
            return components.getInstance(KasperEventBus.class);
        } else {
            final KasperEventBus eventBus = new KasperEventBus();

            components.putInstance(KasperEventBus.class, eventBus);
            return eventBus;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandGateway commandGateway(final CommandBus commandBus) {
        this.ensureNotPresent(CommandGateway.class);

        try {

            final CommandGatewayFactoryBean commandGatewayFactoryBean = commandGatewayFactoryBean(commandBus);
            final CommandGateway commandGateway = (CommandGateway) commandGatewayFactoryBean.getObject();

            components.putInstance(CommandGateway.class, commandGateway);
            return commandGateway;

        } catch (final Exception e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public CommandGateway commandGateway() {
        return this.getAvailableInstance(CommandGateway.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandBus commandBus() {
        if (components.containsKey(CommandBus.class)) {
            return components.getInstance(CommandBus.class);
        } else {

            final KasperCommandBus commandBus = new KasperCommandBus();
            commandBus.setHandlerInterceptors(commandHandlerInterceptors());

            components.putInstance(CommandBus.class, commandBus);
            return commandBus;
        }
    }

    protected List<CommandHandlerInterceptor> commandHandlerInterceptors() {
        final List<CommandHandlerInterceptor> interceptors = new ArrayList<>();
        try {
            interceptors.add(new BeanValidationInterceptor(Validation.buildDefaultValidatorFactory()));
        } catch (final ValidationException ve) {
            LOGGER.warn("No implementation found for BEAN VALIDATION - JSR 303", ve);
        }
        return interceptors;
    }

    // ------------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    protected CommandGatewayFactoryBean commandGatewayFactoryBean(final CommandBus commandBus) {
        this.ensureNotPresent(CommandGatewayFactoryBean.class);

        final CommandGatewayFactoryBean commandGatewayFactoryBean = new CommandGatewayFactoryBean();
        commandGatewayFactoryBean.setCommandBus(commandBus);
        commandGatewayFactoryBean.setGatewayInterface(CommandGateway.class);

        try {
            commandGatewayFactoryBean.afterPropertiesSet();
        } catch (final Exception e) {
            throw new KasperException("Unable to bind Axon Command Gateway", e);
        }

        components.putInstance(CommandGatewayFactoryBean.class, commandGatewayFactoryBean);
        return commandGatewayFactoryBean;
    }

    // ------------------------------------------------------------------------

    @Override
    public DomainLocator domainLocator(final CommandHandlerResolver commandHandlerResolver, final RepositoryResolver repositoryResolver) {
        this.ensureNotPresent(DomainLocator.class);

        final DefaultDomainLocator domainLocator = new DefaultDomainLocator();
        domainLocator.setCommandHandlerResolver(commandHandlerResolver);
        domainLocator.setRepositoryResolver(repositoryResolver);

        components.putInstance(DomainLocator.class, domainLocator);
        return domainLocator;
    }

    @Override
    public DomainLocator domainLocator() {
        return this.getAvailableInstance(DomainLocator.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryServicesLocator queryServicesLocator(final QueryServiceResolver queryServiceResolver) {
        this.ensureNotPresent(QueryServicesLocator.class);

        final DefaultQueryServicesLocator queryServicesLocator = new DefaultQueryServicesLocator();
        queryServicesLocator.setQueryServiceResolver(queryServiceResolver);

        components.putInstance(QueryServicesLocator.class, queryServicesLocator);
        return queryServicesLocator;
    }

    @Override
    public QueryServicesLocator queryServicesLocator() {
        return this.getAvailableInstance(QueryServicesLocator.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandHandlersProcessor commandHandlersProcessor(
            final CommandBus commandBus,
            final DomainLocator domainLocator,
            final KasperEventBus eventBus,
            final CommandHandlerResolver commandHandlerResolver
    ) {
        this.ensureNotPresent(CommandHandlersProcessor.class);

        final CommandHandlersProcessor commandHandlersProcessor = new CommandHandlersProcessor();
        commandHandlersProcessor.setCommandBus(commandBus);
        commandHandlersProcessor.setDomainLocator(domainLocator);
        commandHandlersProcessor.setEventBus(eventBus);
        commandHandlersProcessor.setCommandHandlerResolver(commandHandlerResolver);

        components.putInstance(CommandHandlersProcessor.class, commandHandlersProcessor);
        return commandHandlersProcessor;
    }

    @Override
    public CommandHandlersProcessor commandHandlersProcessor() {
        return this.getAvailableInstance(CommandHandlersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public RepositoriesProcessor repositoriesProcessor(final DomainLocator locator, final KasperEventBus eventBus){
        this.ensureNotPresent(RepositoriesProcessor.class);

        final RepositoriesProcessor repositoriesProcessor = new RepositoriesProcessor();
        repositoriesProcessor.setDomainLocator(locator);
        repositoriesProcessor.setEventBus(eventBus);

        components.putInstance(RepositoriesProcessor.class, repositoriesProcessor);
        return repositoriesProcessor;
    }

    @Override
    public RepositoriesProcessor repositoriesProcessor() {
        return this.getAvailableInstance(RepositoriesProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public ServiceFiltersProcessor serviceFiltersProcessor(QueryServicesLocator locator) {
        this.ensureNotPresent(ServiceFiltersProcessor.class);

        final ServiceFiltersProcessor serviceFiltersProcessor = new ServiceFiltersProcessor();
        serviceFiltersProcessor.setQueryServicesLocator(locator);

        components.putInstance(ServiceFiltersProcessor.class, serviceFiltersProcessor);
        return serviceFiltersProcessor;
    }

    @Override
    public ServiceFiltersProcessor serviceFiltersProcessor() {
        return this.getAvailableInstance(ServiceFiltersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventListenersProcessor eventListenersProcessor(final KasperEventBus eventBus, final CommandGateway commandGateway){
        this.ensureNotPresent(EventListenersProcessor.class);

        final EventListenersProcessor eventListenersProcessor = new EventListenersProcessor();
        eventListenersProcessor.setEventBus(eventBus);
        eventListenersProcessor.setCommandGateway(commandGateway);

        components.putInstance(EventListenersProcessor.class, eventListenersProcessor);
        return eventListenersProcessor;
    }

    @Override
    public EventListenersProcessor eventListenersProcessor() {
        return this.getAvailableInstance(EventListenersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryServicesProcessor queryServicesProcessor(final QueryServicesLocator locator){
        this.ensureNotPresent(QueryServicesProcessor.class);

        final QueryServicesProcessor queryServicesProcessor = new QueryServicesProcessor();
        queryServicesProcessor.setQueryServicesLocator(locator);

        components.putInstance(QueryServicesProcessor.class, queryServicesProcessor);
        return queryServicesProcessor;
    }

    @Override
    public QueryServicesProcessor queryServicesProcessor() {
        return this.getAvailableInstance(QueryServicesProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        this.ensureNotPresent(DomainsProcessor.class);

        final DomainsProcessor domainsProcessor = new DomainsProcessor();
        domainsProcessor.setDomainLocator(domainLocator);

        components.putInstance(DomainsProcessor.class, domainsProcessor);
        return domainsProcessor;
    }

    @Override
    public DomainsProcessor domainsProcessor() {
        return this.getAvailableInstance(DomainsProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryGateway queryGateway(final QueryServicesLocator locator){
        this.ensureNotPresent(QueryGateway.class);

        final DefaultQueryGateway queryGateway = new DefaultQueryGateway();
        queryGateway.setQueryServicesLocator(locator);

        components.putInstance(QueryGateway.class, queryGateway);
        return queryGateway;
    }

    @Override
    public QueryGateway queryGateway() {
        return this.getAvailableInstance(QueryGateway.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandHandlerResolver commandHandlerResolver(final DomainResolver domainResolver) {
        this.ensureNotPresent(CommandHandlerResolver.class);

        final CommandHandlerResolver commandHandlerResolver = new CommandHandlerResolver();
        commandHandlerResolver.setDomainResolver(domainResolver);

        components.putInstance(CommandHandlerResolver.class, commandHandlerResolver);
        return commandHandlerResolver;
    }

    @Override
    public CommandHandlerResolver commandHandlerResolver() {
        return this.getAvailableInstance(CommandHandlerResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public DomainResolver domainResolver() {
        if (components.containsKey(DomainResolver.class)) {
            return components.getInstance(DomainResolver.class);
        } else {
            this.ensureNotPresent(DomainResolver.class);

            final DomainResolver domainResolver = new DomainResolver();

            components.putInstance(DomainResolver.class, domainResolver);
            return domainResolver;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandResolver commandResolver(
            final DomainLocator domainLocator,
            final DomainResolver domainResolver,
            final CommandHandlerResolver commandHandlerResolver
    ) {
        this.ensureNotPresent(CommandResolver.class);

        final CommandResolver commandResolver = new CommandResolver();
        commandResolver.setDomainLocator(domainLocator);
        commandResolver.setCommandHandlerResolver(commandHandlerResolver);
        commandResolver.setDomainResolver(domainResolver);

        components.putInstance(CommandResolver.class, commandResolver);
        return commandResolver;
    }

    @Override
    public CommandResolver commandResolver() {
        return this.getAvailableInstance(CommandResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventListenerResolver eventListenerResolver(
            final DomainResolver domainResolver
    ) {
        this.ensureNotPresent(EventListenerResolver.class);

        final EventListenerResolver eventListenerResolver = new EventListenerResolver();
        eventListenerResolver.setDomainResolver(domainResolver);

        components.putInstance(EventListenerResolver.class, eventListenerResolver);
        return eventListenerResolver;
    }

    @Override
    public EventListenerResolver eventListenerResolver() {
        return this.getAvailableInstance(EventListenerResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventResolver eventResolver(final DomainResolver domainResolver) {
        this.ensureNotPresent(EventResolver.class);

        final EventResolver eventResolver = new EventResolver();
        eventResolver.setDomainResolver(domainResolver);

        components.putInstance(EventResolver.class, eventResolver);
        return eventResolver;
    }

    @Override
    public EventResolver eventResolver() {
        return this.getAvailableInstance(EventResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResolver queryResolver(
        final DomainResolver domainResolver,
        final QueryServiceResolver queryServiceResolver,
        final QueryServicesLocator queryServicesLocator
    ) {
        this.ensureNotPresent(QueryResolver.class);

        final QueryResolver queryResolver = new QueryResolver();
        queryResolver.setQueryServiceResolver(queryServiceResolver);
        queryResolver.setQueryServicesLocator(queryServicesLocator);
        queryResolver.setDomainResolver(domainResolver);

        components.putInstance(QueryResolver.class, queryResolver);
        return queryResolver;
    }

    @Override
    public QueryResolver queryResolver() {
        return this.getAvailableInstance(QueryResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryServiceResolver queryServiceResolver(final DomainResolver domainResolver) {
        this.ensureNotPresent(QueryServiceResolver.class);

        final QueryServiceResolver queryServiceResolver = new QueryServiceResolver();
        queryServiceResolver.setDomainResolver(domainResolver);

        components.putInstance(QueryServiceResolver.class, queryServiceResolver);
        return queryServiceResolver;
    }

    @Override
    public QueryServiceResolver queryServiceResolver() {
        return this.getAvailableInstance(QueryServiceResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public RepositoryResolver repositoryResolver(final EntityResolver entityResolver, final DomainResolver domainResolver) {
        this.ensureNotPresent(RepositoryResolver.class);

        final RepositoryResolver repositoryResolver = new RepositoryResolver();
        repositoryResolver.setEntityResolver(entityResolver);
        repositoryResolver.setDomainResolver(domainResolver);

        components.putInstance(RepositoryResolver.class, repositoryResolver);
        return repositoryResolver;
    }

    @Override
    public RepositoryResolver repositoryResolver() {
        return this.getAvailableInstance(RepositoryResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public EntityResolver entityResolver(
            final ConceptResolver conceptResolver,
            final RelationResolver relationResolver,
            final DomainResolver domainResolver
    ) {
        this.ensureNotPresent(EntityResolver.class);

        final EntityResolver entityResolver = new EntityResolver();
        entityResolver.setConceptResolver(conceptResolver);
        entityResolver.setRelationResolver(relationResolver);
        entityResolver.setDomainResolver(domainResolver);

        components.putInstance(EntityResolver.class, entityResolver);
        return entityResolver;
    }

    @Override
    public EntityResolver entityResolver() {
        return this.getAvailableInstance(EntityResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public ConceptResolver conceptResolver(final DomainResolver domainResolver) {
        this.ensureNotPresent(ConceptResolver.class);

        final ConceptResolver conceptResolver = new ConceptResolver();
        conceptResolver.setDomainResolver(domainResolver);

        components.putInstance(ConceptResolver.class, conceptResolver);
        return conceptResolver;
    }

    @Override
    public ConceptResolver conceptResolver() {
        return this.getAvailableInstance(ConceptResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public RelationResolver relationResolver(final DomainResolver domainResolver, final ConceptResolver conceptResolver) {
        this.ensureNotPresent(RelationResolver.class);

        final RelationResolver relationResolver = new RelationResolver();
        relationResolver.setDomainResolver(domainResolver);
        relationResolver.setConceptResolver(conceptResolver);

        components.putInstance(RelationResolver.class, relationResolver);
        return relationResolver;
    }

    @Override
    public RelationResolver relationResolver() {
        return this.getAvailableInstance(RelationResolver.class);
    }

    // -----------------------------------------------------------------------

    @Override
    public ResolverFactory resolverFactory(
            DomainResolver domainResolver,
            CommandResolver commandResolver,
            CommandHandlerResolver commandHandlerResolver,
            EventListenerResolver eventListenerResolver,
            QueryResolver queryResolver,
            QueryServiceResolver queryServiceResolver,
            RepositoryResolver repositoryResolver,
            EntityResolver entityResolver,
            ConceptResolver conceptResolver,
            RelationResolver relationResolver,
            EventResolver eventResolver
    ) {
        this.ensureNotPresent(ResolverFactory.class);

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

        components.putInstance(ResolverFactory.class, resolverFactory);
        return resolverFactory;
    }

    @Override
    public ResolverFactory resolverFactory() {
        return this.getAvailableInstance(ResolverFactory.class);
    }

    // ------------------------------------------------------------------------

    protected static final int DEFAULT_METRICS_TIMEOUT_SECONDS = 20;

    /**
     * Default Metrics reporter : SLF4J(DEBUG) reported once per minute
     */
    @Override
    public void initializeMetricsReporters() {

        final Logger platformLogger = LoggerFactory.getLogger(Platform.class);

        if (platformLogger.isTraceEnabled()) {
            final Slf4jReporter reporter = Slf4jReporter
                    .forRegistry(KasperMetrics.getRegistry())
                    .outputTo(platformLogger)
                    .markWith(MarkerFactory.getMarker("TRACE"))
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .build();
            reporter.start(DEFAULT_METRICS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

    }

}
