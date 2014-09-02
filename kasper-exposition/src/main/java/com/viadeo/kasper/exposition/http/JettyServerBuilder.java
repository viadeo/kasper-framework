// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http;

import com.codahale.metrics.Clock;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.codahale.metrics.jetty8.InstrumentedBlockingChannelConnector;
import com.codahale.metrics.jetty8.InstrumentedHandler;
import com.codahale.metrics.jetty8.InstrumentedQueuedThreadPool;
import com.codahale.metrics.servlets.AdminServlet;
import com.codahale.metrics.servlets.HealthCheckServlet;
import com.codahale.metrics.servlets.MetricsServlet;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.BlockingChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

public class JettyServerBuilder {
    public static final Connector[] EMPTY_CONNECTORS = new Connector[0];

    public static final String MAIN_CONNECTOR_NAME = "main";
    public static final String ADMIN_CONNECTOR_NAME = "admin";

    private final JettyConfiguration config;
    private final List<ContextHandler> staticContextHandlers;
    private HttpQueryExposer queryExposer;
    private HttpCommandExposer commandExposer;
    private HttpEventExposer eventExposer;
    private Application application;
    private HealthCheckRegistry healthCheckRegistry;
    private MetricRegistry metricRegistry;

    // ------------------------------------------------------------------------

    /**
     * An HTTP servlet which outputs a {@code text/plain} {@code "Jetty is in da place!"} response.
     */
    public static class JettyPingServlet extends HttpServlet {
        private static final String CONTENT_TYPE = "text/plain";
        private static final String CONTENT = "Jetty is in da place!";

        @Override
        protected void doGet(final HttpServletRequest req,
                             final HttpServletResponse resp) throws ServletException, IOException {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
            resp.setContentType(CONTENT_TYPE);
            try (final PrintWriter writer = resp.getWriter()) {
                writer.println(CONTENT);
            }
        }
    }

    // ------------------------------------------------------------------------

    public JettyServerBuilder(final JettyConfiguration config) {
        this.config = config;
        this.staticContextHandlers = Lists.newArrayList();
    }

    public JettyServerBuilder withQueryExposer(final HttpQueryExposer exposer) {
        this.queryExposer = exposer;
        return this;
    }

    public JettyServerBuilder withCommandExposer(final HttpCommandExposer exposer) {
        this.commandExposer = exposer;
        return this;
    }

    public JettyServerBuilder withEventExposer(final HttpEventExposer exposer) {
        this.eventExposer = exposer;
        return this;
    }

    public JettyServerBuilder withJaxRs(final Application application) {
        this.application = application;
        return this;
    }

    public JettyServerBuilder withHealthCheckRegistry(final HealthCheckRegistry registry) {
        checkNotNull(registry);
        this.healthCheckRegistry = registry;
        return this;
    }

    public JettyServerBuilder withMetricRegistry(final MetricRegistry registry) {
        checkNotNull(registry);
        this.metricRegistry = registry;
        return this;
    }

    public JettyServerBuilder addStaticResource(final String path, final ResourceHandler resourceHandler) {
        checkNotNull(path);
        checkNotNull(resourceHandler);

        final ContextHandler handler = new ContextHandler(path);
        handler.setHandler(resourceHandler);
        handler.setConnectorNames(new String[]{MAIN_CONNECTOR_NAME});

        this.staticContextHandlers.add(handler);

        return this;
    }

    // ------------------------------------------------------------------------

    public Server build() {
        final Server server = new Server();

        server.addConnector(createMainConnector());
        server.addConnector(createAdminConnector());

        final HandlerCollection handlerCollection = new HandlerCollection();

        // FIXME main application should not expose static content
        for (final ContextHandler contextHandler : staticContextHandlers) {
            handlerCollection.addHandler(contextHandler);
        }

        handlerCollection.addHandler(createMainHandler());
        handlerCollection.addHandler(createAdminHandler());

        server.setHandler(handlerCollection);

        server.setThreadPool(createServerThreadPool());
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(config.getShutdownGracePeriod());

        if (config.isJmxEnabled()) {
            final MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
            server.getContainer().addEventListener(mbContainer);
            server.addBean(mbContainer);

            mbContainer.addBean(Log.getLogger(Slf4jLog.class));
        }

        return server;
    }

    // ------------------------------------------------------------------------

    private Handler createMainHandler() {
        final ServletContextHandler servletHandler = new ServletContextHandler();

        if (null != this.queryExposer) {
            servletHandler.addServlet(new ServletHolder(queryExposer), config.getQueryPath());
        }
        if (null != this.commandExposer) {
            servletHandler.addServlet(new ServletHolder(commandExposer), config.getCommandPath());
        }
        if (null != this.eventExposer) {
            servletHandler.addServlet(new ServletHolder(eventExposer), config.getEventPath());
        }
        if (null != this.application) {
            servletHandler.addServlet(new ServletHolder(new ServletContainer(application)), "/*"); // FIXME root path should be configurable
        }

        servletHandler.setConnectorNames(new String[] { MAIN_CONNECTOR_NAME });

        Handler handler = servletHandler;
        if (null != this.metricRegistry) {
            handler = new InstrumentedHandler(this.metricRegistry, handler);
        }

        return handler;
    }

