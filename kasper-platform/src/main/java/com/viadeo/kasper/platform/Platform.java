// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.platform;

import com.codahale.metrics.MetricRegistry;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.typesafe.config.Config;
import com.viadeo.kasper.core.component.command.DefaultRepositoryManager;
import com.viadeo.kasper.core.component.command.RepositoryManager;
import com.viadeo.kasper.core.component.command.gateway.CommandGateway;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.component.query.gateway.QueryGateway;
import com.viadeo.kasper.core.component.event.saga.SagaManager;
import com.viadeo.kasper.core.component.command.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.component.event.interceptor.EventInterceptorFactory;
import com.viadeo.kasper.core.component.query.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.core.resolvers.*;
import com.viadeo.kasper.platform.bundle.DomainBundle;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.platform.configuration.KasperPlatformConfiguration;
import com.viadeo.kasper.platform.configuration.PlatformConfiguration;
import com.viadeo.kasper.platform.plugin.Plugin;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * The Kasper platform
 * <p>
 * This interface represent the main entry point to your platform front components,
 * the Command and Query gateways from which your can then send commands and queries,
 * or even send Events.
 * </p>
 */
public interface Platform {

    /**
     * @return the Command gateway to use in order to send commands to the platform
     */
    CommandGateway getCommandGateway();

    /**
     * @return the query gateway to use in order to send queries to the platform
     */
    QueryGateway getQueryGateway();

    /**
     * @return the event bus used by the platform
     */
    KasperEventBus getEventBus();

    /**
     * @return the meta information of the platform
     */
    Meta getMeta();

    // ========================================================================

    /**
     * The platform builder
     */
    class Builder {

        private static final Logger LOGGER = LoggerFactory.getLogger(Builder.class);

        private final Collection<DomainBundle> domainBundles;
        private final Collection<Plugin> kasperPlugins;
        private final List<QueryInterceptorFactory> queryInterceptorFactories;
        private final List<CommandInterceptorFactory> commandInterceptorFactories;
        private final List<EventInterceptorFactory> eventInterceptorFactories;
        private final List<ExtraComponent> extraComponents;

        private DomainDescriptorFactory domainDescriptorFactory;
        private DomainHelper domainHelper;
        private KasperEventBus eventBus;
        private KasperCommandGateway commandGateway;
        private KasperQueryGateway queryGateway;
        private Config configuration;
        private RepositoryManager repositoryManager;
        private MetricRegistry metricRegistry;
        private Meta meta;
        private SagaManager sagaManager;
        private PlatformWirer platformWirer;

        // --------------------------------------------------------------------

        public Builder() {
            this(new DomainDescriptorFactory());
        }

        protected Builder(final DomainDescriptorFactory domainDescriptorFactory) {
            this(checkNotNull(domainDescriptorFactory), new DefaultRepositoryManager());
        }

        protected Builder(
                final DomainDescriptorFactory domainDescriptorFactory,
                final RepositoryManager repositoryManager
        ) {
            this.domainDescriptorFactory = checkNotNull(domainDescriptorFactory);
            this.repositoryManager = checkNotNull(repositoryManager);
            this.domainBundles = Lists.newArrayList();
            this.kasperPlugins = Lists.newArrayList();
            this.extraComponents = Lists.newArrayList();
            this.queryInterceptorFactories = Lists.newArrayList();
            this.commandInterceptorFactories = Lists.newArrayList();
            this.eventInterceptorFactories = Lists.newArrayList();
            this.domainHelper = new DomainHelper();
        }

        public Builder(final PlatformConfiguration platformConfiguration) {
            this();
            checkNotNull(platformConfiguration);
            this.eventBus = checkNotNull(platformConfiguration.eventBus());
            this.commandGateway = checkNotNull(platformConfiguration.commandGateway());
            this.queryGateway = checkNotNull(platformConfiguration.queryGateway());
            this.configuration = checkNotNull(platformConfiguration.configuration());
            this.metricRegistry = checkNotNull(platformConfiguration.metricRegistry());
            this.sagaManager = checkNotNull(platformConfiguration.sagaManager());
            this.extraComponents.addAll(checkNotNull(platformConfiguration.extraComponents()));
            this.queryInterceptorFactories.addAll(checkNotNull(platformConfiguration.queryInterceptorFactories()));
            this.commandInterceptorFactories.addAll(checkNotNull(platformConfiguration.commandInterceptorFactories()));
            this.eventInterceptorFactories.addAll(checkNotNull(platformConfiguration.eventInterceptorFactories()));
            this.domainDescriptorFactory = checkNotNull(platformConfiguration.domainDescriptorFactory());
        }

