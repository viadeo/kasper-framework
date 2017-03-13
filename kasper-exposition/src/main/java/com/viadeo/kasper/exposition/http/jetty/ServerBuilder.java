// ----------------------------------------------------------------------------
//  This file is part of the Kasper framework.
//
//  The Kasper framework is free software: you can redistribute it and/or 
//  modify it under the terms of the GNU Lesser General Public License as 
//  published by the Free Software Foundation, either version 3 of the 
//  License, or (at your option) any later version.
//
//  Kasper framework is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
//
//  You should have received a copy of the GNU Lesser General Public License
//  along with the framework Kasper.  
//  If not, see <http://www.gnu.org/licenses/>.
// --
//  Ce fichier fait partie du framework logiciel Kasper
//
//  Ce programme est un logiciel libre ; vous pouvez le redistribuer ou le 
//  modifier suivant les termes de la GNU Lesser General Public License telle 
//  que publiée par la Free Software Foundation ; soit la version 3 de la 
//  licence, soit (à votre gré) toute version ultérieure.
//
//  Ce programme est distribué dans l'espoir qu'il sera utile, mais SANS 
//  AUCUNE GARANTIE ; sans même la garantie tacite de QUALITÉ MARCHANDE ou 
//  d'ADÉQUATION à UN BUT PARTICULIER. Consultez la GNU Lesser General Public 
//  License pour plus de détails.
//
//  Vous devez avoir reçu une copie de la GNU Lesser General Public License en 
//  même temps que ce programme ; si ce n'est pas le cas, consultez 
//  <http://www.gnu.org/licenses>
// ----------------------------------------------------------------------------
// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.kasper.com - mobile.kasper.com - api.kasper.com - dev.kasper.com
//
//           Kasper Framework for effective CQRS/DDD architecture
// ============================================================================
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
import com.google.common.base.MoreObjects;
import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.io.Resources;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
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

import static com.google.common.base.Preconditions.checkNotNull;

public class ServerBuilder {
    public static final Logger LOGGER = LoggerFactory.getLogger(ServerBuilder.class);

    public static final Connector[] EMPTY_CONNECTORS = new Connector[0];

    public static final String MAIN_CONNECTOR_NAME = "main";
    public static final String ADMIN_CONNECTOR_NAME = "admin";

    private final ServerConfiguration config;
    private HttpQueryExposer queryExposer;
    private HttpCommandExposer commandExposer;
    private HttpEventExposer eventExposer;
    private Application application;
    private HealthCheckRegistry healthCheckRegistry;
    private MetricRegistry metricRegistry;

    // ------------------------------------------------------------------------

    public ServerBuilder(final ServerConfiguration config) {
        this.config = checkNotNull(config);
    }

    public ServerBuilder withQueryExposer(final HttpQueryExposer exposer) {
        this.queryExposer = checkNotNull(exposer);
        return this;
    }

    public ServerBuilder withCommandExposer(final HttpCommandExposer exposer) {
        this.commandExposer = checkNotNull(exposer);
        return this;
    }

    public ServerBuilder withEventExposer(final HttpEventExposer exposer) {
        this.eventExposer = checkNotNull(exposer);
        return this;
    }

    public ServerBuilder withJaxRs(final Application application) {
        this.application = checkNotNull(application);
        return this;
    }

    public ServerBuilder withHealthCheckRegistry(final HealthCheckRegistry registry) {
        this.healthCheckRegistry = checkNotNull(registry);
        return this;
    }

    public ServerBuilder withMetricRegistry(final MetricRegistry registry) {
        this.metricRegistry = checkNotNull(registry);
        return this;
    }

    // ------------------------------------------------------------------------

    public Server build() {
        final Server server = new Server();

        server.addConnector(createMainConnector());
        server.addConnector(createAdminConnector());

        final HandlerCollection handlerCollection = new HandlerCollection();

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

    // ------------------------------------------------------------------------

    private Handler createStaticDocHandler(final String path) {
        /* FIXME
        The static documentation should not be hosted by the main application. It would be better to have it on an other
        dedicated server.
         */
        final ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setResourceBase(Resources.getResource("META-INF/resources/doc").toExternalForm());
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        final ContextHandler handler = new ContextHandler(path);
        handler.setHandler(resourceHandler);
        handler.setConnectorNames(new String[]{MAIN_CONNECTOR_NAME});

        return handler;
    }

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

        servletHandler.setConnectorNames(new String[]{MAIN_CONNECTOR_NAME});

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
            MoreObjects.firstNonNull(healthCheckRegistry, new HealthCheckRegistry())
        );
        handler.setAttribute(
            MetricsServlet.METRICS_REGISTRY,
            MoreObjects.firstNonNull(metricRegistry, new MetricRegistry())
        );

        handler.addServlet(new ServletHolder(new AdminServlet()), "/*");
        handler.addServlet(new ServletHolder(new JettyPingServlet()), "/jetty/*");
        handler.addServlet(new ServletHolder(new SlowServlet()), "/slow/*");
        handler.addServlet(new ServletHolder(new HystrixMetricsStreamServlet()), "/resilience/*");
        handler.setConnectorNames(new String[]{ADMIN_CONNECTOR_NAME});

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
        pool.setMaxQueued(config.getMaxQueued());
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
        Preconditions.checkState(server.isStarted(), "Server must be started to return its main port.");

        final Optional<Connector> connector = getConnectorByName(MAIN_CONNECTOR_NAME, server);
        if (connector.isPresent()) {
            return connector.get().getLocalPort();
        } else {
            throw new IllegalStateException("Impossible to find the main port. The server does not contain any Connector with the appropriate name '" + MAIN_CONNECTOR_NAME + "'");
        }
    }

    public static int getAdminPort(final Server server) {
        checkNotNull(server);
        Preconditions.checkState(server.isStarted(), "Server must be started to return its admin port.");

        final Optional<Connector> connector = getConnectorByName(ADMIN_CONNECTOR_NAME, server);
        if (connector.isPresent()) {
            return connector.get().getLocalPort();
        } else {
            throw new IllegalStateException("Impossible to find the admin port. The server does not contain any Connector with the appropriate name '" + ADMIN_CONNECTOR_NAME + "'");
        }
    }

    public static Optional<Connector> getConnectorByName(final String name, final Server server) {
        checkNotNull(name);

        final Connector[] connectors = MoreObjects.firstNonNull(server.getConnectors(), EMPTY_CONNECTORS);

        Optional<Connector> result = Optional.absent();
        for (final Connector connector : connectors) {
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

    public static class SlowServlet extends HttpServlet {
        private static final String CONTENT_TYPE = "text/plain";
        private static final String CONTENT = "OK!";

        @Override
        protected void doGet(final HttpServletRequest req,
                             final HttpServletResponse resp) throws ServletException, IOException {
            int durationInMs = Integer.valueOf(req.getParameter("sleepMs"));

            try {
                Thread.sleep(durationInMs);
            } catch (final InterruptedException e) {
                // whoops
                throw new RuntimeException(e);
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setHeader("Cache-Control", "must-revalidate,no-cache,no-store");
            resp.setContentType(CONTENT_TYPE);
            try (final PrintWriter writer = resp.getWriter()) {
                writer.println(CONTENT);
            }
        }
    }

}
