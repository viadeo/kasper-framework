// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.jetty8.InstrumentedBlockingChannelConnector;
import com.codahale.metrics.jetty8.InstrumentedHandler;
import com.codahale.metrics.jetty8.InstrumentedQueuedThreadPool;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.eclipse.jetty.server.AbstractConnector;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.bio.SocketConnector;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class ServerBuilderUTest {

    private static final String MAIN_CONNECTOR_NAME = "main";
    Config typeSafeConfig = ConfigFactory.parseMap(ImmutableMap.<String, Object>builder()
            .put("port", "12345")
            .put("adminPort", "12345")
            .put("bindHost", "chezBibi")
            .put("useForwardedHeaders", true)
            .put("maxIdleTime", "12s")
            .put("lowResourcesMaxIdleTime", "11ms")
            .put("acceptorThreadPriorityOffset", 10)
            .put("acceptQueueSize", 65)
            .put("maxQueued", 0)
            .put("requestBufferSize", "64KiB")
            .put("requestHeaderBufferSize", "128KiB")
            .put("responseBufferSize", "12KiB")
            .put("responseHeaderBufferSize", "67KiB")
            .put("maxThreads", 42)
            .put("shutdownGracePeriod", 0)
            .put("path.query", "/query/*")
            .put("path.command", "/command/*")
            .put("path.event", "/event/*")
            .put("jmx.enabled", false)
            .put("acceptorThreads", 5)
            .put("minThreads", 5)
            .put("maxBufferCount", 10)
            .build());

    private ServerConfiguration config = new ServerConfiguration(typeSafeConfig);

    @Test
    public void minimal_builder_usage() {
        Server server = new ServerBuilder(config).build();

        assertNotNull(server);
    }

    @Test
    public void createMainConnector() {
        AbstractConnector connector = new ServerBuilder(config).createMainConnector();

        assertEquals(MAIN_CONNECTOR_NAME, connector.getName());
        assertEquals(config.getPort(), connector.getPort());
        assertEquals(config.getHost(), connector.getHost());
        assertEquals(config.getAcceptors(), connector.getAcceptors());
        assertEquals(config.isForwarded(), connector.isForwarded());
        assertEquals(config.getMaxIdleTime(), connector.getMaxIdleTime());
        assertEquals(config.getLowResourcesMaxIdleTime(), connector.getLowResourcesMaxIdleTime());
        assertEquals(config.getAcceptorPriorityOffset(), connector.getAcceptorPriorityOffset());
        assertEquals(config.getAcceptQueueSize(), connector.getAcceptQueueSize());
        assertEquals(config.getMaxBuffers(), connector.getMaxBuffers());
        assertEquals(config.getRequestBufferSize(), connector.getRequestBufferSize());
        assertEquals(config.getRequestHeaderSize(), connector.getRequestHeaderSize());
        assertEquals(config.getResponseBufferSize(), connector.getResponseBufferSize());
        assertEquals(config.getResponseHeaderSize(), connector.getResponseHeaderSize());
    }

    @Test
    public void createMainConnector_withMetricRegistry_returns_InstrumentedConnector() {
        AbstractConnector connector = new ServerBuilder(config)
                .withMetricRegistry(new MetricRegistry())
                .createMainConnector();

        assertTrue("Main connector must be an InstrumentedBlockingChannelConnector", connector instanceof InstrumentedBlockingChannelConnector);
    }

    @Test
    public void getConnectorByName_with_no_connectors_returns_absent() {
        assertFalse(ServerBuilder.getConnectorByName("pouet", new Server()).isPresent());
    }

    @Test
    public void getConnectorByName_with_no_matching_connectors_returns_absent() {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setName("no-name");
        server.addConnector(connector);

        assertFalse(ServerBuilder.getConnectorByName("pouet", server).isPresent());
    }

    @Test
    public void getConnectorByName_with_matching_connector_returns_the_connector() {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setName("pouet");
        server.addConnector(connector);

        assertSame(connector, ServerBuilder.getConnectorByName("pouet", server).get());
    }

    @Test(expected = IllegalStateException.class)
    public void getConnectorByName_with_multiple_matching_connectors_throws_IllegalStateException() {
        Server server = new Server();
        SocketConnector connector1 = new SocketConnector();
        connector1.setName("pouet");
        server.addConnector(connector1);
        SocketConnector connector2 = new SocketConnector();
        connector2.setName("pouet");
        server.addConnector(connector2);

        ServerBuilder.getConnectorByName("pouet", server);
    }

    @Test
    public void getPort_with_well_configured_server_returns_the_port() throws Exception {
        int port = 12345;

        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(port);
        connector.setName(ServerBuilder.MAIN_CONNECTOR_NAME);
        server.addConnector(connector);
        server.start();

        assertEquals(port, ServerBuilder.getPort(server));

        server.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void getPort_with_server_with_wrong_connector_throws_IllegalStateException() throws Exception {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(12345);
        connector.setName("bad-name");
        server.addConnector(connector);
        server.start();

        try {
            ServerBuilder.getPort(server);
        } finally {
            server.stop();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void getPort_with_not_started_server_throws_IllegalStateException() throws Exception {
        ServerBuilder.getPort(new Server());
    }

    @Test(expected = IllegalStateException.class)
    public void getPort_with_server_without_connectors_throws_IllegalStateException() throws Exception {
        Server server = new Server();
        server.start();

        try {
            ServerBuilder.getPort(server);
        } finally {
            server.stop();
        }
    }

    @Test
    public void getAdminPort_with_well_configured_server_returns_the_port() throws Exception {
        int port = 12345;

        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(port);
        connector.setName(ServerBuilder.ADMIN_CONNECTOR_NAME);
        server.addConnector(connector);
        server.start();

        assertEquals(port, ServerBuilder.getAdminPort(server));

        server.stop();
    }

    @Test(expected = IllegalStateException.class)
    public void getAdminPort_with_server_with_wrong_connector_throws_IllegalStateException() throws Exception {
        Server server = new Server();
        SocketConnector connector = new SocketConnector();
        connector.setPort(12345);
        connector.setName("bad-name");
        server.addConnector(connector);
        server.start();

        try {
            ServerBuilder.getAdminPort(server);
        } finally {
            server.stop();
        }
    }

    @Test(expected = IllegalStateException.class)
    public void getAdminPort_with_not_started_server_throws_IllegalStateException() throws Exception {
        ServerBuilder.getAdminPort(new Server());
    }

    @Test(expected = IllegalStateException.class)
    public void getAdminPort_with_server_without_connectors_throws_IllegalStateException() throws Exception {
        Server server = new Server();
        server.start();

        try {
            ServerBuilder.getAdminPort(server);
        } finally {
            server.stop();
        }
    }

    @Test
    public void build_with_metric_registry_uses_instrumented_connector() {
        // Given
        MetricRegistry registry = spy(new MetricRegistry());

        // When
        new ServerBuilder(config)
                .withMetricRegistry(registry)
                .build();

        // Then
        // Beurk, linked to InstrumentedBlockingChannelConnector constructor implementation. But it is tested...
        verify(registry, atLeastOnce()).timer(anyString());
    }

    @Test
    public void build_without_metric_registry_uses_a_QueuedThreadPool() {
        // Given + When
        ThreadPool threadPool = new ServerBuilder(config)
                .build().getThreadPool();

        // Then
        assertTrue("Thread pool must be a QueuedThreadPool", threadPool instanceof QueuedThreadPool);
    }

    @Test
    public void build_with_metric_registry_uses_an_InstrumentedQueuedThreadPool() {
        // Given + When
        ThreadPool threadPool = new ServerBuilder(config)
                .withMetricRegistry(new MetricRegistry())
                .build()
                .getThreadPool();

        // Then
        assertTrue("The server thread pool must be instrumented by Metrics", threadPool instanceof InstrumentedQueuedThreadPool);
    }

    @Test
    public void build_without_metric_registry_uses_non_instrumented_handler_for_main_handler() {
        // Given + When
        Server server = new ServerBuilder(config)
                .build();

        // Then
        // Reverse strategy to test if the main handler "/" is not instrumented:
        // 1. get all instrumented handlers
        // 2. check that the main handler is not in these handlers
        List<Handler> instrumentedHandlers = Arrays.asList(server.getChildHandlersByClass(InstrumentedHandler.class));
        assertFalse("Main handler must not be instrumented", containsMainHandler(instrumentedHandlers));
    }

    @Test
    public void build_with_metric_registry_uses_instrumented_handler_for_main_handler() {
        // Given + When
        Server server = new ServerBuilder(config)
                .withMetricRegistry(new MetricRegistry())
                .build();

        // Then
        // Reverse strategy to test if the main handler "/" is instrumented:
        // 1. get all instrumented handlers
        // 2. check that the main handler is in these handlers
        List<Handler> instrumentedHandlers = Arrays.asList(server.getChildHandlersByClass(InstrumentedHandler.class));
        assertTrue("Main handler must be instrumented", containsMainHandler(instrumentedHandlers));
    }

    private boolean containsMainHandler(List<Handler> instrumentedHandlers) {
        return Iterables.any(instrumentedHandlers, new Predicate<Handler>() {
            @Override
            public boolean apply(Handler input) {
                if (input == null) {
                    return false;
                }
                InstrumentedHandler instrumentedHandler = (InstrumentedHandler) input;
                if (instrumentedHandler.getHandler() instanceof ContextHandler) {
                    ContextHandler contextHandler = (ContextHandler) instrumentedHandler.getHandler();
                    String[] connectorNames = contextHandler.getConnectorNames();
                    if (Arrays.asList(connectorNames).contains(MAIN_CONNECTOR_NAME)) {
                        return contextHandler.getContextPath().equals("/");
                    }
                }
                return false;
            }
        });
    }
}
