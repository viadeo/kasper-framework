// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import javax.ws.rs.core.Application;

public class Server implements SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(Server.class);

    private final org.eclipse.jetty.server.Server server;

    // ------------------------------------------------------------------------

    public Server(
            final ServerConfiguration config,
            final HttpQueryExposer queryExposer,
            final HttpCommandExposer commandExposer,
            final HttpEventExposer eventExposer,
            final Application application,
            final HealthCheckRegistry healthCheckRegistry,
            final MetricRegistry metricRegistry
    ) {
        this.server = new ServerBuilder(config)
                .withQueryExposer(queryExposer)
                .withCommandExposer(commandExposer)
                .withEventExposer(eventExposer)
                .withJaxRs(application)
                .withHealthCheckRegistry(healthCheckRegistry)
                .withMetricRegistry(metricRegistry)
                .build();
    }

    // ------------------------------------------------------------------------

    public void start() {
        try {
            this.server.start();
            LOGGER.info("Http server started");
        } catch (Exception e) {
            LOGGER.error("Fail to start the Jetty server... please check the availability of the port.");
            throw Throwables.propagate(e);
        }
    }

    public void stop() {
        Preconditions.checkState(server.isStarted(), "Server was not started, please call start before stop");
        try {
            this.server.stop();
        } catch (final Exception e) {
            LOGGER.error("An error occured while stopping the Jetty server", e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean isRunning() {
        return this.server.isRunning();
    }

    public int getPort() {
        return ServerBuilder.getPort(this.server);
    }

    public int getAdminPort() {
        return ServerBuilder.getAdminPort(this.server);
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    public void start(final Runnable callback) {
        start();
        callback.run();
    }

    @Override
    public void stop(final Runnable callback) {
        stop();
        callback.run();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }

}