        // --------------------------------------------------------------------

        public Builder addDomainBundle(final DomainBundle domainBundle, final DomainBundle... domainBundles) {
            this.domainBundles.add(checkNotNull(domainBundle));
            with(this.domainBundles, domainBundles);
            return this;
        }

        public Builder addPlugin(final Plugin plugin, final Plugin... plugins) {
            this.kasperPlugins.add(checkNotNull(plugin));
            with(this.kasperPlugins, plugins);
            return this;
        }

        public Builder addQueryInterceptorFactory(final QueryInterceptorFactory factory, final QueryInterceptorFactory... factories) {
            this.queryInterceptorFactories.add(checkNotNull(factory));
            with(this.queryInterceptorFactories, factories);
            return this;
        }

        public Builder addCommandInterceptorFactory(final CommandInterceptorFactory factory, final CommandInterceptorFactory... factories) {
            this.commandInterceptorFactories.add(checkNotNull(factory));
            with(this.commandInterceptorFactories, factories);
            return this;
        }

        public Builder addEventInterceptorFactory(final EventInterceptorFactory factory, final EventInterceptorFactory... factories) {
            this.eventInterceptorFactories.add(checkNotNull(factory));
            with(this.eventInterceptorFactories, factories);
            return this;
        }

        public <E> Builder addExtraComponent(final String name, final Class<E> clazz, final E component) {
            checkNotNull(name);
            checkNotNull(clazz);
            checkNotNull(component);
            this.extraComponents.add(new ExtraComponent(name, clazz, component));
            return this;
        }

        public Builder withMeta(final Meta meta){
            this.meta = checkNotNull(meta);
            return this;
        }

        public Builder withConfiguration(final Config configuration) {
            this.configuration = checkNotNull(configuration);
            return this;
        }

        public Builder withEventBus(final KasperEventBus eventBus) {
            this.eventBus = checkNotNull(eventBus);
            return this;
        }

        public Builder withCommandGateway(final KasperCommandGateway commandGateway) {
            this.commandGateway = checkNotNull(commandGateway);
            return this;
        }

        public Builder withQueryGateway(final KasperQueryGateway queryGateway) {
            this.queryGateway = checkNotNull(queryGateway);
            return this;
        }

        public Builder withRepositoryManager(final RepositoryManager repositoryManager) {
            this.repositoryManager = checkNotNull(repositoryManager);
            return this;
        }

        public Builder withMetricRegistry(final MetricRegistry metricRegistry) {
            this.metricRegistry = checkNotNull(metricRegistry);
            return this;
        }

        public Builder withSagaManager(final SagaManager sagaManager) {
            this.sagaManager = checkNotNull(sagaManager);
            return this;
        }

        @SafeVarargs
        private final <COMP> void with(final Collection<COMP> collection, final COMP... components) {
            checkNotNull(collection);
            for (final COMP component : checkNotNull(components)) {
                collection.add(checkNotNull(component));
            }
        }

        @VisibleForTesting
        protected void setDomainHelper(final DomainHelper domainHelper){
            this.domainHelper = checkNotNull(domainHelper);
        }

        // --------------------------------------------------------------------

        public Platform build() {
            checkState((null != eventBus), "the event bus cannot be null");
            checkState((null != commandGateway), "the command gateway cannot be null");
            checkState((null != queryGateway), "the query gateway cannot be null");
            checkState((null != configuration), "the configuration cannot be null");
            checkState((null != repositoryManager), "the repository manager cannot be null");
            checkState((null != metricRegistry), "the metric registry cannot be null");
            checkState((null != sagaManager), "the saga manager cannot be null");

            this.platformWirer = new PlatformWirer(
                    configuration,
                    metricRegistry,
                    eventBus,
                    commandGateway,
                    queryGateway,
                    sagaManager,
                    repositoryManager
            );

            for (final ExtraComponent extraComponent : extraComponents) {
                this.platformWirer.register(extraComponent);
            }

            this.meta = Objects.firstNonNull(meta, new Meta("unknown", DateTime.now(), DateTime.now()));

            registerGlobalInterceptors(platformWirer);

            registerShutdownHook();

            initializeKasperMetrics(domainHelper);

            final Collection<DomainDescriptor> domainDescriptors = configureDomainBundles(platformWirer);

            final KasperPlatform platform = new KasperPlatform(commandGateway, queryGateway, eventBus, meta);

            initializePlugins(platform, domainDescriptors);

            LOGGER.info("Platform is ready (version:'{}', date:'{}')", meta.getVersion(), meta.getBuildingDate());

            return platform;
        }

