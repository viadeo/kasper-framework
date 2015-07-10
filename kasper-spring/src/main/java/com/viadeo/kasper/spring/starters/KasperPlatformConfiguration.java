package com.viadeo.kasper.spring.starters;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.IDBuilder;
import com.viadeo.kasper.client.platform.Build;
import com.viadeo.kasper.client.platform.Platform;
import com.viadeo.kasper.client.platform.components.eventbus.KasperEventBus;
import com.viadeo.kasper.client.platform.domain.DomainBundle;
import com.viadeo.kasper.client.platform.domain.descriptor.DescriptorRegistry;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptor;
import com.viadeo.kasper.client.platform.domain.descriptor.DomainDescriptorFactory;
import com.viadeo.kasper.context.ContextHelper;
import com.viadeo.kasper.context.Version;
import com.viadeo.kasper.core.context.ContextVersion;
import com.viadeo.kasper.core.context.DefaultContextHelper;
import com.viadeo.kasper.core.interceptor.CommandInterceptorFactory;
import com.viadeo.kasper.core.interceptor.QueryInterceptorFactory;
import com.viadeo.kasper.core.resolvers.DomainHelper;
import com.viadeo.kasper.cqrs.command.CommandHandler;
import com.viadeo.kasper.cqrs.command.RepositoryManager;
import com.viadeo.kasper.cqrs.command.impl.DefaultRepositoryManager;
import com.viadeo.kasper.cqrs.command.impl.KasperCommandGateway;
import com.viadeo.kasper.cqrs.query.QueryHandler;
import com.viadeo.kasper.cqrs.query.impl.KasperQueryGateway;
import com.viadeo.kasper.ddd.repository.Repository;
import com.viadeo.kasper.event.CommandEventListener;
import com.viadeo.kasper.event.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.LifecycleProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.DefaultLifecycleProcessor;

import java.util.List;

@Configuration
public class KasperPlatformConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperPlatformConfiguration.class);

    @Autowired
    private Config config;

    @Bean
    public Build.Info info(ObjectMapper objectMapper) {
        return Build.info(objectMapper);
    }

    /**
     * Register lifecycle processor (using spring smart lifecycle)
     * In order to get a proper start/shutdown sequence
     *
     * @return lifecycle processor
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public LifecycleProcessor lifecycleProcessor() {
        return new DefaultLifecycleProcessor();
    }

    @Bean
    public Version contextVersion(final Config config) {

        final int currentApplicationVersion;
        if (config.hasPath("runtime.context.application.version")) {
            currentApplicationVersion = config.getInt("runtime.context.application.version");
        } else {
            currentApplicationVersion = 0;
            LOGGER.warn("runtime.context.application.version not set in configuration");
        }

        final int currentClientVersion;
        if (config.hasPath("runtime.context.client.version")) {
            currentClientVersion = config.getInt("runtime.context.client.version");
        } else {
            currentClientVersion = 0;
            LOGGER.warn("runtime.context.client.version not set in configuration");
        }

        return new ContextVersion(currentApplicationVersion, currentClientVersion);
    }

    @Bean
    public ContextHelper contextHelper(final Version version, final IDBuilder idBuilder) {
        return new DefaultContextHelper(version, idBuilder);
    }

    /**
     * Provide the domain descriptors instances
     * Domain descriptors are responsible both for wiring common object (TODO should be remove for proper DI)
     * And maintaining an explicit list of components exposed by the domains
     * <p/>
     * TODO Remove the builder context, redundant with DI
     *
     * @param metricRegistry the metric registry
     * @param evenBus        event bus
     * @param commandGateway command gateway
     * @param queryGateway   query gateway
     * @return domain descriptors instance
     */
    @Bean
    public Platform.BuilderContext platformBuilderContext(MetricRegistry metricRegistry,
                                                          KasperEventBus evenBus,
                                                          KasperCommandGateway commandGateway,
                                                          KasperQueryGateway queryGateway

    ) {

        ImmutableMap<Platform.ExtraComponentKey, Object> extraComponents = ImmutableMap.<Platform.ExtraComponentKey, Object>builder()
                .build();

        return new Platform.BuilderContext(config, evenBus, commandGateway, queryGateway, metricRegistry, extraComponents);
    }

    /*KasperDescriptorRegistry*
     * Initialize domain descriptors, with an additional nice side effect on domain helper.
     * This is the main glue code between the framework mess and the platform.
     * **Touch with caution**.
     *
     * @param domainHelper something strange
     * @param bundles the domains (with isolated command / query contexts depending on this context)
     * @param evenBus the event bus (rabbitmq in all environment except local and test)
     * @param commandGateway command gateway from kasper
     * @param queryGateway query gateway from kasper
     * @param context kasper context (some kind of DI framework)
     * @return Descriptor registry
     */
    @Bean
    public DescriptorRegistry descriptorRegistry(
            DomainHelper domainHelper,
            List<DomainBundle> bundles,
            KasperEventBus evenBus,
            KasperCommandGateway commandGateway,
            KasperQueryGateway queryGateway,
            Platform.BuilderContext context
    ) {

        List<DomainDescriptor> descriptors = Lists.newArrayList();
        DomainDescriptorFactory domainDescriptorFactory = new DomainDescriptorFactory();

        RepositoryManager repositoryManager = new DefaultRepositoryManager();
        for (final DomainBundle bundle : bundles) {

            LOGGER.debug("Configuring bundle : {}", bundle.getName());

            bundle.configure(context);

            for (final Repository repository : bundle.getRepositories()) {
                repository.init();
                repository.setEventBus(evenBus);
                repositoryManager.register(repository);
            }

            for (final CommandHandler commandHandler : bundle.getCommandHandlers()) {
                commandHandler.setEventBus(evenBus);
                commandHandler.setRepositoryManager(repositoryManager);
                commandGateway.register(commandHandler);
            }

            for (final QueryHandler queryHandler : bundle.getQueryHandlers()) {
                queryHandler.setEventBus(evenBus);
                queryGateway.register(queryHandler);
            }

            for (final EventListener eventListener : bundle.getEventListeners()) {
                eventListener.setEventBus(evenBus);

                if (CommandEventListener.class.isAssignableFrom(eventListener.getClass())) {
                    final CommandEventListener commandEventListener = (CommandEventListener) eventListener;
                    commandEventListener.setCommandGateway(commandGateway);
                }
                evenBus.subscribe(eventListener);
            }

            final DomainDescriptor domainDescriptor = domainDescriptorFactory.createFrom(bundle);
            domainHelper.add(DomainDescriptorFactory.mapToDomainClassByComponentClass(domainDescriptor));
            descriptors.add(domainDescriptor);
        }

        for (DomainBundle bundle : bundles) {
            for (CommandInterceptorFactory factory : bundle.getCommandInterceptorFactories()) {
                commandGateway.register(factory);
            }
            for (QueryInterceptorFactory factory : bundle.getQueryInterceptorFactories()) {
                queryGateway.register(factory);
            }
        }

        return new DescriptorRegistry(descriptors);
    }

}
