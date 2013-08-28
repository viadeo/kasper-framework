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
import com.viadeo.kasper.core.boot.*;
import com.viadeo.kasper.core.locators.DomainLocator;
import com.viadeo.kasper.core.locators.QueryServicesLocator;
import com.viadeo.kasper.core.locators.impl.DefaultDomainLocator;
import com.viadeo.kasper.core.locators.impl.DefaultQueryServicesLocator;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.cqrs.command.CommandGateway;
import com.viadeo.kasper.cqrs.query.QueryGateway;
import com.viadeo.kasper.cqrs.query.impl.DefaultQueryGateway;
import com.viadeo.kasper.exception.KasperException;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.components.commandbus.KasperCommandBus;
import com.viadeo.kasper.platform.components.eventbus.KasperHybridEventBus;
import com.viadeo.kasper.platform.impl.KasperPlatform;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.gateway.CommandGatewayFactoryBean;
import org.axonframework.eventhandling.EventBus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

import java.util.concurrent.TimeUnit;

public class DefaultPlatformConfiguration implements PlatformConfiguration {

    private static final String INSTANCE_NOT_YET_AVAILABLE = "Component %s cannot be retrieved : it has not yet been instanciated (platform not yet booted ?)";
    private static final String INSTANCE_ALREADY_CREATED = "Component %s has already been created !";

    protected final MutableClassToInstanceMap<Object> components = MutableClassToInstanceMap.create();

    protected <T> T getAvailableInstance(final Class<T> clazz) {
        if (components.containsKey(clazz)) {
            return components.getInstance(clazz);
        }
        throw new KasperException(String.format(INSTANCE_NOT_YET_AVAILABLE, clazz.getSimpleName()));
    }

    protected void ensureNotPresent(final Class<?> clazz) {
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
            , final EventBus eventBus
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

    @Override
    public EventBus eventBus() {
        if (components.containsKey(EventBus.class)) {
            return components.getInstance(EventBus.class);
        } else {

            final EventBus eventBus = new KasperHybridEventBus();

            components.putInstance(EventBus.class, eventBus);
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

            final CommandBus commandBus = new KasperCommandBus();

            components.putInstance(CommandBus.class, commandBus);
            return commandBus;
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

        components.putInstance(CommandGatewayFactoryBean.class, commandGatewayFactoryBean);
        return commandGatewayFactoryBean;
    }

    // ------------------------------------------------------------------------

    @Override
    public DomainLocator domainLocator() {
        if (components.containsKey(DomainLocator.class)) {
            return components.getInstance(DomainLocator.class);
        } else {
            final DomainLocator domainLocator = new DefaultDomainLocator();

            components.putInstance(DomainLocator.class, domainLocator);
            return domainLocator;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public QueryServicesLocator queryServicesLocator() {
        if (components.containsKey(QueryServicesLocator.class)) {
            return components.getInstance(QueryServicesLocator.class);
        } else {
            final QueryServicesLocator queryServicesLocator = new DefaultQueryServicesLocator();

            components.putInstance(QueryServicesLocator.class, queryServicesLocator);
            return queryServicesLocator;
        }
    }

    // ------------------------------------------------------------------------

    @Override
    public CommandHandlersProcessor commandHandlersProcessor(final CommandBus commandBus, final DomainLocator domainLocator) {
        this.ensureNotPresent(CommandHandlersProcessor.class);

        final CommandHandlersProcessor commandHandlersProcessor = new CommandHandlersProcessor();
        commandHandlersProcessor.setCommandBus(commandBus);
        commandHandlersProcessor.setDomainLocator(domainLocator);

        components.putInstance(CommandHandlersProcessor.class, commandHandlersProcessor);
        return commandHandlersProcessor;
    }

    @Override
    public CommandHandlersProcessor commandHandlersProcessor() {
        return this.getAvailableInstance(CommandHandlersProcessor.class);
    }

    // ------------------------------------------------------------------------

    @Override
    public RepositoriesProcessor repositoriesProcessor(final DomainLocator locator, final EventBus eventBus){
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
    public EventListenersProcessor eventListenersProcessor(final EventBus eventBus){
        this.ensureNotPresent(EventListenersProcessor.class);

        final EventListenersProcessor eventListenersProcessor = new EventListenersProcessor();
        eventListenersProcessor.setEventBus(eventBus);

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

    /**
     * Default Metrics reporter : SLF4J(DEBUG) reported once per minute
     */
    @Override
    public void initializeMetricsReporters() {

        final Logger platformLogger = LoggerFactory.getLogger(Platform.class);

        if (platformLogger.isDebugEnabled()) {
            final Slf4jReporter reporter = Slf4jReporter
                    .forRegistry(KasperMetrics.getRegistry())
                    .outputTo(platformLogger)
                    .markWith(MarkerFactory.getMarker("DEBUG"))
                    .convertDurationsTo(TimeUnit.MILLISECONDS)
                    .convertRatesTo(TimeUnit.SECONDS)
                    .build();
            reporter.start(20, TimeUnit.SECONDS);
        }

    }

}