    private Handler createAdminHandler() {
        final ServletContextHandler handler = new ServletContextHandler();

        handler.setAttribute(
            HealthCheckServlet.HEALTH_CHECK_REGISTRY,
            Objects.firstNonNull(healthCheckRegistry, new HealthCheckRegistry())
        );
        handler.setAttribute(
            MetricsServlet.METRICS_REGISTRY,
            Objects.firstNonNull(metricRegistry, new MetricRegistry())
        );

        handler.addServlet(new ServletHolder(new AdminServlet()), "/*");
        handler.addServlet(new ServletHolder(new JettyPingServlet()), "/jetty/*");
        handler.setConnectorNames(new String[] { ADMIN_CONNECTOR_NAME });

        return handler;
    }

    @VisibleForTesting
    protected ThreadPool createServerThreadPool() {
        final QueuedThreadPool pool;
        if (null == this.metricRegistry) {
            pool = new QueuedThreadPool();
        } else {
            pool = new InstrumentedQueuedThreadPool(this.metricRegistry);
        }

        pool.setMinThreads(config.getPoolMinThreads());
        pool.setMaxThreads(config.getPoolMaxThreads());

        return pool;
    }

    @VisibleForTesting
    protected AbstractConnector createMainConnector() {

        final BlockingChannelConnector connector;
        if (null == this.metricRegistry) {
            connector = new BlockingChannelConnector();
            connector.setPort(config.getPort());
        } else {
            connector = new InstrumentedBlockingChannelConnector(
                metricRegistry,
                config.getPort(),
                Clock.defaultClock()
            );
        }

        connector.setName(MAIN_CONNECTOR_NAME);
        connector.setHost(config.getHost());
        connector.setAcceptors(config.getAcceptors());
        connector.setForwarded(config.isForwarded());
        connector.setMaxIdleTime(config.getMaxIdleTime());
        connector.setLowResourcesMaxIdleTime(config.getLowResourcesMaxIdleTime());
        connector.setAcceptorPriorityOffset(config.getAcceptorPriorityOffset());
        connector.setAcceptQueueSize(config.getAcceptQueueSize());
        connector.setMaxBuffers(config.getMaxBuffers());
        connector.setRequestBufferSize(config.getRequestBufferSize());
        connector.setRequestHeaderSize(config.getRequestHeaderSize());
        connector.setResponseBufferSize(config.getResponseBufferSize());
        connector.setResponseHeaderSize(config.getResponseHeaderSize());

        return connector;
    }

    private Connector createAdminConnector() {
        final SocketConnector connector = new SocketConnector();

        connector.setHost(config.getHost());
        connector.setPort(config.getAdminPort());
        connector.setName(ADMIN_CONNECTOR_NAME);
        connector.setThreadPool(new QueuedThreadPool(8));

        return connector;
    }

    public static int getPort(final Server server) {
        checkNotNull(server);
        checkState(server.isStarted(), "Server must be started to return its main port.");

        final Optional<Connector> connector = getConnectorByName(MAIN_CONNECTOR_NAME, server);
        if (connector.isPresent()) {
            return connector.get().getLocalPort();
        } else {
            throw new IllegalStateException(String.format(
                "Impossible to find the main port. The server does not contain any Connector with the appropriate name '%s'",
                MAIN_CONNECTOR_NAME
            ));
        }
    }

    public static int getAdminPort(final Server server) {
        checkNotNull(server);
        checkState(server.isStarted(), "Server must be started to return its admin port.");

        final Optional<Connector> connector = getConnectorByName(ADMIN_CONNECTOR_NAME, server);
        if (connector.isPresent()) {
            return connector.get().getLocalPort();
        } else {
            throw new IllegalStateException(String.format(
                "Impossible to find the admin port. The server does not contain any Connector with the appropriate name '%s'",
                ADMIN_CONNECTOR_NAME
            ));
        }
    }

    public static Optional<Connector> getConnectorByName(final String name, final Server server) {
        checkNotNull(name);

        final Connector[] connectors = Objects.firstNonNull(server.getConnectors(), EMPTY_CONNECTORS);

        Optional<Connector> result = Optional.absent();
        for (final Connector connector : connectors) {
            if (name.equals(connector.getName())) {
                if (result.isPresent()) {
                    throw new IllegalStateException(String.format(
                            "Multiple connectors have the same name '%s'",
                            name
                    ));
                }
                result = Optional.of(connector);
            }
        }

        return result;
    }

}
