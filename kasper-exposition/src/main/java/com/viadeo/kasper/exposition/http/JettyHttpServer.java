// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Application;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class JettyHttpServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyHttpServer.class);

    private final Server server;

    // ------------------------------------------------------------------------

    public JettyHttpServer(
            final JettyConfiguration config,
            final HttpQueryExposer queryExposer,
            final HttpCommandExposer commandExposer,
            final HttpEventExposer eventExposer,
            final Application application,
            final HealthCheckRegistry healthCheckRegistry,
            final MetricRegistry metricRegistry) {
        this(
                config,
                queryExposer, commandExposer, eventExposer,
                application,
                healthCheckRegistry, metricRegistry,
                Maps.<String,ResourceHandler>newHashMap()
        );
    }

    public JettyHttpServer(
            final JettyConfiguration config,
            final HttpQueryExposer queryExposer,
            final HttpCommandExposer commandExposer,
            final HttpEventExposer eventExposer,
            final Application application,
            final HealthCheckRegistry healthCheckRegistry,
            final MetricRegistry metricRegistry,
            final Map<String, ResourceHandler> resourceByPaths) {

        final JettyServerBuilder builder = new JettyServerBuilder(config)
                .withQueryExposer(queryExposer)
                .withCommandExposer(commandExposer)
                .withEventExposer(eventExposer)
                .withJaxRs(application)
                .withHealthCheckRegistry(healthCheckRegistry)
                .withMetricRegistry(metricRegistry);

        for (final Map.Entry<String, ResourceHandler> entry : resourceByPaths.entrySet()) {
            builder.addStaticResource(
                    entry.getKey(),
                    entry.getValue()
            );
        }

        this.server = builder.build();
    }

    // ------------------------------------------------------------------------

    public void start() {
        try {
            this.server.start();
        } catch (final Exception e) {
            LOGGER.error("Fail to start the Jetty server... please check the availability of the port.");
            Throwables.propagate(e);
        }
    }

    public void stop() {
        checkState(server.isStarted(), "Server was not started, please call start before stop");
        try {
            this.server.stop();
        } catch (final Exception e) {
            LOGGER.error("An error occured while stopping the Jetty server", e);
            Throwables.propagate(e);
        }
    }

    // ------------------------------------------------------------------------

    public int getPort() {
        return JettyServerBuilder.getPort(this.server);
    }

    public int getAdminPort() {
        return JettyServerBuilder.getAdminPort(this.server);
    }

}

