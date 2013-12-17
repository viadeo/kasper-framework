// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.client.platform.configuration;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.google.common.base.Throwables;
import com.google.common.collect.MutableClassToInstanceMap;
import com.viadeo.kasper.client.platform.OldPlatform;
import com.viadeo.kasper.client.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.impl.OldKasperPlatform;
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryHandlersLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryHandlersLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.cqrs.command.impl.DefaultRepositoryManager;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import com.viadeo.kasper.exception.KasperException;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.CommandHandlerInterceptor;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.commandhandling.interceptors.BeanValidationInterceptor;
import org.axonframework.unitofwork.DefaultUnitOfWorkFactory;
import org.axonframework.unitofwork.UnitOfWorkFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import javax.validation.Validation;
import javax.validation.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Deprecated
public class DefaultOldPlatformConfiguration implements OldPlatformConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultOldPlatformConfiguration.class);

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

    public <T> void registerInstance(final Class<T> clazz, T instance) {
        components.putInstance(clazz, instance);
    }

    public boolean containsInstance(final Class<?> clazz) {
        return components.containsKey(clazz);
    }

    public <T> T getInstance(final Class<T> clazz) {
        return components.getInstance(clazz);
    }

    // ------------------------------------------------------------------------

    @Override
    public ComponentsInstanceManager getComponentsInstanceManager() {
        if (containsInstance(ComponentsInstanceManager.class)) {
            return getInstance(ComponentsInstanceManager.class);
        } else {

            final SimpleComponentsInstanceManager sman = new SimpleComponentsInstanceManager();

            registerInstance(ComponentsInstanceManager.class, sman);
            return sman;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public AnnotationRootProcessor annotationRootProcessor(final ComponentsInstanceManager instancesManager){
        this.ensureNotPresent(AnnotationRootProcessor.class);

        final AnnotationRootProcessor rootProcessor =  new AnnotationRootProcessor();
        rootProcessor.setComponentsInstanceManager(instancesManager);

        registerInstance(AnnotationRootProcessor.class, rootProcessor);
        return rootProcessor;
    }

    @Override
    public AnnotationRootProcessor annotationRootProcessor() {
        return this.getAvailableInstance(AnnotationRootProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public OldKasperPlatform kasperPlatform(final CommandGateway commandGateway
            , final QueryGateway queryGateway
            , final KasperEventBus eventBus
            , final AnnotationRootProcessor annotationRootProcessor) {

        this.ensureNotPresent(OldKasperPlatform.class);

        final OldKasperPlatform kasperPlatform = new OldKasperPlatform();
        kasperPlatform.setCommandGateway(commandGateway);
        kasperPlatform.setQueryGateway(queryGateway);
        kasperPlatform.setRootProcessor(annotationRootProcessor);
        kasperPlatform.setEventBus(eventBus);

        registerInstance(OldKasperPlatform.class, kasperPlatform);

         /* Initialize metrics reporters */
        this.initializeMetricsReporters();

        return kasperPlatform;
    }

    @Override
    public OldKasperPlatform kasperPlatform() {
        return this.getAvailableInstance(OldKasperPlatform.class);
    }

    // ------------------------------------------------------------------------

    // FIXME: put a configurable policy (then specific bean)
    @Override
    public KasperEventBus eventBus() {
        if (containsInstance(KasperEventBus.class)) {
            return getInstance(KasperEventBus.class);
        } else {
            final KasperEventBus eventBus = new KasperEventBus();

            registerInstance(KasperEventBus.class, eventBus);
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

            registerInstance(CommandGateway.class, commandGateway);
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
    public CommandBus commandBus(final UnitOfWorkFactory uowFactory) {
        this.ensureNotPresent(CommandBus.class);

        final KasperCommandBus commandBus = new KasperCommandBus();
        commandBus.setHandlerInterceptors(commandHandlerInterceptors());
        commandBus.setUnitOfWorkFactory(uowFactory);

        registerInstance(CommandBus.class, commandBus);
        return commandBus;
    }

    @Override
    public CommandBus commandBus() {
        return this.getAvailableInstance(CommandBus.class);
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

    @Override
    public UnitOfWorkFactory uowFactory() {
        if (containsInstance(UnitOfWorkFactory.class)) {
            return getInstance(UnitOfWorkFactory.class);
        } else {
            final UnitOfWorkFactory uowFactory = new DefaultUnitOfWorkFactory();
            registerInstance(UnitOfWorkFactory.class, uowFactory);
            return uowFactory;
        }
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

        registerInstance(CommandGatewayFactoryBean.class, commandGatewayFactoryBean);
        return commandGatewayFactoryBean;
    }

    // ------------------------------------------------------------------------

    @Override
    public RepositoryManager repositoryManager() {
        if (containsInstance(RepositoryManager.class)) {
            return getInstance(RepositoryManager.class);
        }

        RepositoryManager repositoryManager = new DefaultRepositoryManager();
        registerInstance(RepositoryManager.class, repositoryManager);
        return repositoryManager;
    }

    @Override
    public DomainLocator domainLocator(final CommandHandlerResolver commandHandlerResolver, final RepositoryResolver repositoryResolver) {
        this.ensureNotPresent(DomainLocator.class);

        final DefaultDomainLocator domainLocator = new DefaultDomainLocator();
        domainLocator.setCommandHandlerResolver(commandHandlerResolver);

        registerInstance(DomainLocator.class, domainLocator);
        return domainLocator;
    }

    @Override
    public DomainLocator domainLocator() {
        return this.getAvailableInstance(DomainLocator.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryHandlersLocator queryHandlersLocator(final QueryHandlerResolver queryHandlerResolver) {
        this.ensureNotPresent(QueryHandlersLocator.class);

        final DefaultQueryHandlersLocator queryHandlersLocator = new DefaultQueryHandlersLocator(queryHandlerResolver);

        registerInstance(QueryHandlersLocator.class, queryHandlersLocator);
        return queryHandlersLocator;
    }

    @Override
    public QueryHandlersLocator queryHandlersLocator() {
        return this.getAvailableInstance(QueryHandlersLocator.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandHandlersProcessor commandHandlersProcessor(
            final CommandBus commandBus,
            final DomainLocator domainLocator,
            final RepositoryManager repositoryManager,
            final KasperEventBus eventBus,
            final CommandHandlerResolver commandHandlerResolver
    ) {
        this.ensureNotPresent(CommandHandlersProcessor.class);

        final CommandHandlersProcessor commandHandlersProcessor = new CommandHandlersProcessor();
        commandHandlersProcessor.setCommandBus(commandBus);
        commandHandlersProcessor.setDomainLocator(domainLocator);
        commandHandlersProcessor.setRepositoryManager(repositoryManager);
        commandHandlersProcessor.setEventBus(eventBus);
        commandHandlersProcessor.setCommandHandlerResolver(commandHandlerResolver);

        registerInstance(CommandHandlersProcessor.class, commandHandlersProcessor);
        return commandHandlersProcessor;
    }

    @Override
    public CommandHandlersProcessor commandHandlersProcessor() {
        return this.getAvailableInstance(CommandHandlersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public RepositoriesProcessor repositoriesProcessor(final RepositoryManager repositoryManager, final KasperEventBus eventBus){
        this.ensureNotPresent(RepositoriesProcessor.class);

        final RepositoriesProcessor repositoriesProcessor = new RepositoriesProcessor();
        repositoriesProcessor.setRepositoryManager(repositoryManager);
        repositoriesProcessor.setEventBus(eventBus);

        registerInstance(RepositoriesProcessor.class, repositoriesProcessor);
        return repositoriesProcessor;
    }

    @Override
    public RepositoriesProcessor repositoriesProcessor() {
        return this.getAvailableInstance(RepositoriesProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryHandlerAdaptersProcessor queryHandlerAdaptersProcessor(QueryHandlersLocator locator) {
        this.ensureNotPresent(QueryHandlerAdaptersProcessor.class);

        final QueryHandlerAdaptersProcessor queryHandlerAdaptersProcessor = new QueryHandlerAdaptersProcessor();
        queryHandlerAdaptersProcessor.setQueryHandlersLocator(locator);

        registerInstance(QueryHandlerAdaptersProcessor.class, queryHandlerAdaptersProcessor);
        return queryHandlerAdaptersProcessor;
    }

    @Override
    public QueryHandlerAdaptersProcessor queryHandlerAdaptersProcessor() {
        return this.getAvailableInstance(QueryHandlerAdaptersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public EventListenersProcessor eventListenersProcessor(final KasperEventBus eventBus, final CommandGateway commandGateway){
        this.ensureNotPresent(EventListenersProcessor.class);

        final EventListenersProcessor eventListenersProcessor = new EventListenersProcessor();
        eventListenersProcessor.setEventBus(eventBus);
        eventListenersProcessor.setCommandGateway(commandGateway);

        registerInstance(EventListenersProcessor.class, eventListenersProcessor);
        return eventListenersProcessor;
    }

    @Override
    public EventListenersProcessor eventListenersProcessor() {
        return this.getAvailableInstance(EventListenersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryHandlersProcessor queryHandlersProcessor(final QueryHandlersLocator locator){
        this.ensureNotPresent(QueryHandlersProcessor.class);

        final QueryHandlersProcessor queryHandlersProcessor = new QueryHandlersProcessor();
        queryHandlersProcessor.setQueryHandlersLocator(locator);

        registerInstance(QueryHandlersProcessor.class, queryHandlersProcessor);
        return queryHandlersProcessor;
    }

    @Override
    public QueryHandlersProcessor queryHandlersProcessor() {
        return this.getAvailableInstance(QueryHandlersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public DomainsProcessor domainsProcessor(final DomainLocator domainLocator){
        this.ensureNotPresent(DomainsProcessor.class);

        final DomainsProcessor domainsProcessor = new DomainsProcessor();
        domainsProcessor.setDomainLocator(domainLocator);

        registerInstance(DomainsProcessor.class, domainsProcessor);
        return domainsProcessor;
    }

    @Override
    public DomainsProcessor domainsProcessor() {
        return this.getAvailableInstance(DomainsProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryGateway queryGateway(final QueryHandlersLocator locator){
        this.ensureNotPresent(QueryGateway.class);

        final KasperQueryGateway queryGateway = new KasperQueryGateway(locator);

        registerInstance(QueryGateway.class, queryGateway);
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

        registerInstance(CommandHandlerResolver.class, commandHandlerResolver);
        return commandHandlerResolver;
    }

    @Override
    public CommandHandlerResolver commandHandlerResolver() {
        return this.getAvailableInstance(CommandHandlerResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public DomainResolver domainResolver() {
        if (containsInstance(DomainResolver.class)) {
            return getInstance(DomainResolver.class);
        } else {
            this.ensureNotPresent(DomainResolver.class);

            final DomainResolver domainResolver = new DomainResolver();

            registerInstance(DomainResolver.class, domainResolver);
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

        registerInstance(CommandResolver.class, commandResolver);
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

        registerInstance(EventListenerResolver.class, eventListenerResolver);
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

        registerInstance(EventResolver.class, eventResolver);
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
        final QueryHandlerResolver queryHandlerResolver,
        final QueryHandlersLocator queryHandlersLocator
    ) {
        this.ensureNotPresent(QueryResolver.class);

        final QueryResolver queryResolver = new QueryResolver();
        queryResolver.setQueryHandlerResolver(queryHandlerResolver);
        queryResolver.setQueryHandlersLocator(queryHandlersLocator);
        queryResolver.setDomainResolver(domainResolver);

        registerInstance(QueryResolver.class, queryResolver);
        return queryResolver;
    }

    @Override
    public QueryResolver queryResolver() {
        return this.getAvailableInstance(QueryResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryResultResolver queryResultResolver(
        final DomainResolver domainResolver,
        final QueryHandlerResolver queryHandlerResolver,
        final QueryHandlersLocator queryHandlersLocator
    ) {
        this.ensureNotPresent(QueryResultResolver.class);

        final QueryResultResolver queryResultResolver = new QueryResultResolver();
        queryResultResolver.setQueryHandlerResolver(queryHandlerResolver);
        queryResultResolver.setQueryHandlersLocator(queryHandlersLocator);
        queryResultResolver.setDomainResolver(domainResolver);

        registerInstance(QueryResultResolver.class, queryResultResolver);
        return queryResultResolver;
    }

    @Override
    public QueryResultResolver queryResultResolver() {
        return this.getAvailableInstance(QueryResultResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryHandlerResolver queryHandlerResolver(final DomainResolver domainResolver) {
        this.ensureNotPresent(QueryHandlerResolver.class);

        final QueryHandlerResolver queryHandlerResolver = new QueryHandlerResolver();
        queryHandlerResolver.setDomainResolver(domainResolver);

        registerInstance(QueryHandlerResolver.class, queryHandlerResolver);
        return queryHandlerResolver;
    }

    @Override
    public QueryHandlerResolver queryHandlerResolver() {
        return this.getAvailableInstance(QueryHandlerResolver.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public RepositoryResolver repositoryResolver(final EntityResolver entityResolver, final DomainResolver domainResolver) {
        this.ensureNotPresent(RepositoryResolver.class);

        final RepositoryResolver repositoryResolver = new RepositoryResolver();
        repositoryResolver.setEntityResolver(entityResolver);
        repositoryResolver.setDomainResolver(domainResolver);

        registerInstance(RepositoryResolver.class, repositoryResolver);
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

        registerInstance(EntityResolver.class, entityResolver);
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

        registerInstance(ConceptResolver.class, conceptResolver);
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

        registerInstance(RelationResolver.class, relationResolver);
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
            QueryResultResolver queryResultResolver,
            QueryHandlerResolver queryHandlerResolver,
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
        resolverFactory.setQueryResultResolver(queryResultResolver);
        resolverFactory.setQueryHandlerResolver(queryHandlerResolver);
        resolverFactory.setRepositoryResolver(repositoryResolver);
        resolverFactory.setEntityResolver(entityResolver);
        resolverFactory.setConceptResolver(conceptResolver);
        resolverFactory.setRelationResolver(relationResolver);
        resolverFactory.setEventResolver(eventResolver);

        registerInstance(ResolverFactory.class, resolverFactory);
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

        final Logger platformLogger = LoggerFactory.getLogger(OldPlatform.class);

        if (platformLogger.isTraceEnabled()) {
            final Slf4jReporter reporter = Slf4jReporter
                    .forRegistry(KasperMetrics.getMetricRegistry())
                    .outputTo(platformLogger)
                    .markWith(MarkerFactory.getMarker("TRACE"))
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .build();
            reporter.start(DEFAULT_METRICS_TIMEOUT_SECONDS, TimeUnit.SECONDS);
        }

    }

    public MetricRegistry metricRegistry() {
        this.ensureNotPresent(MetricRegistry.class);

        MetricRegistry metricRegistry = new MetricRegistry();

        registerInstance(MetricRegistry.class, metricRegistry);
        return metricRegistry;
    }

}