        private void registerShutdownHook() {
            if ((null != eventBus.getShutdownHook()) /* for mocks... */
                && eventBus.getShutdownHook().isPresent()) {
                Runtime.getRuntime().addShutdownHook(new Thread(eventBus.getShutdownHook().get()));
                LOGGER.info("Registered shutdown hook : Event Processing");
            }
        }

        protected void registerGlobalInterceptors(PlatformWirer platformWirer) {
            for (final QueryInterceptorFactory interceptorFactory : queryInterceptorFactories) {
                platformWirer.wire(interceptorFactory);
            }

            for (final CommandInterceptorFactory interceptorFactory : commandInterceptorFactories) {
                platformWirer.wire(interceptorFactory);
            }

            for (final EventInterceptorFactory interceptorFactory : eventInterceptorFactories) {
                platformWirer.wire(interceptorFactory);
            }
        }

        protected Collection<DomainDescriptor> configureDomainBundles(PlatformWirer platformWirer) {
            final List<DomainDescriptor> domainDescriptors = Lists.newArrayList();

            for (final DomainBundle bundle : domainBundles) {
                domainDescriptors.add(configureDomainBundle(platformWirer, bundle));
            }

            return domainDescriptors;
        }

        protected DomainDescriptor configureDomainBundle(final PlatformWirer platformWirer, final DomainBundle bundle) {
            final DomainDescriptor descriptor = platformWirer.wire(bundle);

            domainHelper.add(DomainDescriptorFactory.mapToDomainClassByComponentClass(descriptor));

            return descriptor;
        }

        protected void initializePlugins(final Platform platform, final Collection<DomainDescriptor> domainDescriptors) {
            final DomainDescriptor[] domainDescriptorArray = domainDescriptors.toArray(new DomainDescriptor[domainDescriptors.size()]);

            for (final Plugin plugin : kasperPlugins) {
                LOGGER.debug("Initializing plugin : {}", plugin.getClass().getSimpleName());
                plugin.initialize(platform, metricRegistry, domainDescriptorArray);
            }
        }

        protected void initializeKasperMetrics(final DomainHelper domainHelper) {

            // FIXME here we declare resolver allowing to defined the name of metrics
            final ConceptResolver conceptResolver = new ConceptResolver();
            final RelationResolver relationResolver = new RelationResolver(conceptResolver);
            final EntityResolver entityResolver = new EntityResolver(conceptResolver, relationResolver);

            final DomainResolver domainResolver = new DomainResolver();
            domainResolver.setDomainHelper(domainHelper);

            final EventListenerResolver eventListenerResolver = new EventListenerResolver();
            eventListenerResolver.setDomainResolver(domainResolver);

            final CommandHandlerResolver commandHandlerResolver = new CommandHandlerResolver();
            commandHandlerResolver.setDomainResolver(domainResolver);

            final RepositoryResolver repositoryResolver = new RepositoryResolver(entityResolver);
            repositoryResolver.setDomainResolver(domainResolver);

            final QueryHandlerResolver queryHandlerResolver = new QueryHandlerResolver(domainResolver);

            final CommandResolver commandResolver = new CommandResolver();
            commandResolver.setCommandHandlerResolver(commandHandlerResolver);
            commandResolver.setDomainResolver(domainResolver);

            final QueryResolver queryResolver = new QueryResolver();
            queryResolver.setQueryHandlerResolver(queryHandlerResolver);
            queryResolver.setDomainResolver(domainResolver);

            final QueryResultResolver queryResultResolver = new QueryResultResolver();
            queryResultResolver.setQueryHandlerResolver(queryHandlerResolver);
            queryResultResolver.setDomainResolver(domainResolver);

            final EventResolver eventResolver = new EventResolver();
            eventResolver.setDomainResolver(domainResolver);

            final SagaResolver sagaResolver = new SagaResolver();
            sagaResolver.setDomainResolver(domainResolver);

            final ResolverFactory resolverFactory = new ResolverFactory();
            resolverFactory.setCommandHandlerResolver(commandHandlerResolver);
            resolverFactory.setEventListenerResolver(eventListenerResolver);
            resolverFactory.setEventResolver(eventResolver);
            resolverFactory.setRepositoryResolver(repositoryResolver);
            resolverFactory.setQueryHandlerResolver(queryHandlerResolver);
            resolverFactory.setCommandResolver(commandResolver);
            resolverFactory.setQueryResolver(queryResolver);
            resolverFactory.setQueryResultResolver(queryResultResolver);
            resolverFactory.setSagaResolver(sagaResolver);

            KasperMetrics.setResolverFactory(resolverFactory);
            KasperMetrics.setMetricRegistry(metricRegistry);
        }

    }

