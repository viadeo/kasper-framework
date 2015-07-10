package com.viadeo.kasper.exposition.http.jetty;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.SmartLifecycle;

import javax.ws.rs.core.Application;

public class JettyHttpServer implements SmartLifecycle {
    private static final Logger LOGGER = LoggerFactory.getLogger(JettyHttpServer.class);

    private final Server server;

    public JettyHttpServer(
            JettyConfiguration config,
            HttpQueryExposer queryExposer,
            HttpCommandExposer commandExposer,
            HttpEventExposer eventExposer,
            Application application,
            HealthCheckRegistry healthCheckRegistry,
            MetricRegistry metricRegistry
    ) {
        this.server = new JettyServerBuilder(config)
                .withQueryExposer(queryExposer)
                .withCommandExposer(commandExposer)
                .withEventExposer(eventExposer)
                .withJaxRs(application)
                .withHealthCheckRegistry(healthCheckRegistry)
                .withMetricRegistry(metricRegistry)
                .build();
    }

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
        } catch (Exception e) {
            LOGGER.error("An error occured while stopping the Jetty server", e);
            throw Throwables.propagate(e);
        }
    }

    @Override
    public boolean isRunning() {
        return this.server.isRunning();
    }

    public int getPort() {
        return JettyServerBuilder.getPort(this.server);
    }

    public int getAdminPort() {
        return JettyServerBuilder.getAdminPort(this.server);
    }

    @Override
    public boolean isAutoStartup() {
        return false;
    }

    @Override
    public void stop(Runnable callback) {
        stop();
    }

    @Override
    public int getPhase() {
        return Integer.MAX_VALUE;
    }
}
