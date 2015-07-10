package com.viadeo.kasper.exposition.http.jetty;

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
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.sun.jersey.spi.container.servlet.ServletContainer;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.handler.RequestLogHandler;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.nio.BlockingChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.management.ManagementFactory;


public class JettyServerBuilder {
    public static final Logger LOGGER = LoggerFactory.getLogger(JettyServerBuilder.class);

    public static final Connector[] EMPTY_CONNECTORS = new Connector[0];

    public static final String MAIN_CONNECTOR_NAME = "main";
    public static final String ADMIN_CONNECTOR_NAME = "admin";

    private final JettyConfiguration config;
    private HttpQueryExposer queryExposer;
    private HttpCommandExposer commandExposer;
    private HttpEventExposer eventExposer;
    private Application application;
    private HealthCheckRegistry healthCheckRegistry;
    private MetricRegistry metricRegistry;

    public JettyServerBuilder(JettyConfiguration config) {
        this.config = config;
    }

    public JettyServerBuilder withQueryExposer(HttpQueryExposer exposer) {
        this.queryExposer = exposer;
        return this;
    }

    public JettyServerBuilder withCommandExposer(HttpCommandExposer exposer) {
        this.commandExposer = exposer;
        return this;
    }

    public JettyServerBuilder withEventExposer(HttpEventExposer exposer) {
        this.eventExposer = exposer;
        return this;
    }

    public JettyServerBuilder withJaxRs(Application application) {
        this.application = application;
        return this;
    }

    public JettyServerBuilder withHealthCheckRegistry(HealthCheckRegistry registry) {
        Preconditions.checkNotNull(registry);
        this.healthCheckRegistry = registry;
        return this;
    }

    public JettyServerBuilder withMetricRegistry(MetricRegistry registry) {
        Preconditions.checkNotNull(registry);
        this.metricRegistry = registry;
        return this;
    }

    public Server build() {
        Server server = new Server();

        server.addConnector(createMainConnector());
        server.addConnector(createAdminConnector());

        HandlerCollection handlerCollection = new HandlerCollection();

        /*
         * /!\ ONLY FOR TEST PURPOSE: WILL BE REMOVED SOON
         */

        String viadeoEnv = System.getenv("viadeo_env");

        if (viadeoEnv != null && (viadeoEnv.equals("preprod") || viadeoEnv.equals("local"))) {
            LOGGER.warn("/!\\ ViadeoRequestLog is enabled, be sure you are not in production!");
            RequestLogHandler logHandler = new RequestLogHandler();

            JettyRequestLog requestLog = new JettyRequestLog();
            requestLog.setFilename("/tmp/jetty_viadeo_platform");
            requestLog.setLogLatency(true);
            requestLog.setExtended(true);
            requestLog.setAppend(false);

            logHandler.setRequestLog(requestLog);
            handlerCollection.addHandler(logHandler);

            Handler myLogHandler = new JettyRequestLogHandler();
            handlerCollection.addHandler(myLogHandler);
        }

        handlerCollection.addHandler(createStaticDocHandler("/doc")); // FIXME main application should not expose static content
        handlerCollection.addHandler(createStaticDocHandler("/")); // FIXME main application should not expose static content
        handlerCollection.addHandler(createMainHandler());
        handlerCollection.addHandler(createAdminHandler());

        server.setHandler(handlerCollection);

        server.setThreadPool(createServerThreadPool());
        server.setStopAtShutdown(true);
        server.setGracefulShutdown(config.getShutdownGracePeriod());

        if (config.isJmxEnabled()) {
            MBeanContainer mbContainer = new MBeanContainer(ManagementFactory.getPlatformMBeanServer());
            server.getContainer().addEventListener(mbContainer);
            server.addBean(mbContainer);

            mbContainer.addBean(Log.getLogger(Slf4jLog.class));
        }

        return server;
    }

    private Handler createStaticDocHandler(String path) {
        /* FIXME
        The static documentation should not be hosted by the main application. It would be better to have it on an other
        dedicated server.
         */
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(Resources.getResource("META-INF/resources/ndoc").toExternalForm());
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        ContextHandler handler = new ContextHandler(path);
        handler.setHandler(resourceHandler);
        handler.setConnectorNames(new String[]{MAIN_CONNECTOR_NAME});

        return handler;
    }