    // ========================================================================

    static class BuilderContext {

        private final Config configuration;
        private final KasperEventBus eventBus;
        private final CommandGateway commandGateway;
        private final QueryGateway queryGateway;
        private final MetricRegistry metricRegistry;
        private final Map<ExtraComponent.Key, ExtraComponent> extraComponents;

        // --------------------------------------------------------------------

        public BuilderContext(final KasperPlatformConfiguration platformConfiguration,
                              final List<ExtraComponent> extraComponents
        ) {
            this(
                platformConfiguration.configuration(),
                platformConfiguration.eventBus(),
                platformConfiguration.commandGateway(),
                platformConfiguration.queryGateway(),
                platformConfiguration.metricRegistry(),
                extraComponents
            );
        }

        public BuilderContext(final Config configuration,
                              final KasperEventBus eventBus,
                              final CommandGateway commandGateway,
                              final QueryGateway queryGateway,
                              final MetricRegistry metricRegistry,
                              final List<ExtraComponent> extraComponents
        ) {
            this.configuration = checkNotNull(configuration);
            this.eventBus = checkNotNull(eventBus);
            this.commandGateway = checkNotNull(commandGateway);
            this.queryGateway = checkNotNull(queryGateway);
            this.metricRegistry = checkNotNull(metricRegistry);

            checkNotNull(extraComponents);

            this.extraComponents = Maps.newHashMap();
            for (final ExtraComponent extraComponent : extraComponents) {
                this.extraComponents.put(extraComponent.getKey(), extraComponent);
            }
        }

        // --------------------------------------------------------------------

        public Config getConfiguration() {
            return configuration;
        }

        public KasperEventBus getEventBus() {
            return eventBus;
        }

        public CommandGateway getCommandGateway() {
            return commandGateway;
        }

        public QueryGateway getQueryGateway() {
            return queryGateway;
        }

        public MetricRegistry getMetricRegistry() {
            return metricRegistry;
        }

        @SuppressWarnings("unchecked")
        public <E> Optional<E> getExtraComponent(final String name, final Class<E> clazz) {
            return Optional.fromNullable((E) extraComponents.get(new ExtraComponent.Key(name, clazz)));
        }

        public List<ExtraComponent> getExtraComponents() {
            return Lists.newArrayList(extraComponents.values());
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(configuration, eventBus, commandGateway, queryGateway, metricRegistry, extraComponents);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            final BuilderContext other = (BuilderContext) obj;
            return Objects.equal(this.configuration, other.configuration) && Objects.equal(this.eventBus, other.eventBus) && Objects.equal(this.commandGateway, other.commandGateway) && Objects.equal(this.queryGateway, other.queryGateway) && Objects.equal(this.metricRegistry, other.metricRegistry) && Objects.equal(this.extraComponents, other.extraComponents);
        }


        @Override
        public String toString() {
            return Objects.toStringHelper(this)
                    .add("configuration", configuration)
                    .add("eventBus", eventBus)
                    .add("commandGateway", commandGateway)
                    .add("queryGateway", queryGateway)
                    .add("metricRegistry", metricRegistry)
                    .add("extraComponents", extraComponents)
                    .toString();
        }
    }
}
