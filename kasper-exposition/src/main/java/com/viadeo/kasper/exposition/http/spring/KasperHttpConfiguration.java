// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.spring;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.typesafe.config.Config;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.command.gateway.KasperCommandGateway;
import com.viadeo.kasper.core.component.event.eventbus.KasperEventBus;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.core.component.query.gateway.KasperQueryGateway;
import com.viadeo.kasper.core.metrics.KasperMetrics;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.doc.initializer.DefaultDocumentedElementInitializer;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpContextDeserializer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import com.viadeo.kasper.exposition.http.jetty.Server;
import com.viadeo.kasper.exposition.http.jetty.ServerConfiguration;
import com.viadeo.kasper.exposition.http.jetty.resource.KasperDocResource;
import com.viadeo.kasper.exposition.http.jetty.resource.Resource;
import com.viadeo.kasper.platform.Build;
import com.viadeo.kasper.platform.Meta;
import com.viadeo.kasper.platform.bundle.descriptor.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
public class KasperHttpConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(KasperHttpConfiguration.class);

    @Autowired
    private Config config;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired(required = false)
    Map<String, HealthCheck> healthChecks;

    /**
     * Setup the http server
     *
     * @param queryExposer        query exposer
     * @param eventExposer        event exposer
     * @param commandExposer      command exposer
     * @param resourceConfig      resource config
     * @param healthCheckRegistry registry
     * @return the http server
     */
    @Bean
    public Server httpServer(
            final HttpQueryExposer queryExposer,
            final HttpEventExposer eventExposer,
            final HttpCommandExposer commandExposer,
            final ResourceConfig resourceConfig,
            final HealthCheckRegistry healthCheckRegistry) {
        return new Server(
                new ServerConfiguration(config.getConfig("runtime.http"))
                , queryExposer
                , commandExposer
                , eventExposer
                , resourceConfig
                , healthCheckRegistry
                , KasperMetrics.getMetricRegistry()
        );
    }

    @Bean
    public HttpContextDeserializer httpContextDeserializer() {
        return new HttpContextDeserializer();
    }

    /**
     * Expose event to the wild
     *
     * @param httpContextDeserializer a context deserializer
     * @param descriptors domain descriptors
     * @param eventBus    the event bus (in which events are put after de-serialization)
     * @param meta        meta
     * @return http event exposer
     */
    @Bean
    public HttpEventExposer httpEventExposer(
            final HttpContextDeserializer httpContextDeserializer,
            final DescriptorRegistry descriptors,
            final KasperEventBus eventBus,
            final Meta meta) {

        final List<ExposureDescriptor<Event, EventListener>> exposureDescriptors = Lists.newArrayList();
        for (final DomainDescriptor domainDescriptor : descriptors) {
            for (final EventListenerDescriptor eventListenerDescriptor : domainDescriptor.getEventListenerDescriptors()) {
                exposureDescriptors.add(new ExposureDescriptor<>(eventListenerDescriptor.getEventClass(), eventListenerDescriptor.getReferenceClass()));
            }
        }

        LOGGER.info("Exposing {} event listeners", exposureDescriptors.size());

        return new HttpEventExposer(eventBus, meta, exposureDescriptors, httpContextDeserializer, objectMapper);
    }

    /**
     * Expose command to the wild
     *
     * @param httpContextDeserializer a context deserializer
     * @param descriptors domain descriptors
     * @param commandGateway command gateway
     * @param meta           meta
     * @return http command exposer
     */
    @Bean
    public HttpCommandExposer httpCommandExposer(
            final HttpContextDeserializer httpContextDeserializer,
            final DescriptorRegistry descriptors,
            final KasperCommandGateway commandGateway,
            final Meta meta) {

        final List<ExposureDescriptor<Command, CommandHandler>> exposureDescriptors = Lists.newArrayList();

        for (final DomainDescriptor domainDescriptor : descriptors) {
            final Collection<CommandHandlerDescriptor> commandHandlerDescriptors = domainDescriptor.getCommandHandlerDescriptors();
            for (final CommandHandlerDescriptor descriptor : commandHandlerDescriptors) {
                exposureDescriptors.add(new ExposureDescriptor<>(descriptor.getCommandClass(), descriptor.getReferenceClass()));
            }
        }

        LOGGER.info("Exposing {} command handlers", exposureDescriptors.size());

        return new HttpCommandExposer(
            commandGateway,
            meta,
            exposureDescriptors,
            httpContextDeserializer,
            objectMapper
        );
    }

    /**
     * Expose query to the wild
     *
     * @param httpContextDeserializer a context deserializer
     * @param descriptors  domain descriptors
     * @param queryGateway query gateway
     * @param meta the meta information
     * @return http query exposer
     */
    @Bean
    public HttpQueryExposer httpQueryExposer(
            final HttpContextDeserializer httpContextDeserializer,
            final DescriptorRegistry descriptors,
            final KasperQueryGateway queryGateway,
            final Meta meta) {

        final List<ExposureDescriptor<Query, QueryHandler>> exposureDescriptors = Lists.newArrayList();
        for (final DomainDescriptor domainDescriptor : descriptors) {
            for (final QueryHandlerDescriptor descriptor : domainDescriptor.getQueryHandlerDescriptors()) {
                exposureDescriptors.add(new ExposureDescriptor<>(descriptor.getQueryClass(), descriptor.getReferenceClass()));
            }
        }

        LOGGER.info("Exposing {} query handlers", exposureDescriptors.size());

        return new HttpQueryExposer(queryGateway, meta, exposureDescriptors, new QueryFactoryBuilder().create(), httpContextDeserializer, objectMapper);
    }

    /**
     * The documented platform
     *
     * @param descriptors the domain descriptor provider
     * @return the documented platform
     */
    @Bean
    public DocumentedPlatform documentedPlatform(final DescriptorRegistry descriptors) {
        long start = System.currentTimeMillis();

        final DocumentedPlatform documentedPlatform = new DocumentedPlatform();
        for (final DomainDescriptor domainDescriptor : descriptors) {
            documentedPlatform.registerDomain(domainDescriptor.getName(), domainDescriptor);
        }

        documentedPlatform.accept(new DefaultDocumentedElementInitializer(documentedPlatform));

        LOGGER.info("Documentation loaded in {} ms", System.currentTimeMillis() - start);
        return documentedPlatform;
    }

    @Bean
    public Resource kasperDocResource(final DocumentedPlatform documentedPlatform) {
        return new KasperDocResource(documentedPlatform);
    }

    /**
     * Returns resources exposed by jetty.
     *
     * @param resources resources to expose
     * @return resource config
     */
    @Bean
    ResourceConfig resourceConfig(final List<Resource> resources) {
        // Aggregate all Jersey resources in a single Jax-RS application
        final DefaultResourceConfig resourceConfig = new DefaultResourceConfig();
        resourceConfig.getFeatures().put(ResourceConfig.FEATURE_DISABLE_WADL, true);

        for (final Object resource : resources) {
            resourceConfig.getSingletons().add(resource);
        }

        return resourceConfig;
    }

    @Bean
    HealthCheckRegistry healthCheckRegistry() {

        // Add health checks to a new registry
        final HealthCheckRegistry healthCheckRegistry = new HealthCheckRegistry();
        healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck());


        if (healthChecks == null || healthChecks.isEmpty()) {
            LOGGER.warn('\n' +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                            "!    THIS SERVICE HAS NO HEALTHCHECKS. THIS MEANS YOU WILL NEVER KNOW IF IT    !\n" +
                            "!    DIES IN PRODUCTION, WHICH MEANS YOU WILL NEVER KNOW IF YOU'RE LETTING     !\n" +
                            "!     YOUR USERS DOWN. YOU SHOULD ADD A HEALTHCHECK FOR EACH DEPENDENCY OF     !\n" +
                            "!     YOUR SERVICE WHICH FULLY (BUT LIGHTLY) TESTS YOUR SERVICE'S ABILITY TO   !\n" +
                            "!      USE THAT SERVICE. THINK OF IT AS A CONTINUOUS INTEGRATION TEST.         !\n" +
                            "!                                                               -- Coda Hale   !\n" +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n" +
                            "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!"
            );
        } else {
            for (final Map.Entry<String, HealthCheck> healthCheck : healthChecks.entrySet()) {
                healthCheckRegistry.register(healthCheck.getKey(), healthCheck.getValue());
            }
        }

        return healthCheckRegistry;
    }

    @Bean
    public Meta meta(final Build.Info info) {
        return new Meta(
                info.getRevision(),
                info.getTime(),
                DateTime.now()
        );
    }

}