    private Handler createMainHandler() {
        ServletContextHandler servletHandler = new ServletContextHandler();

        if (this.queryExposer != null) {
            servletHandler.addServlet(new ServletHolder(queryExposer), config.getQueryPath());
        }
        if (this.commandExposer != null) {
            servletHandler.addServlet(new ServletHolder(commandExposer), config.getCommandPath());
        }
        if (this.eventExposer != null) {
            servletHandler.addServlet(new ServletHolder(eventExposer), config.getEventPath());
        }
        if (this.application != null) {
            servletHandler.addServlet(new ServletHolder(new ServletContainer(application)), "/*"); // FIXME root path should be configurable
        }

        servletHandler.setConnectorNames(new String[]{MAIN_CONNECTOR_NAME});

        Handler handler = servletHandler;
        if (this.metricRegistry != null) {
            handler = new InstrumentedHandler(this.metricRegistry, handler);
        }

        return handler;
    }

    private Handler createAdminHandler() {
        ServletContextHandler handler = new ServletContextHandler();

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
        handler.addServlet(new ServletHolder(new SlowServlet()), "/slow/*");
        handler.setConnectorNames(new String[]{ADMIN_CONNECTOR_NAME});

        return handler;
    }

    @VisibleForTesting
    protected ThreadPool createServerThreadPool() {
        QueuedThreadPool pool;
        if (this.metricRegistry == null) {
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
        BlockingChannelConnector connector;
        if (this.metricRegistry == null) {
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
        SocketConnector connector = new SocketConnector();

        connector.setHost(config.getHost());
        connector.setPort(config.getAdminPort());
        connector.setName(ADMIN_CONNECTOR_NAME);
        connector.setThreadPool(new QueuedThreadPool(8));

        return connector;
    }

    public static int getPort(Server server) {
        Preconditions.checkNotNull(server);
        Preconditions.checkState(server.isStarted(), "Server must be started to return its main port.");

        Optional<Connector> connector = getConnectorByName(MAIN_CONNECTOR_NAME, server);
        if (connector.isPresent()) {
            return connector.get().getLocalPort();
        } else {
            throw new IllegalStateException("Impossible to find the main port. The server does not contain any Connector with the appropriate name '" + MAIN_CONNECTOR_NAME + "'");
        }
    }

    public static int getAdminPort(Server server) {
        Preconditions.checkNotNull(server);
        Preconditions.checkState(server.isStarted(), "Server must be started to return its admin port.");

        Optional<Connector> connector = getConnectorByName(ADMIN_CONNECTOR_NAME, server);
        if (connector.isPresent()) {
            return connector.get().getLocalPort();
        } else {
            throw new IllegalStateException("Impossible to find the admin port. The server does not contain any Connector with the appropriate name '" + ADMIN_CONNECTOR_NAME + "'");
        }
    }

    public static Optional<Connector> getConnectorByName(String name, Server server) {
        Preconditions.checkNotNull(name);
        Connector[] connectors = Objects.firstNonNull(server.getConnectors(), EMPTY_CONNECTORS);
        Optional<Connector> result = Optional.absent();
        for (Connector connector : connectors) {
            if (name.equals(connector.getName())) {
                if (result.isPresent()) {
                    throw new IllegalStateException("Multiple connectors have the same name '" + name + "'");
                }
                result = Optional.of(connector);
            }
        }
        return result;
    }

    /**
     * An HTTP servlet which outputs a {@code text/plain} {@code "Jetty is in da place!"} response.
     */
    public static class JettyPingServlet extends HttpServlet {
        private static final String CONTENT_TYPE = "text/plain";
        private static final String CONTENT = "Jetty is in da place!";

        @Override
        protected void doGet(HttpServletRequest req,
                             HttpServletResponse resp) throws ServletException, IOException {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
            resp.setContentType(CONTENT_TYPE);
            try (PrintWriter writer = resp.getWriter()) {
                writer.println(CONTENT);
            }
        }
    }

    public static class SlowServlet extends HttpServlet {
        private static final String CONTENT_TYPE = "text/plain";
        private static final String CONTENT = "OK!";

        @Override
        protected void doGet(HttpServletRequest req,
                             HttpServletResponse resp) throws ServletException, IOException {
            int durationInMs = Integer.valueOf(req.getParameter("sleepMs"));

            try {
                Thread.sleep(durationInMs);
            } catch (InterruptedException e) {
                // whoops
                throw new RuntimeException(e);
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
            resp.setContentType(CONTENT_TYPE);
            try (PrintWriter writer = resp.getWriter()) {
                writer.println(CONTENT);
            }
        }
    }

}
