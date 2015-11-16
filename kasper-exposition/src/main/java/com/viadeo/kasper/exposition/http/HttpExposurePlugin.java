// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.sun.jersey.api.core.ResourceConfig;
import com.viadeo.kasper.api.component.command.Command;
import com.viadeo.kasper.api.component.event.Event;
import com.viadeo.kasper.api.component.query.Query;
import com.viadeo.kasper.common.exposition.query.QueryFactoryBuilder;
import com.viadeo.kasper.common.serde.ObjectMapperProvider;
import com.viadeo.kasper.core.component.command.CommandHandler;
import com.viadeo.kasper.core.component.event.listener.EventListener;
import com.viadeo.kasper.core.component.query.QueryHandler;
import com.viadeo.kasper.doc.element.DocumentedPlatform;
import com.viadeo.kasper.exposition.ExposureDescriptor;
import com.viadeo.kasper.exposition.http.jetty.Server;
import com.viadeo.kasper.exposition.http.jetty.ServerConfiguration;
import com.viadeo.kasper.exposition.http.jetty.resource.DefaultRootResource;
import com.viadeo.kasper.exposition.http.jetty.resource.KasperDocResource;
import com.viadeo.kasper.exposition.http.jetty.resource.Resource;
import com.viadeo.kasper.platform.Platform;
import com.viadeo.kasper.platform.builder.PlatformContext;
import com.viadeo.kasper.platform.bundle.descriptor.CommandHandlerDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.DomainDescriptor;
import com.viadeo.kasper.platform.bundle.descriptor.QueryHandlerDescriptor;
import com.viadeo.kasper.platform.plugin.Plugin;
import com.viadeo.kasper.platform.plugin.PluginAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class HttpExposurePlugin extends PluginAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpExposurePlugin.class);

    private final HealthCheckRegistry healthCheckRegistry;
    private final HttpContextDeserializer contextDeserializer;
    private final DefaultResourceConfig resourceConfig;

    private Server server;

    private HttpCommandExposer commandExposer;
    private HttpQueryExposer queryExposer;
    private HttpEventExposer eventExposer;

    // ------------------------------------------------------------------------

    public HttpExposurePlugin() {
        this(new SimpleHttpContextDeserializer());
    }

    public HttpExposurePlugin(final HttpContextDeserializer contextDeserializer) {
        this.contextDeserializer = checkNotNull(contextDeserializer);
        this.healthCheckRegistry = new HealthCheckRegistry();
        this.healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck());
        this.resourceConfig = new DefaultResourceConfig();
        this.resourceConfig.getFeatures().put(ResourceConfig.FEATURE_DISABLE_WADL, true);
    }

    // ------------------------------------------------------------------------

    @Override
    public void initialize(final PlatformContext context) {
        checkNotNull(context);

        queryExposer = new HttpQueryExposer(
                context.getQueryGateway(),
                context.getMeta(),
                Lists.<ExposureDescriptor<Query, QueryHandler>>newArrayList(),
                new QueryFactoryBuilder().create(),
                contextDeserializer,
                ObjectMapperProvider.INSTANCE.mapper()
        );

        commandExposer = new HttpCommandExposer(
                context.getCommandGateway(),
                context.getMeta(),
                Lists.<ExposureDescriptor<Command, CommandHandler>>newArrayList(),
                contextDeserializer,
                ObjectMapperProvider.INSTANCE.mapper()
        );

        eventExposer = new HttpEventExposer(
                context.getEventBus(),
                context.getMeta(),
                Lists.<ExposureDescriptor<Event, EventListener>>newArrayList(),
                contextDeserializer,
                ObjectMapperProvider.INSTANCE.mapper()
        );

        initServer(context);
    }

    protected void initServer(final PlatformContext context) {
        checkNotNull(context);

        server = new Server(
                new ServerConfiguration(context.getConfiguration().getConfig("runtime.http"))
                , queryExposer
                , commandExposer
                , eventExposer
                , resourceConfig
                , healthCheckRegistry
                , context.getMetricRegistry()
        );
    }

    @Override
    public void onPlatformStarted(final Platform platform) {
        LOGGER.info("Exposing {} command handlers", commandExposer.getExposedInputs().size());
        LOGGER.info("Exposing {} query handlers", queryExposer.getExposedInputs().size());
        LOGGER.info("Exposing {} event listeners", eventExposer.getExposedInputs().size());

        if (resourceConfig.getSingletons().isEmpty()) {
            resourceConfig.getSingletons().add(new DefaultRootResource());
        }

        if (server != null) {
            server.start();
        }
    }

    @Override
    public void onPlatformStopped(final Platform platform) {
        if (server != null) {
            server.stop();
        }
    }

    @Override
    public void onDomainRegistered(final DomainDescriptor domainDescriptor) {
        checkNotNull(domainDescriptor);

        for (final Class<? extends Event> eventClass : domainDescriptor.getEventClasses()) {
            eventExposer.register(new ExposureDescriptor<>(eventClass, EventListener.class));
        }

        for (final CommandHandlerDescriptor descriptor :  domainDescriptor.getCommandHandlerDescriptors()) {
            commandExposer.register(new ExposureDescriptor<>(descriptor.getCommandClass(), descriptor.getReferenceClass()));
        }

        for (final QueryHandlerDescriptor descriptor : domainDescriptor.getQueryHandlerDescriptors()) {
            queryExposer.register(new ExposureDescriptor<>(descriptor.getQueryClass(), descriptor.getReferenceClass()));
        }
    }

    @Override
    public void onPluginRegistered(final Plugin plugin) {
        checkNotNull(plugin);

        for (final DocumentedPlatform documentedPlatform : plugin.get(DocumentedPlatform.class)) {
            resourceConfig.getSingletons().add(new KasperDocResource(documentedPlatform));
        }

        for (final Resource resource : plugin.get(Resource.class)) {
            resourceConfig.getSingletons().add(resource);
        }

        for (final HealthCheck healthCheck : plugin.get(HealthCheck.class)) {
            healthCheckRegistry.register(
                    healthCheck.getClass().getSimpleName(),
                    healthCheck
            );
        }
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

    public Server getServer() {
        return server;
    }

    @VisibleForTesting
    protected HttpCommandExposer getCommandExposer() {
        return commandExposer;
    }

    @VisibleForTesting
    protected HttpQueryExposer getQueryExposer() {
        return queryExposer;
    }

    @VisibleForTesting
    protected HttpEventExposer getEventExposer() {
        return eventExposer;
    }

}
