// ============================================================================
//                 KASPER - Kasper is the treasure keeper
//    www.viadeo.com - mobile.viadeo.com - api.viadeo.com - dev.viadeo.com
//
//           Viadeo Framework for effective CQRS/DDD architecture
// ============================================================================
package com.viadeo.kasper.exposition.http.jetty;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.common.collect.ImmutableMap;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.core.DefaultResourceConfig;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigValueFactory;
import com.viadeo.kasper.exposition.http.HttpCommandExposer;
import com.viadeo.kasper.exposition.http.HttpEventExposer;
import com.viadeo.kasper.exposition.http.HttpQueryExposer;
import org.eclipse.jetty.server.Server;
import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

/**
 * Test the usability of a Jetty HTTP server built by the [@link com.viadeo.platform.http.JettyServerBuilder}
 */
public class ServerBuilderITest {

    private Config baseConf = ConfigFactory.parseMap(
            ImmutableMap.<String, Object>builder()
                    .put("port", 0)
                    .put("adminPort", 0)
                    .put("bindHost", "0.0.0.0")
                    .put("acceptorThreads", 1)
                    .put("useForwardedHeaders", true)
                    .put("maxIdleTime", "200s")
                    .put("lowResourcesMaxIdleTime", "5s")
                    .put("acceptorThreadPriorityOffset", 0)
                    .put("acceptQueueSize", 5)
                    .put("maxBufferCount", 1024)
                    .put("requestBufferSize", "16KiB")
                    .put("requestHeaderBufferSize", "16KiB")
                    .put("responseBufferSize", "16KiB")
                    .put("responseHeaderBufferSize", "16KiB")
                    .put("minThreads", 5)
                    .put("maxThreads", 10)
                    .put("shutdownGracePeriod", "0s")
                    .put("jmx.enabled", false)
                    .build()
    );

    private Server server;

    @After
    public void stopServer() throws Exception {
        if (server != null) {
            server.stop();
        }
    }

    /**
     * This test uses a {@link org.mockito.Mock} of {@link com.viadeo.kasper.exposition.http.HttpQueryExposer} because it
     * only checks that the builder creates a server listening on "/query/*".
     */
    @Test
    public void queryExposition() throws Exception {
        // Given
        Config queryConf = baseConf.withValue("path.query", ConfigValueFactory.fromAnyRef("/query/*"));
        ServerConfiguration config = new ServerConfiguration(queryConf);

        server = new ServerBuilder(config)
                .withQueryExposer(mock(HttpQueryExposer.class))
                .build();
        server.start();

        int port = ServerBuilder.getPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/query/nawak").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    /**
     * This test uses a {@link org.mockito.Mock} of {@link com.viadeo.kasper.exposition.http.HttpCommandExposer} because it
     * only checks that the builder creates a server listening on "/command/*".
     */
    @Test
    public void commandExposition() throws Exception {
        // Given
        Config commandConf = baseConf.withValue("path.command", ConfigValueFactory.fromAnyRef("/command/*"));
        ServerConfiguration config = new ServerConfiguration(commandConf);

        server = new ServerBuilder(config)
                .withCommandExposer(mock(HttpCommandExposer.class))
                .build();
        server.start();

        int port = ServerBuilder.getPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/command/nawak").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    /**
     * This test uses a {@link org.mockito.Mock} of {@link com.viadeo.kasper.exposition.http.HttpCommandExposer} because it
     * only checks that the builder creates a server listening on "/command/*".
     */
    @Test
    public void eventExposition() throws Exception {
        // Given
        Config commandConf = baseConf.withValue("path.event", ConfigValueFactory.fromAnyRef("/event/*"));
        ServerConfiguration config = new ServerConfiguration(commandConf);

        server = new ServerBuilder(config)
                .withEventExposer(mock(HttpEventExposer.class))
                .build();
        server.start();

        int port = ServerBuilder.getPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/event/nawak").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void jaxRsExposition() throws Exception {
        // Given
        Application application = new DefaultResourceConfig(FunkyResource.class);

        server = new ServerBuilder(new ServerConfiguration(baseConf))
                .withJaxRs(application)
                .build();
        server.start();

        int port = ServerBuilder.getPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/funkyResource").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Path("/funkyResource")
    public static class FunkyResource {
        @GET
        public String get() {
            return "do the funk!";
        }
    }

    @Test
    public void staticDocExposition() throws Exception {
        // Given
        server = new ServerBuilder(new ServerConfiguration(baseConf))
                .build();
        server.start();

        int port = ServerBuilder.getPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/doc/index.html").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void pingExposition() throws Exception {
        // Given
        server = new ServerBuilder(new ServerConfiguration(baseConf))
                .build();
        server.start();

        int port = ServerBuilder.getAdminPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/ping").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("pong\n", response.getEntity(String.class));
    }

    @Test
    public void adminExposition() throws Exception {
        // Given
        server = new ServerBuilder(new ServerConfiguration(baseConf))
                .build();
        server.start();

        int port = ServerBuilder.getAdminPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
    }

    @Test
    public void healthCheckExposition() throws Exception {
        // Given
        HealthCheckRegistry registry = new HealthCheckRegistry();
        registry.register("pouik", new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });

        server = new ServerBuilder(new ServerConfiguration(baseConf))
                .withHealthCheckRegistry(registry)
                .build();
        server.start();

        int port = ServerBuilder.getAdminPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/healthcheck").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
        assertThat(response.getEntity(String.class), StringContains.containsString("pouik"));
    }

    @Test
    @Ignore
    /**
     * This test must be executed with specific JAVA_OPTS:
     * -Dcom.sun.management.jmxremote
     * -Dcom.sun.management.jmxremote.port=1099
     * -Dcom.sun.management.jmxremote.ssl=false
     * -Dcom.sun.management.jmxremote.authenticate=false
     */
    public void jmx() throws Exception {
        // Given
        int jmxPort = 1099;
        String url = "service:jmx:rmi:///jndi/rmi://localhost:" + jmxPort + "/jmxrmi";
        JMXServiceURL serviceUrl = new JMXServiceURL(url);

        Config jmxConf = baseConf.withValue("jmx.enabled", ConfigValueFactory.fromAnyRef(true));
        server = new ServerBuilder(new ServerConfiguration(jmxConf))
                .build();
        server.start();

        // When
        JMXConnector connector = JMXConnectorFactory.connect(serviceUrl);

        // Then
        assertNotNull(connector);
    }

    @Test
    public void jettyPingExposition() throws Exception {
        // Given
        server = new ServerBuilder(new ServerConfiguration(baseConf))
                .build();
        server.start();

        int port = ServerBuilder.getAdminPort(server);

        // When
        ClientResponse response = Client.create().resource("http://localhost:" + port).path("/jetty").get(ClientResponse.class);

        // Then
        assertEquals(200, response.getStatus());
        assertEquals("Jetty is in da place!\n", response.getEntity(String.class));
    }
}
